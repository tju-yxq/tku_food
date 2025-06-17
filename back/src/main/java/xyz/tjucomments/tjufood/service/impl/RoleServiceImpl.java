// 文件路径: src/main/java/xyz/tjucomments/tjufood/service/impl/RoleServiceImpl.java

package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Role;
import xyz.tjucomments.tjufood.entity.RolePermission;
import xyz.tjucomments.tjufood.mapper.RoleMapper;
import xyz.tjucomments.tjufood.mapper.RolePermissionMapper;
import xyz.tjucomments.tjufood.service.IRoleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional // 开启事务，保证操作的原子性
    public Result assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 1. 先删除该角色原有的所有权限关联
        rolePermissionMapper.delete(new QueryWrapper<RolePermission>().eq("role_id", roleId));

        // 2. 如果权限列表不为空，则批量插入新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
        return Result.ok();
    }

    @Override
    public Result getPermissionIdsByRoleId(Long roleId) {
        // 查询关联表，获取该角色所有的权限关联记录
        List<RolePermission> relations = rolePermissionMapper.selectList(
                new QueryWrapper<RolePermission>().eq("role_id", roleId)
        );
        // 提取所有权限ID并返回
        List<Long> permissionIds = relations.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        return Result.ok(permissionIds);
    }
}