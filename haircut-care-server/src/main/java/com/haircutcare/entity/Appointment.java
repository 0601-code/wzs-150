package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String appointmentNo;
    private Long residentId;
    private Long activityId;
    private Long timeSlotId;
    private String serviceType;
    private Boolean needHomeService;
    private String homeAddress;
    private Long volunteerId;
    private String status;
    private Integer queueNumber;
    private LocalDateTime checkInAt;
    private LocalDateTime serviceStartAt;
    private LocalDateTime serviceEndAt;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
