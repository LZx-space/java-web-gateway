package org.lzx.web.gateway.fileupload.controller;

import org.lzx.web.gateway.fileupload.model.UploadSftpCmd;
import org.lzx.web.gateway.fileupload.service.SftpService;
import org.lzx.web.gateway.infrastructure.support.ReturnValueMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 文件上传SFTP控制器
 *
 * @author LZx
 * @since 2020/12/30
 */
@Api(tags = "文件上传管理")
@Controller
@RequestMapping("/sftp/files")
public class UploadSftpController {

    private final SftpService sftpService;

    public UploadSftpController(SftpService sftpService) {
        this.sftpService = sftpService;
    }

    /**
     * 表单方式的单文件上传
     *
     * @param filePartFlux {@link Flux < FilePart >}
     * @param uploadCmd    请求参数
     * @return Mono<访问路径集合> 单文件上传时为size为1的数组
     */
    @ApiOperation("上传文件")
    @ResponseBody
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<?> upload(@RequestPart("file") Flux<FilePart> filePartFlux,
                          @Validated UploadSftpCmd uploadCmd) {
        String sftpGroupName = uploadCmd.getSftpGroupId();
        String fileGroupName = uploadCmd.getFileGroupId();
        boolean structuredReturn = uploadCmd.isStructuredReturn();
        if (uploadCmd.isMultiFile()) {
            return Mono.error(new IllegalArgumentException("暂不支持多文件上传"));
        } else {
            Mono<FilePart> filePartMono = filePartFlux.elementAt(0);
            Mono<String> pathMono = sftpService.uploadSingle(filePartMono, sftpGroupName, fileGroupName);
            return structuredReturn ? pathMono.map(ReturnValueMapper.successMapper()) : pathMono;
        }
    }

}
