package xyz.tjucomments.tjufood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.tjucomments.tjufood.entity.Admin;
import java.util.List;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    /**
     * 根据管理员ID，通过联表查询获取其所有角色的名称列表
     * @param adminId 管理员ID
     * @return 角色名称列表 (例如: ["系统管理员", "内容管理员"])
     */
    List<String> findRoleNamesByAdminId(@Param("adminId") Long adminId);
}