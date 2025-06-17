// src/main/java/xyz/tjucomments/tjufood/controller/FileController.java
package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.IFileService;

@Tag(name = "11. 文件上传功能", description = "提供通用的文件上传接口")
@RestController
@RequestMapping("/api/upload")
public class FileController {

    @Resource
    private IFileService fileService;

    @Operation(summary = "上传图片", description = "上传成功后返回图片的访问URL")
    @PostMapping("/image")
    public Result uploadImage(@Parameter(description = "要上传的图片文件") @RequestParam("file") MultipartFile imageFile) {
        return fileService.uploadImage(imageFile);
    }
}