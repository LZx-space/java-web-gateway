package org.lzx.web.gateway.fileupload.service.impl;

import org.lzx.web.gateway.fileupload.service.SftpService;
import org.lzx.web.gateway.infrastructure.support.sftp.SftpClusterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;

/**
 * @author LZx
 * @since 2021/1/5
 */
@Slf4j
@Service
public class SftpServiceImpl implements SftpService {

    private final SftpClusterHelper sftpClusterHelper;

    public SftpServiceImpl(SftpClusterHelper sftpClusterHelper) {
        this.sftpClusterHelper = sftpClusterHelper;
    }

    @Override
    public Mono<String> uploadSingle(Mono<FilePart> filePartMono, final String sftpGroupId, final String fileGroupId) {
        return filePartMono.flatMap(filePart -> Mono
                .fromCallable(() -> Files.createTempFile(null, "_" + filePart.filename()))
                .flatMap(tempPath -> filePart
                        .transferTo(tempPath)
                        .then(Mono
                                .fromCallable(() -> sftpClusterHelper.upload(sftpGroupId, fileGroupId, tempPath.toFile()))
                                .doFinally(signalType -> {
                                    try {
                                        Files.delete(tempPath);
                                    } catch (IOException ignored) {
                                        log.warn("删除上传文件的本地临时文件[{}]异常", tempPath.toFile().getAbsolutePath());
                                    }
                                })
                        )
                ));
    }

}
