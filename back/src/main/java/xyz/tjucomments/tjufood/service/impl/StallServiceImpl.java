package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.dto.StallDTO;
import xyz.tjucomments.tjufood.entity.Stall;
import xyz.tjucomments.tjufood.mapper.StallMapper;
import xyz.tjucomments.tjufood.service.IStallService;
import java.util.List;

@Service
public class StallServiceImpl extends ServiceImpl<StallMapper, Stall> implements IStallService {

    @Override
    public Result queryStallById(Long id) {
        // XML中的自定义多表查询返回StallDTO，保持不变
        StallDTO stall = baseMapper.queryStallById(id);
        if (stall == null) {
            return Result.fail("窗口不存在！");
        }
        return Result.ok(stall);
    }

    @Override
    public Result listStallsByCanteenId(Long canteenId) {
        // XML中的自定义多表查询返回StallDTO，保持不变
        List<StallDTO> stalls = baseMapper.listStallsByCanteenId(canteenId);
        return Result.ok(stalls);
    }

    // addStall, updateStall, deleteStall 等冗余方法已移除。
}
