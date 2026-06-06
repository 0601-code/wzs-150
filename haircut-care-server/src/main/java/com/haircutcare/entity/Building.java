package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("building")
public class Building {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String buildingNo;
    private String buildingName;
    private Integer unitCount;
    private Integer floorCount;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
