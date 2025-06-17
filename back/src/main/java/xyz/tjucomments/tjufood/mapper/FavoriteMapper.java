// src/main/java/xyz/tjucomments/tjufood/mapper/FavoriteMapper.java
package xyz.tjucomments.tjufood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.tjucomments.tjufood.entity.Favorite;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}