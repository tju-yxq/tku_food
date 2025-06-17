package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.Campus;
import xyz.tjucomments.tjufood.mapper.CampusMapper;
import xyz.tjucomments.tjufood.service.ICampusService;

@Service
public class CampusServiceImpl extends ServiceImpl<CampusMapper, Campus> implements ICampusService {
}