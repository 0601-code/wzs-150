package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tool_disinfestation")
public class ToolDisinfestation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long toolId;
    private Long operatorId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String disinfectionMethod;
    private Integer durationMinutes;
    private String status;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
