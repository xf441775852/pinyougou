package entity;

import java.io.Serializable;

/**
 * 返回结果封装
 */
public class Result implements Serializable {
    //是否成功
    private boolean isSuccess;
    //返回的信息
    private String message;

    public Result() {
    }

    public Result(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
