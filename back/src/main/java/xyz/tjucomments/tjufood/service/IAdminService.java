// 文件路径: src/main/java/xyz/tjucomments/tjufood/service/IAdminService.java

package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.AdminCreateDTO;
import xyz.tjucomments.tjufood.dto.AdminInfoVO;
import xyz.tjucomments.tjufood.dto.AdminLoginDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Admin;

import java.util.List;

public interface IAdminService extends IService<Admin> {

    AdminInfoVO login(AdminLoginDTO loginForm);

    List<String> getRolesByUsername(String username);

    Result logout(String token);

    /**
     * 【新增】创建管理员账号
     * @param createDTO 包含用户名和姓名的数据
     * @return 操作结果
     */
    Result createAdmin(AdminCreateDTO createDTO);
}