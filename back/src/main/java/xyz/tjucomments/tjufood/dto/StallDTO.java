// 文件路径: back/src/main/java/xyz/tjucomments/tjufood/dto/StallDTO.java

package xyz.tjucomments.tjufood.dto;

import lombok.Data;
import xyz.tjucomments.tjufood.entity.Stall;
import java.time.LocalDateTime;

@Data
public class StallDTO {

    private Long id;
    private String name;
    private Long canteenId;
    private Long typeId;
    private String location;
    private String introduction;
    private String openHours;

    // ▼▼▼ 【新增】补全字段 ▼▼▼
    private Integer avgPrice;

    private Double score;
    private Double tasteScore;

    // ▼▼▼ 【新增】补全字段 ▼▼▼
    private Double environmentScore;
    private Double serviceScore;

    private Double priceScore;
    private Integer comments;
    private Integer openStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段
    private String canteenName;
    private String typeName;

    /**
     * 从实体类转换的静态工厂方法 (已更新)
     * @param stall Stall实体对象
     * @return StallDTO对象
     */
    public static StallDTO fromEntity(Stall stall) {
        if (stall == null) {
            return null;
        }
        StallDTO dto = new StallDTO();
        dto.setId(stall.getId());
        dto.setName(stall.getName());
        dto.setCanteenId(stall.getCanteenId());
        dto.setTypeId(stall.getTypeId());
        dto.setLocation(stall.getLocation());
        dto.setIntroduction(stall.getIntroduction());
        dto.setOpenHours(stall.getOpenHours());

        // ▼▼▼ 【新增】赋值新字段 ▼▼▼
        dto.setAvgPrice(stall.getAvgPrice());

        dto.setScore(stall.getScore());
        dto.setTasteScore(stall.getTasteScore());

        // ▼▼▼ 【新增】赋值新字段 ▼▼▼
        dto.setEnvironmentScore(stall.getEnvironmentScore());
        dto.setServiceScore(stall.getServiceScore());

        dto.setPriceScore(stall.getPriceScore());
        dto.setComments(stall.getComments());
        dto.setOpenStatus(stall.getOpenStatus());
        dto.setCreateTime(stall.getCreateTime());
        dto.setUpdateTime(stall.getUpdateTime());

        return dto;
    }
}