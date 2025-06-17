package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.CanteenType;
import xyz.tjucomments.tjufood.mapper.CanteenTypeMapper;
import xyz.tjucomments.tjufood.service.ICanteenTypeService;

@Service
public class CanteenTypeServiceImpl extends ServiceImpl<CanteenTypeMapper, CanteenType> implements ICanteenTypeService {
}