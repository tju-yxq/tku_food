// 文件路径: yxq/back/src/main/java/xyz/tjucomments/tjufood/dto/AdminInfoVO.java (新增文件)
package xyz.tjucomments.tjufood.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdminInfoVO {
    private String token;
    private Long id;
    private String username;
    private String name;
    private String avatar; // 预留头像字段
    private List<String> roles;
}