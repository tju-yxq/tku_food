// 文件路径: src/main/java/xyz/tjucomments/tjufood/dto/RolePermissionDTO.java

package xyz.tjucomments.tjufood.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "为角色分配权限时使用的数据传输对象")
public class RolePermissionDTO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "该角色拥有的权限ID列表")
    private List<Long> permissionIds;
}