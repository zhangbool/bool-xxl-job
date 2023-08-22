package king.bool.xxl.job.core.biz.model;


import lombok.Data;

/**
 * #Author : 不二
 * #Date   : 10/10/2019-9:54 AM
 * #Desc   : 返回结果模型
 **/
@Data
public class ResultModel {

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;

    //存放返回状态
    private int code;
    private String status;
    //存放状态信息，如果失败，就是失败信息
    private String msg;
    //这里是返回的结果
    private Object content;

    //如果出错 那么状态和数据直接设置为error和null，消息可自定义
    public ResultModel setErrorResult(String msg) {
        this.code = FAIL_CODE;
        this.status = "error";
        this.msg = msg;
        this.content = null;
        return this;
    }
    public ResultModel setOKResult(Object data) {
        this.code = SUCCESS_CODE;
        this.status = "ok";
        this.msg = "success";
        this.content = data;
        return this;
    }

    public static final ResultModel SUCCESS = new ResultModel(SUCCESS_CODE, null);
    public static final ResultModel FAIL = new ResultModel(FAIL_CODE, null);

    public ResultModel() {
    }

    public ResultModel(int code, Object data) {
        this.code = SUCCESS_CODE;
        if (code == 200) {
            this.status = "ok";
            this.msg = "success";
        } else {
            this.status = "error";
            this.msg = "error";
        }
        this.content = data;
    }


}
