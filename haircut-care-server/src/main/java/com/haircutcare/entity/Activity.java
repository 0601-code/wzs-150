package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String activityName;
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private Integer maxCapacity;
    private String status;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
