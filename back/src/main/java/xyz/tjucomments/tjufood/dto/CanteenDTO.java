package xyz.tjucomments.tjufood.dto;

import lombok.Data;
import xyz.tjucomments.tjufood.entity.Canteen;

/**
 * @author Gemini
 * @description 食堂数据传输对象 (已重构，采用组合而非继承)
 * @create 2025-06-16 14:15
 */
@Data
public class CanteenDTO {

    // 包含所有Canteen实体的基础字段
    private Long id;
    private String name;
    private Long campusId;
    private Long typeId;
    private String address;
    private String floor;
    private String openHours;
    private Integer avgPrice;
    private String introduction;
    private Double score;
    private Double tasteScore;
    private Double environmentScore;
    private Double serviceScore;
    private Integer liked;
    private Integer comments;
    private Integer openStatus;

    // 扩展字段
    private String campusName;
    private String typeName;

    /**
     * 【新增】从实体类转换的静态工厂方法
     *
     * @param canteen Canteen实体对象
     * @return CanteenDTO对象
     */
    public static CanteenDTO fromEntity(Canteen canteen) {
        if (canteen == null) {
            return null;
        }
        CanteenDTO dto = new CanteenDTO();
        dto.setId(canteen.getId());
        dto.setName(canteen.getName());
        dto.setCampusId(canteen.getCampusId());
        dto.setTypeId(canteen.getTypeId());
        dto.setAddress(canteen.getAddress());
        dto.setFloor(canteen.getFloor());
        dto.setOpenHours(canteen.getOpenHours());
        dto.setAvgPrice(canteen.getAvgPrice());
        dto.setIntroduction(canteen.getIntroduction());
        dto.setScore(canteen.getScore());
        dto.setTasteScore(canteen.getTasteScore());
        dto.setEnvironmentScore(canteen.getEnvironmentScore());
        dto.setServiceScore(canteen.getServiceScore());
        dto.setLiked(canteen.getLiked());
        dto.setComments(canteen.getComments());
        dto.setOpenStatus(canteen.getOpenStatus());
        return dto;
    }
}
