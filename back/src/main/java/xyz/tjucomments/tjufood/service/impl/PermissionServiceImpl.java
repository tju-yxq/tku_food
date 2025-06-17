package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.Permission;
import xyz.tjucomments.tjufood.mapper.PermissionMapper;
import xyz.tjucomments.tjufood.service.IPermissionService;

/**
 * 权限管理服务实现类
 *
 * @author Gemini
 * @create 2025-06-16
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {
    // ServiceImpl 已经帮我们实现了所有 IService 中的方法，
    // 通过自动注入的 baseMapper (即 PermissionMapper) 与数据库交互。
    // 因为目前没有复杂的自定义业务，所以这个类保持简洁即可。
}