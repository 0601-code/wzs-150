package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("time_slot")
public class TimeSlot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCount;
    private Integer currentCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
