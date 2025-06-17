// 文件路径: src/main/java/xyz/tjucomments/tjufood/service/IRoleService.java

package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Role;

import java.util.List;

public interface IRoleService extends IService<Role> {

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    Result assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 查询指定角色拥有的权限ID列表
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    Result getPermissionIdsByRoleId(Long roleId);
}