package king.bool.xxl.job.core.thread;

import king.bool.xxl.job.core.log.XxlJobFileAppender;
import king.bool.xxl.job.core.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author : 不二
 * @date : 2023/8/24-14:17
 * @desc :
 **/
@Slf4j
public class JobLogFileCleanThread {

    private static JobLogFileCleanThread instance = new JobLogFileCleanThread();
    public static JobLogFileCleanThread getInstance(){
        return instance;
    }

    private Thread localThread;
    private volatile boolean toStop = false;
    public void start(final long logRetentionDays){

        // limit min value
        if (logRetentionDays < 3 ) {
            return;
        }

        localThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // clean log dir, over logRetentionDays
                        File[] childDirs = new File(XxlJobFileAppender.getLogPath()).listFiles();
                        if (childDirs!=null && childDirs.length>0) {

                            // today
                            Calendar todayCal = Calendar.getInstance();
                            todayCal.set(Calendar.HOUR_OF_DAY,0);
                            todayCal.set(Calendar.MINUTE,0);
                            todayCal.set(Calendar.SECOND,0);
                            todayCal.set(Calendar.MILLISECOND,0);

                            Date todayDate = todayCal.getTime();

                            for (File childFile: childDirs) {

                                // valid
                                if (!childFile.isDirectory()) {
                                    continue;
                                }
                                if (childFile.getName().indexOf("-") == -1) {
                                    continue;
                                }

                                // file create date
                                Date logFileCreateDate = null;
                                try {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    logFileCreateDate = simpleDateFormat.parse(childFile.getName());
                                } catch (ParseException e) {
                                    log.error(e.getMessage(), e);
                                }
                                if (logFileCreateDate == null) {
                                    continue;
                                }

                                if ((todayDate.getTime()-logFileCreateDate.getTime()) >= logRetentionDays * (24 * 60 * 60 * 1000) ) {
                                    FileUtil.deleteRecursively(childFile);
                                }

                            }
                        }

                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(e.getMessage(), e);
                        }

                    }

                    try {
                        TimeUnit.DAYS.sleep(1);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
                log.info(">>>>>>>>>>> xxl-job, executor JobLogFileCleanThread thread destroy.");

            }
        });
        localThread.setDaemon(true);
        localThread.setName("xxl-job, executor JobLogFileCleanThread");
        localThread.start();
    }

    public void toStop() {
        toStop = true;

        if (localThread == null) {
            return;
        }

        // interrupt and wait
        localThread.interrupt();
        try {
            localThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}
