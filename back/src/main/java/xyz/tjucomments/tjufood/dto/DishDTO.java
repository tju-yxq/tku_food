package xyz.tjucomments.tjufood.dto;

import lombok.Data;
import xyz.tjucomments.tjufood.entity.Dish;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DishDTO {

    private Long id;
    private String name;
    private Long stallId;
    private String category;
    private BigDecimal price;
    private String description;

    // 【修正】移除了 score 和 comments 字段，添加了 liked 字段
    private Integer liked;

    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联的名称字段
    private String stallName;
    private String canteenName;

    /**
     * 【修正】从实体类转换的静态工厂方法
     * @param dish Dish实体对象
     * @return DishDTO对象
     */
    public static DishDTO fromEntity(Dish dish) {
        if (dish == null) {
            return null;
        }
        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setStallId(dish.getStallId());
        dto.setCategory(dish.getCategory());
        dto.setPrice(dish.getPrice());
        dto.setDescription(dish.getDescription());

        // 【修正】调用实体中存在的 getLiked() 方法
        dto.setLiked(dish.getLiked());

        dto.setStatus(dish.getStatus());
        dto.setCreateTime(dish.getCreateTime());
        dto.setUpdateTime(dish.getUpdateTime());

        // 注意：stallName 和 canteenName 需要在 Controller 或 Service 层单独设置
        return dto;
    }
}