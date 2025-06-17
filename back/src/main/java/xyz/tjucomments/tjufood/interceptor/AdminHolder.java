package xyz.tjucomments.tjufood.interceptor;

import xyz.tjucomments.tjufood.dto.AdminDTO;

/**
 * 使用 ThreadLocal 保存后台管理员信息的工具类
 */
public class AdminHolder {
    private static final ThreadLocal<AdminDTO> tl = new ThreadLocal<>();

    public static void saveAdmin(AdminDTO admin){
        tl.set(admin);
    }

    public static AdminDTO getAdmin(){
        return tl.get();
    }

    public static void removeAdmin(){
        tl.remove();
    }
}