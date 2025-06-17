// src/main/java/xyz/tjucomments/tjufood/service/impl/FileServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.IFileService;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    /**
     * 从 application.yaml 注入Nginx配置的图片访问URL前缀
     * 例如：http://your-server-ip/imgs/
     */
    @Value("${tjufood.image-access-url}")
    private String imageAccessUrl;

    /**
     * 从 application.yaml 注入文件上传的本地磁盘路径
     * 例如：D:/lesson/nginx-1.18.0/html/tjufood/imgs/
     */
    @Value("${tjufood.image-upload-dir}")
    private String uploadDir;

    @Override
    public Result uploadImage(MultipartFile imageFile) {
        // 1. 校验文件是否为空
        if (imageFile == null || imageFile.isEmpty()) {
            return Result.fail("上传的文件不能为空！");
        }

        // 2. 获取原始文件名
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null) {
            return Result.fail("文件名异常！");
        }

        // 3. 生成新的唯一文件名，防止重名覆盖
        // 格式：UUID.后缀名
        String fileExtension = FileUtil.extName(originalFilename);
        String newFileName = UUID.randomUUID().toString(true) + "." + fileExtension;

        // 4. 创建目标保存文件对象
        File destFile = new File(uploadDir, newFileName);

        // 5. 确保父目录存在
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        try {
            // 6. 将上传的文件保存到目标位置
            imageFile.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败，服务器内部错误！");
        }

        // 7. 拼接并返回可访问的URL
        String fileUrl = imageAccessUrl + newFileName;
        log.info("文件上传成功，访问URL: {}", fileUrl);
        return Result.ok(fileUrl);
    }
}