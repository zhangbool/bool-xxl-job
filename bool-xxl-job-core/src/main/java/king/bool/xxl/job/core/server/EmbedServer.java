package king.bool.xxl.job.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import king.bool.xxl.job.core.biz.ExecutorBiz;
import king.bool.xxl.job.core.biz.impl.ExecutorBizImpl;
import king.bool.xxl.job.core.biz.model.*;
import king.bool.xxl.job.core.thread.ExecutorRegistryThread;
import king.bool.xxl.job.core.util.GsonTool;
import king.bool.xxl.job.core.util.ThrowableUtil;
import king.bool.xxl.job.core.util.XxlJobRemotingUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author : 不二
 * @date : 2023/8/24-11:25
 * @desc : netty实现的http服务
 *        #todo: 为啥要用netty呢, 直接使用springboot的接口不行吗
 **/
@Slf4j
public class EmbedServer {

    private ExecutorBiz executorBiz;

    private Thread thread;

    public void start(final String address, final int port, final String appname, final String accessToken) {

        // #todo: 擦, 为啥Impl是放在这里的??? 这里是
        executorBiz = new ExecutorBizImpl();

        // 启动一个线程, 跑内嵌的服务器, 这个服务器是使用netty来做的
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // param
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                // #todo-01: 这个地方应该只需要调用一次启动个内嵌的服务器吧, 可以不用线程池吗
                // 这个线程池是给内置服务器中的调度任务用的, 而不是用来创建服务器的
                ThreadPoolExecutor bizThreadPool = new ThreadPoolExecutor(
                        0,
                        200,
                        60L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(2000),
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "xxl-job, EmbedServer bizThreadPool-" + r.hashCode());
                            }
                        },
                        new RejectedExecutionHandler() {
                            @Override
                            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                                throw new RuntimeException("xxl-job, EmbedServer bizThreadPool is EXHAUSTED!");
                            }
                        });

                try {

                    /*
                    1. channel 代表了一个socket.
                    2. ChannelPipeline 就是一个“羊肉串”，这个“羊肉串”里边的每一块羊肉就是一个 handler.
                       handler分为两种，inbound handler,outbound handler 。顾名思义，分别处理 流入，流出。
                    3. HttpServerCodec 是 http消息的编解码器。
                    4. HttpObjectAggregator是Http消息聚合器，Aggregator这个单次就是“聚合，聚集”的意思。
                       http消息在传输的过程中可能是一片片的消息片端，所以当服务器接收到的是一片片的时候，就需要HttpObjectAggregator来把它们聚合起来。
                    5. 接收到请求之后，你要做什么，准备怎么做，就在HttpRequestHandler中实现。
                     */
                    // start server
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) throws Exception {
                                    log.info("服务器启动中.........");
                                    channel.pipeline()
                                            // #todo-1: 这些都是干啥的干啥的???
                                            // beat 3N, close if idle
                                            .addLast(new IdleStateHandler(0, 0, 30 * 3, TimeUnit.SECONDS))
                                            // http编解码
                                            .addLast(new HttpServerCodec())
                                            // merge request & reponse to FULL
                                            // http 消息聚合器, 5*1024*1024为接收的最大contentlength
                                            .addLast(new HttpObjectAggregator(5 * 1024 * 1024))
                                            // 在这里创建内置服务器类
                                            // 请求处理器
                                            // .addLast(new HttpRequestHandler());
                                            .addLast((ChannelHandler) new EmbedHttpServerHandler(executorBiz, accessToken, bizThreadPool));
                                }
                            })
                            //开启TCP底层心跳机制
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    // bind
                    ChannelFuture future = bootstrap.bind(port).sync();
                    log.info(">>>>>>>>>>> xxl-job remoting server start success, nettype = {}, port = {}", EmbedServer.class, port);

                    // 服务器创建好了, 就可以像admin进行注册了, 因为注册的时候需要内嵌服务器的地址
                    // 这个是自动注册, 然后同时在:
                    // 这里进行注册吗???
                    // start registry
                    startRegistry(appname, address);

                    // wait util stop
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.info(">>>>>>>>>>> xxl-job remoting server stop.");
                } catch (Exception e) {
                    log.error(">>>>>>>>>>> xxl-job remoting server error.", e);
                } finally {
                    // stop
                    try {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });

        // 守护线程, 守护用户线程, 比如说main线程或者自己建立的其他用户线程(非demo线程)
        // 如果应用中没有来其他的用户线程, 即应用中的所有线程都是守护线程, 这个就会被终止掉, 单纯的守护线程不会存在
        // daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() throws Exception {
        // destroy server thread
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }

        // stop registry
        stopRegistry();
        log.info(">>>>>>>>>>> xxl-job remoting server destroy success.");
    }


    // ---------------------- registry ----------------------
    /**
     * netty_http
     * <p>
     * Copy from : https://github.com/xuxueli/xxl-rpc
     * 消息流入如何处理
     *
     * @author xuxueli 2015-11-24 22:25:15
     */
    public static class EmbedHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        private ExecutorBiz executorBiz;
        private String accessToken;
        private ThreadPoolExecutor bizThreadPool;

        public EmbedHttpServerHandler(ExecutorBiz executorBiz, String accessToken, ThreadPoolExecutor bizThreadPool) {
            this.executorBiz = executorBiz;
            this.accessToken = accessToken;
            this.bizThreadPool = bizThreadPool;
        }

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            // request parse
            // final byte[] requestBytes = ByteBufUtil.getBytes(msg.content());
            // byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            String requestData = msg.content().toString(CharsetUtil.UTF_8);
            String uri = msg.uri();
            HttpMethod httpMethod = msg.method();
            boolean keepAlive = HttpUtil.isKeepAlive(msg);
            String accessTokenReq = msg.headers().get(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN);

            // 这里演示一下netty服务器的作用
            log.info("uri是: {}, 请求参数是: {}", uri, requestData);
            if (uri.equals("/")) {
                String msg01 = "<html><head><title>test</title></head><body>ivanl001 is the king of world! 你请求uri为：" + uri+"</body></html>";
                // 创建http响应
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(msg01, CharsetUtil.UTF_8));
                // 设置头信息
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                // 将html write到客户端
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }

            // invoke
            bizThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // do invoke
                    Object responseObj = process(httpMethod, uri, requestData, accessTokenReq);
                    // to json
                    String responseJson = GsonTool.toJson(responseObj);
                    // write response
                    writeResponse(ctx, keepAlive, responseJson);
                }
            });
        }

        private Object process(HttpMethod httpMethod, String uri, String requestData, String accessTokenReq) {

            // valid
            if (HttpMethod.POST != httpMethod) {
                return new ResultModel(ResultModel.FAIL_CODE, "invalid request, HttpMethod not support.");
            }
            if (uri == null || uri.trim().length() == 0) {
                return new ResultModel(ResultModel.FAIL_CODE, "invalid request, uri-mapping empty.");
            }
            if (accessToken != null
                    && accessToken.trim().length() > 0
                    && !accessToken.equals(accessTokenReq)) {
                return new ResultModel(ResultModel.FAIL_CODE, "The access token is wrong.");
            }

            // services mapping
            try {
                switch (uri) {
                    case "/beat":
                        return executorBiz.beat();
                    case "/idleBeat":
                        IdleBeatParam idleBeatParam = GsonTool.fromJson(requestData, IdleBeatParam.class);
                        return executorBiz.idleBeat(idleBeatParam);
                    case "/run":
                        log.info("～～～～～～～请求到executor啦～～～～～～～");
                        TriggerParam triggerParam = GsonTool.fromJson(requestData, TriggerParam.class);
                        return executorBiz.run(triggerParam);
                    case "/kill":
                        KillParam killParam = GsonTool.fromJson(requestData, KillParam.class);
                        return executorBiz.kill(killParam);
                    case "/log":
                        LogParam logParam = GsonTool.fromJson(requestData, LogParam.class);
                        return executorBiz.log(logParam);
                    default:
                        return new ResultModel(ResultModel.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return new ResultModel(ResultModel.FAIL_CODE, "request error:" + ThrowableUtil.toString(e));
            }
        }

        /**
         * write response
         */
        private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, String responseJson) {
            // write response
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseJson, CharsetUtil.UTF_8));   //  Unpooled.wrappedBuffer(responseJson)
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");       // HttpHeaderValues.TEXT_PLAIN.toString()
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.writeAndFlush(response);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error(">>>>>>>>>>> xxl-job provider netty_http server caught exception", cause);
            ctx.close();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.channel().close();      // beat 3N, close if idle
                log.debug(">>>>>>>>>>> xxl-job provider netty_http server close an idle channel.");
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    // ---------------------- registry ----------------------
    public void startRegistry(final String appname, final String address) {
        log.info("startRegistry开始注册咯-------{}-{}", appname, address);

        // start registry
        ExecutorRegistryThread.getInstance().start(appname, address);
    }

    public void stopRegistry() {
        // stop registry
        ExecutorRegistryThread.getInstance().toStop();
    }

}
