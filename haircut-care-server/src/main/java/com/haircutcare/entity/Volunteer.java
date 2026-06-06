package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("volunteer")
public class Volunteer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String volunteerNo;
    private String skillLevel;
    private String speciality;
    private Boolean available;
    private Integer totalServed;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
