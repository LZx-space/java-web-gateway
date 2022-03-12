package org.lzx.javaweb.gateway.infrastructure.support;

import org.lzx.javaweb.gateway.infrastructure.model.Response;

import java.util.function.Function;

/**
 * 返回数据包装器，临时方案，所有应用应该有个统一的返回处理的数据解构和验证API
 *
 * @author LZx
 * @since 2021/1/7
 */
public class ReturnValueMapper {

    /**
     * 方法成功时原数据转换Fn
     *
     * @param <T> 原方法返回值的类型
     * @return 方法成功时原数据转换Fn
     */
    public static <T> Function<? super T, Response<T>> successMapper() {
        return Response::ok;
    }

}
