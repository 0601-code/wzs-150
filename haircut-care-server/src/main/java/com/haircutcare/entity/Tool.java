package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("tool")
public class Tool {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String toolNo;
    private String toolName;
    private Long typeId;
    private String brand;
    private String model;
    private String status;
    private LocalDate purchaseDate;
    private LocalDateTime lastDisinfestationAt;
    private Integer useCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
