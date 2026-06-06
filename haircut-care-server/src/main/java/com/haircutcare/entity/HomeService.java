package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("home_service")
public class HomeService {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private Long volunteerId;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private LocalDateTime actualArrivalAt;
    private LocalDateTime actualFinishAt;
    private String status;
    private String feedback;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
