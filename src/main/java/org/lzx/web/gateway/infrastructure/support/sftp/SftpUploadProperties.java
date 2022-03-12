package org.lzx.web.gateway.infrastructure.support.sftp;

import org.lzx.web.gateway.infrastructure.util.SftpUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SFTP服务器配置，因为并发问题，不可热修改
 *
 * @author LZx
 * @since 2021/1/18
 */
@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = SftpUploadProperties.SFTP_CONFIG_PREFIX)
public class SftpUploadProperties implements InitializingBean {

    static final String SFTP_CONFIG_PREFIX = "sftp";

    private static final String PATH_SEPARATOR = "/";

    private static final String SFTP_GROUP_CONFIG_PREFIX = SFTP_CONFIG_PREFIX + ".groups";

    /**
     * FileGroup.name值必须符合该Regex
     */
    private static final Pattern FILE_GROUP_PATTERN = Pattern.compile("[a-zA-Z_]+");

    /**
     * FileGroup.abstractDir上传到SFTP基本目录的相对目录，这个值必须符合该Regex
     */
    private static final Pattern UPLOAD_ABSTRACT_DIR_PATTERN = Pattern.compile("sftp/[a-zA-Z_]+/");

    /**
     * 日志级别，默认ERROR
     */
    private SftpUtils.SftpLogger.LogLevel logLevel = SftpUtils.SftpLogger.LogLevel.ERROR;

    /**
     * SFTP集群信息
     */
    private List<SftpGroup> groups = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        int sftpGroupSize = groups.size();
        if (sftpGroupSize == 0) {
            log.warn(SFTP_CONFIG_PREFIX + ".groups未配置任何SFTP组，其它所有配置项将忽略");
            return;
        }
        groups.forEach(sftpGroup -> {
            String sftpGroupId = sftpGroup.id;
            if (!sftpGroup.getAccessPrefix().endsWith(PATH_SEPARATOR)) {
                throw new IllegalArgumentException(SFTP_GROUP_CONFIG_PREFIX + "." + sftpGroupId +
                        ".access-prefix配置的值必须以/结束");
            }
            List<FileGroup> fileGroups = sftpGroup.fileGroups;
            if (fileGroups.size() == 0) {
                throw new IllegalArgumentException(SFTP_GROUP_CONFIG_PREFIX + "." + sftpGroupId +
                        "至少需要一个上传文件组配置");
            }
            fileGroups.forEach(config -> {
                String fileGroupId = config.getId();
                if (!FILE_GROUP_PATTERN.matcher(fileGroupId).matches()) {
                    String message = String.format("SFTP组[%s]的文件组[%s]命名不符合Regex:%s", sftpGroupId,
                            fileGroupId, FILE_GROUP_PATTERN.pattern());
                    throw new IllegalArgumentException(message);
                }
                String abstractDir = config.getAbstractDir();
                if (!UPLOAD_ABSTRACT_DIR_PATTERN.matcher(abstractDir).matches()) {
                    String message = String.format("SFTP组[%s]的文件组[%s]的上传目录[%s]路径不符合Regex:%s", sftpGroupId,
                            fileGroupId, abstractDir, UPLOAD_ABSTRACT_DIR_PATTERN.pattern());
                    throw new IllegalArgumentException(message);
                }
                String allowExtensionsRegex = config.getAllowExtensionsRegex();
                if (!StringUtils.hasText(allowExtensionsRegex)) {
                    String message = String.format("SFTP组[%s]的文件组[%s]的允许文件格式[%s]Regex不能为空", sftpGroupId,
                            fileGroupId, allowExtensionsRegex);
                    throw new IllegalArgumentException(message);
                }
            });
        });
    }

    /**
     * sftp服务器集群
     */
    @Getter
    @Setter
    public static class SftpGroup {

        /**
         * SFTP的ID
         */
        private String id;

        /**
         * 外网访问时需要的前缀
         */
        private String accessPrefix = "/";

        /**
         * 连接超时时间，包括创建Session和Channel
         */
        private int connectionTimeoutMillis = 1000;

        /**
         * SFTP服务器连接用属性
         */
        private List<SftpUtils.Server> servers = new ArrayList<>();

        /**
         * 上传组配置
         */
        private List<FileGroup> fileGroups = new ArrayList<>();

    }

    /**
     * 上传组为相同上传内容、目的地和其验证等操作的抽象
     */
    @Data
    public static class FileGroup {

        /**
         * 文件组的ID
         */
        private String id;

        /**
         * 相对于SFTP的基础目录，文件上传的相对目录
         */
        private String abstractDir;

        /**
         * 如果sftpBaseDir + abstractDir不存在，都是允许自动创建文件夹
         */
        private boolean allowAutoMkdir;

        /**
         * 允许上传的文件格式的regex
         */
        private String allowExtensionsRegex = ".*";

        /**
         * 上传默认允许的最大字节数（1MB）
         */
        private long maxMegaBytes = 1;

    }

}
