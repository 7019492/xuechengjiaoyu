package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常
 *
 * @author WangPan
 * @date 2020/7/8 16:53
 */
public class CustomException extends RuntimeException {
    // 错误代码
    private ResultCode resultCode;

    CustomException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
