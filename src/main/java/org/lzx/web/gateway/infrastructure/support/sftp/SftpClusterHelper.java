package org.lzx.web.gateway.infrastructure.support.sftp;

import org.lzx.web.gateway.infrastructure.util.SftpUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SFTP集群操作工具类
 *
 * @author LZx
 * @since 2020/12/31
 */
@Slf4j
@Component
@EnableConfigurationProperties(SftpUploadProperties.class)
public class SftpClusterHelper implements DisposableBean {

    private static final int MB_BYTES = 1024 * 1024;

    private final SftpUploadProperties sftpUploadProperties;

    /**
     * Key为SFTP组的名字，Value为该SFTP组所有SFTP实例的Session集合
     */
    private final Map<String, List<Session>> sftpGroupSessions;

    public SftpClusterHelper(SftpUploadProperties sftpUploadProperties) {
        log.info("创建各SFTP组的会话组-[0]-开始");
        this.sftpUploadProperties = sftpUploadProperties;
        this.sftpGroupSessions = sftpUploadProperties.getGroups().stream()
                .collect(Collectors.toMap(
                        SftpUploadProperties.SftpGroup::getId,
                        group -> group.getServers().stream()
                                .map(server -> {
                                    try {
                                        return SftpUtils.session(server);
                                    } catch (JSchException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .collect(Collectors.toList())
                ));
        log.info("创建各SFTP组的会话组-[1]-成功");
    }

    /**
     * 上传文件
     *
     * @param sftpGroupId 上传的SFTP组的ID
     * @param fileGroupId 上传SFTP组中预定义的文件组的ID
     * @param srcFile     上传文件的本地绝对路径
     * @return 访问地址
     */
    public String upload(String sftpGroupId, String fileGroupId, File srcFile) {
        Objects.requireNonNull(sftpGroupId, "参数[sftpGroupId]不能为空");
        Objects.requireNonNull(fileGroupId, "参数[fileGroupId]不能为空");
        Objects.requireNonNull(srcFile, "参数[srcFile]不能为空");
        SftpUtils.logLevel(this.sftpUploadProperties.getLogLevel());

        SftpUploadProperties.SftpGroup sftpGroup = findSftpGroup(sftpGroupId)
                .orElseThrow(() -> new IllegalArgumentException("未找到名为[" + sftpGroupId + "]的SFTP组"));
        String accessPrefix = sftpGroup.getAccessPrefix();
        int timeoutMillis = sftpGroup.getConnectionTimeoutMillis();

        SftpUploadProperties.FileGroup fileGroup = findFileGroup(sftpGroup, fileGroupId)
                .orElseThrow(() -> new IllegalArgumentException("未在[" + sftpGroupId + "]中找到名为[" + fileGroupId + "]的文件组"));

        String extension = FilenameUtils.getExtension(srcFile.getName());
        if (!StringUtils.hasText(extension) || !extension.matches(fileGroup.getAllowExtensionsRegex())) {
            throw new IllegalArgumentException("[" + extension + "]为非允许的格式");
        }
        long allowMaxMegaBytes = fileGroup.getMaxMegaBytes();
        if (srcFile.length() > allowMaxMegaBytes * MB_BYTES) {
            throw new IllegalArgumentException("上传的文件超过最大长度[" + allowMaxMegaBytes + "]MB");
        }

        String srcFileAbsolutePath = srcFile.getAbsolutePath();
        String remoteAbstractDir = fileGroup.getAbstractDir();

        sftpGroupSessions.get(sftpGroupId).parallelStream().forEach(session -> {
            try {
                SftpUtils.uploadFile(session, srcFileAbsolutePath, remoteAbstractDir, fileGroup.isAllowAutoMkdir(), timeoutMillis);
            } catch (JSchException | SftpException e) {
                throw new RuntimeException(e);
            }
        });
        return accessPrefix + fileGroup.getAbstractDir() + srcFile.getName();
    }

    /**
     * 销毁已创建的会话，尤其是RefreshScope类型的组件，动态新建对象时，SFTP链接不关闭极容易达到一般SFTP默认个位数的连接数上限
     */
    @Override
    public void destroy() {
        this.sftpGroupSessions.forEach((sftpGroupName, groupSessions) -> {
            log.info("开始销毁SFTP组[{}]的所有已创建会话", sftpGroupName);
            groupSessions.forEach(session -> {
                String host = session.getHost();
                log.info("SFTP组[{}]关闭与主机[{}]的会话", sftpGroupName, host);
            });
        });
    }

    /**
     * 获取SFTP组的配置
     *
     * @param sftpGroupId SFTP组的ID
     * @return SFTP组
     */
    private Optional<SftpUploadProperties.SftpGroup> findSftpGroup(String sftpGroupId) {
        return sftpUploadProperties.getGroups().stream()
                .filter(sftpGroup -> sftpGroup.getId().equals(sftpGroupId))
                .findFirst();
    }

    /**
     * 从SFTP组获取文件组的配置
     *
     * @param sftpGroup   SFTP组
     * @param fileGroupId 文件组的ID
     * @return SFTP组中的文件组
     */
    private Optional<SftpUploadProperties.FileGroup> findFileGroup(SftpUploadProperties.SftpGroup sftpGroup, String fileGroupId) {
        return sftpGroup.getFileGroups().stream()
                .filter(fileGroup -> fileGroup.getId().equals(fileGroupId))
                .findFirst();
    }

}
