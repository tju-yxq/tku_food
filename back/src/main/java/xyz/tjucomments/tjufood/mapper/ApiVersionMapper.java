package xyz.tjucomments.tjufood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.tjucomments.tjufood.entity.ApiVersion;

/**
 * API版本管理Mapper接口
 */
@Mapper
public interface ApiVersionMapper extends BaseMapper<ApiVersion> {
}
