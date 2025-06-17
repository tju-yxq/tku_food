// 文件路径: src/main/java/xyz/tjucomments/tjufood/service/impl/BannerServiceImpl.java

package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.Banner;
import xyz.tjucomments.tjufood.mapper.BannerMapper;
import xyz.tjucomments.tjufood.service.IBannerService;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {
}