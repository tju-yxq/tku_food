package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.entity.Permission;

/**
 * 权限管理服务接口
 *
 * @author Gemini
 * @create 2025-06-16
 */
public interface IPermissionService extends IService<Permission> {
    // 继承了 IService 之后，已经拥有了强大的CRUD能力
    // 如 list(), getById(), save(), removeById(), updateById() 等
    // 目前无需在此处添加额外的自定义方法。
}