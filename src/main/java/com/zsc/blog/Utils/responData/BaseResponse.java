package com.zsc.blog.Utils.responData;

import lombok.Data;

/**
 * @program: demo
 * @description: mff
 * @author: Mr.Wang
 * @create: 2020-07-24 17:24
 **/
@Data
public class BaseResponse {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    protected BaseResponse() {}

    protected BaseResponse(CodeEnum code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }
}
