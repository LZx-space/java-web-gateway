package org.lzx.javaweb.gateway.infrastructure.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LZx
 * @since 2021/12/12
 */
@Data
public class Response<T> implements Serializable {

    private static final String SUCCESS_STATUS = "00000";

    private static final String SUCCESS_REASON = "成功";

    private String status;

    private String reason;

    private T data;

    /**
     * 成功
     *
     * @return 简单的返回成功
     */
    public static Response<Void> ok() {
        Response<Void> response = new Response<>();
        response.setStatus(SUCCESS_STATUS);
        response.setReason(SUCCESS_REASON);
        return response;
    }

    /**
     * 成功
     *
     * @param data 返回成功状态并添加相关数据
     * @param <T>  返回的数据的类型
     * @return 约定的返回结构的数据，并且已添加业务返回数据
     */
    public static <T> Response<T> ok(T data) {
        Response<T> response = new Response<>();
        response.setStatus(SUCCESS_STATUS);
        response.setReason(SUCCESS_REASON);
        response.setData(data);
        return response;
    }

    /**
     * 处理成功
     *
     * @param status 状态
     * @param reason 原因
     * @return 约定的返回结构的数据
     */
    public static Response<Void> fail(String status, String reason) {
        Response<Void> response = new Response<>();
        response.setStatus(status);
        response.setReason(reason);
        return response;
    }

}
