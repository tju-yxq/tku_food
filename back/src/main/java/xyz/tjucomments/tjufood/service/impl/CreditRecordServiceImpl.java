// src/main/java/xyz/tjucomments/tjufood/service/impl/CreditRecordServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.CreditRecord;
import xyz.tjucomments.tjufood.mapper.CreditRecordMapper;
import xyz.tjucomments.tjufood.service.ICreditRecordService;

@Service
public class CreditRecordServiceImpl extends ServiceImpl<CreditRecordMapper, CreditRecord> implements ICreditRecordService {
}