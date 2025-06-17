// 文件路径: src/main/java/xyz/tjucomments/tjufood/service/impl/NoticeServiceImpl.java

package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.entity.Notice;
import xyz.tjucomments.tjufood.mapper.NoticeMapper;
import xyz.tjucomments.tjufood.service.INoticeService;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {
}