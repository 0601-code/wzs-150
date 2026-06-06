package com.haircutcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tool_type")
public class ToolType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String typeName;
    private String typeCode;
    private Integer disinfectionMinutes;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
