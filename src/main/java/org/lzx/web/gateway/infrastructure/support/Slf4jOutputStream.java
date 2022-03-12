package org.lzx.web.gateway.infrastructure.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Slf4j日志输出流
 *
 * @author LZx
 * @since 2021/1/11
 */
public class Slf4jOutputStream extends OutputStream {

    /**
     * 默认缓存大小
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private final Logger logger;

    /**
     * <i style="color:red;">不能并发或者并行使用</i>
     *
     * @param name 日志名
     */
    public Slf4jOutputStream(String name) {
        logger = LoggerFactory.getLogger(name);
    }

    /**
     * 缓存
     */
    private byte[] buf = new byte[DEFAULT_BUFFER_SIZE];

    /**
     * 当前最多缓存字节数，每当缓存已满，该值增加{@link this#DEFAULT_BUFFER_SIZE}
     */
    private int limit = DEFAULT_BUFFER_SIZE;

    /**
     * 已读取字节数
     */
    private int count;

    @Override
    public void write(int b) {
        if (count == limit) {
            limit += DEFAULT_BUFFER_SIZE;
            byte[] tempBuf = new byte[limit];
            System.arraycopy(buf, 0, tempBuf, 0, count);
            buf = tempBuf;
        }
        buf[count] = (byte) b;
        count++;
    }

    @Override
    public void flush() {
        byte[] resultBuf = new byte[count];
        System.arraycopy(buf, 0, resultBuf, 0, count);
        String message = new String(resultBuf, StandardCharsets.UTF_8);
        logger.info("[{}]-[{}]", logger.getName(), message.trim());
        reset();
    }

    /**
     * 重置缓存和已读字节数
     */
    private void reset() {
        buf = new byte[DEFAULT_BUFFER_SIZE];
        count = 0;
    }

}
