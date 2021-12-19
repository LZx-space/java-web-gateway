package org.lzx.web.gateway.infrastructure.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LZx
 * @since 2021/12/12
 */
@Data
public class Response<T> implements Serializable {

    private String status;

    private String reason;

    private T data;

    public static <T> Response<T> ok() {
        Response<T> response = new Response<>();
        response.setStatus("00000");
        response.setReason("成功");
        return response;
    }

    public static <T> Response<T> ok(T data) {
        Response<T> response = new Response<>();
        response.setStatus("00000");
        response.setReason("成功");
        response.setData(data);
        return response;
    }

    public static Response<Void> fail(String status, String reason) {
        Response<Void> response = new Response<>();
        response.setStatus(status);
        response.setReason(reason);
        return response;
    }

}
