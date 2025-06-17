package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.dto.CanteenDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Canteen;
import xyz.tjucomments.tjufood.mapper.CanteenMapper;
import xyz.tjucomments.tjufood.service.ICanteenService;
import xyz.tjucomments.tjufood.utils.cache.CacheClient;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
public class CanteenServiceImpl extends ServiceImpl<CanteenMapper, Canteen> implements ICanteenService {

    @Resource
    private CacheClient cacheClient;

    // 【重要】移除我们之前添加的 pageQuery 方法，保持这个文件整洁

    @Override
    public Result listCanteens() {
        // ... 此处逻辑不变 ...
        List<CanteenDTO> canteens = baseMapper.listCanteens();
        return Result.ok(canteens);
    }

    @Override
    public Result queryCanteenById(Long id) {
        // ... 此处逻辑不变 ...
        CanteenDTO canteen = cacheClient.queryWithPassThrough(
                RedisConstants.CACHE_CANTEEN_KEY,
                id,
                CanteenDTO.class,
                canteenId -> baseMapper.queryCanteenById(canteenId),
                RedisConstants.CACHE_CANTEEN_TTL,
                TimeUnit.MINUTES
        );
        if (canteen == null) {
            return Result.fail("食堂不存在！");
        }
        return Result.ok(canteen);
    }
}