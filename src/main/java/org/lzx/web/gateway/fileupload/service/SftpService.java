package org.lzx.web.gateway.fileupload.service;

import org.lzx.web.gateway.infrastructure.support.sftp.SftpUploadProperties;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * 文件服务
 *
 * @author LZx
 * @since 2021/1/5
 */
public interface SftpService {

    /**
     * 上传单个文件
     *
     * @param filePart    {@link FilePart}
     * @param sftpGroupId SFTP配置中，SFTP组的ID，{@link SftpUploadProperties.SftpGroup#getId()}
     * @param fileGroupId SFTP配置中，SFTP组中文件组的ID，{@link SftpUploadProperties.FileGroup#getId()}
     * @return 文件名
     */
    Mono<String> uploadSingle(Mono<FilePart> filePart, String sftpGroupId, String fileGroupId);

}
