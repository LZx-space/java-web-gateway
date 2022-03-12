package org.lzx.javaweb.gateway.infrastructure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

/**
 * @author LZx
 */
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final ObjectReader OBJECT_READER = OBJECT_MAPPER.reader();

    private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer();

    /**
     * 将对象序列化为字符串
     *
     * @param obj 需被序列化的对象
     * @return 字符串
     * @throws JsonProcessingException 异常
     */
    public static String write(Object obj) throws JsonProcessingException {
        return OBJECT_WRITER.writeValueAsString(obj);
    }

    /**
     * 将字符串反序列化为对象
     *
     * @param json  字符串
     * @param clazz 对象的类类型
     * @param <T>   类型泛型
     * @return 对象
     * @throws IOException 反序列异常
     */
    public static <T> T read(String json, Class<T> clazz) throws IOException {
        return OBJECT_READER.readValue(json, clazz);
    }

    /**
     * JSON字符串转变为对象，对于Collection或者Map等包含泛型的Object需要指定其元素的具体类型
     *
     * @param json    JSON字符串
     * @param typeRef 转变的目标类型
     * @return 对象
     * @throws JsonMappingException 异常
     * @throws IOException          异常
     */
    public static <T> T read(String json, TypeReference<T> typeRef) throws JsonMappingException, IOException {
        return OBJECT_MAPPER.readValue(json, typeRef);
    }

}
