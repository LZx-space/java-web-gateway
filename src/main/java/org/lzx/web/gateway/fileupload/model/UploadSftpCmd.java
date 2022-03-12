package org.lzx.web.gateway.fileupload.model;

import org.lzx.web.gateway.infrastructure.support.sftp.SftpUploadProperties;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 上传文件请求参数
 *
 * @author LZx
 * @since 2021/1/13
 */
@Data
public class UploadSftpCmd implements Serializable {

    /**
     * 是否多文件上传
     */
    @ApiParam("是否多文件上传")
    private boolean multiFile;

    /**
     * 返回数据使用约定的数据结构
     */
    @ApiParam("是否使用标准化的数据结构返回数据")
    private boolean structuredReturn;

    /**
     * {@link SftpUploadProperties.SftpGroup#getId()}
     * <p>
     * 上传sftp集群名称
     */
    @ApiParam("SFTP集群ID")
    @NotBlank(message = "上传SFTP组不能为空")
    private String sftpGroupId;

    /**
     * {@link SftpUploadProperties.FileGroup#getId()}
     */
    @ApiParam("目标SFTP集群中的预定义文件组的ID")
    @NotBlank(message = "上传文件组不能为空")
    private String fileGroupId;

}
