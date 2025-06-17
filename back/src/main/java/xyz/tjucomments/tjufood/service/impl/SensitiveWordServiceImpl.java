// 文件路径: src/main/java/xyz/tjucomments/tjufood/service/impl/SensitiveWordServiceImpl.java

package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.SensitiveWord;
import xyz.tjucomments.tjufood.mapper.SensitiveWordMapper;
import xyz.tjucomments.tjufood.service.ISensitiveWordService;

@Service
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements ISensitiveWordService {
}