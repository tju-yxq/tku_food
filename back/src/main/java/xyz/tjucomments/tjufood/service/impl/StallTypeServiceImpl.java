package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.StallType;
import xyz.tjucomments.tjufood.mapper.StallTypeMapper;
import xyz.tjucomments.tjufood.service.IStallTypeService;

@Service
public class StallTypeServiceImpl extends ServiceImpl<StallTypeMapper, StallType> implements IStallTypeService {
}