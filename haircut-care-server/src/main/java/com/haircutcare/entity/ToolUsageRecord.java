package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tool_usage_record")
public class ToolUsageRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long toolId;
    private Long appointmentId;
    private Long volunteerId;
    private LocalDateTime usedAt;
    private LocalDateTime returnedAt;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
