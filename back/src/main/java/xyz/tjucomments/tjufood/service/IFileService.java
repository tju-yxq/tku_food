// src/main/java/xyz/tjucomments/tjufood/service/IFileService.java
package xyz.tjucomments.tjufood.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.tjucomments.tjufood.dto.Result;

public interface IFileService {
    /**
     * 上传图片文件
     * @param imageFile 图片文件
     * @return 包含图片URL的Result对象
     */
    Result uploadImage(MultipartFile imageFile);
}