// src/main/java/xyz/tjucomments/tjufood/service/impl/UserVoucherServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.UserVoucher;
import xyz.tjucomments.tjufood.mapper.UserVoucherMapper;
import xyz.tjucomments.tjufood.service.IUserVoucherService;

@Service
public class UserVoucherServiceImpl extends ServiceImpl<UserVoucherMapper, UserVoucher> implements IUserVoucherService {
}