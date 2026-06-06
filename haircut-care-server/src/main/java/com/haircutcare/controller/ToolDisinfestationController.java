package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.*;
import com.haircutcare.mapper.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tool-disinfestations")
public class ToolDisinfestationController {

    private final ToolMapper toolMapper;
    private final ToolDisinfestationMapper toolDisinfestationMapper;
    private final ToolUsageRecordMapper toolUsageRecordMapper;
    private final ToolTypeMapper toolTypeMapper;

    public ToolDisinfestationController(ToolMapper toolMapper,
                                         ToolDisinfestationMapper toolDisinfestationMapper,
                                         ToolUsageRecordMapper toolUsageRecordMapper,
                                         ToolTypeMapper toolTypeMapper) {
        this.toolMapper = toolMapper;
        this.toolDisinfestationMapper = toolDisinfestationMapper;
        this.toolUsageRecordMapper = toolUsageRecordMapper;
        this.toolTypeMapper = toolTypeMapper;
    }

    @PostMapping("/{toolId}/use")
    @Transactional
    public Result<ToolUsageRecord> useTool(@PathVariable Long toolId,
                                            @RequestParam Long volunteerId,
                                            @RequestParam(required = false) Long appointmentId) {
        Tool tool = toolMapper.selectById(toolId);
        if (tool == null) {
            return Result.error("工具不存在");
        }
        if (!"AVAILABLE".equals(tool.getStatus())) {
            return Result.error("工具当前不可用");
        }

        tool.setStatus("IN_USE");
        tool.setUseCount(tool.getUseCount() + 1);
        toolMapper.updateById(tool);

        ToolUsageRecord record = new ToolUsageRecord();
        record.setToolId(toolId);
        record.setVolunteerId(volunteerId);
        record.setAppointmentId(appointmentId);
        record.setUsedAt(LocalDateTime.now());
        record.setStatus("IN_USE");
        toolUsageRecordMapper.insert(record);

        return Result.success("领取成功", record);
    }

    @PostMapping("/usage/{recordId}/return")
    @Transactional
    public Result<ToolUsageRecord> returnTool(@PathVariable Long recordId) {
        ToolUsageRecord record = toolUsageRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("使用记录不存在");
        }
        if (!"IN_USE".equals(record.getStatus())) {
            return Result.error("该记录已归还");
        }

        record.setReturnedAt(LocalDateTime.now());
        record.setStatus("RETURNED");
        toolUsageRecordMapper.updateById(record);

        Tool tool = toolMapper.selectById(record.getToolId());
        if (tool != null) {
            tool.setStatus("DISINFECTING");
            toolMapper.updateById(tool);

            ToolType toolType = toolTypeMapper.selectById(tool.getTypeId());
            int disinfectionMinutes = toolType != null ? toolType.getDisinfectionMinutes() : 30;

            ToolDisinfestation disinfestation = new ToolDisinfestation();
            disinfestation.setToolId(tool.getId());
            disinfestation.setStartAt(LocalDateTime.now());
            disinfestation.setStatus("PROCESSING");
            disinfestation.setDurationMinutes(disinfectionMinutes);
            disinfestation.setDisinfectionMethod("紫外线消毒");
            toolDisinfestationMapper.insert(disinfestation);
        }

        return Result.success("归还成功，已进入消毒流程", record);
    }

    @GetMapping("/disinfestations")
    public Result<List<ToolDisinfestation>> listDisinfestations(
            @RequestParam(required = false) Long toolId,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<ToolDisinfestation> wrapper = new LambdaQueryWrapper<ToolDisinfestation>()
                .orderByDesc(ToolDisinfestation::getCreatedAt);

        if (toolId != null) {
            wrapper.eq(ToolDisinfestation::getToolId, toolId);
        }
        if (status != null) {
            wrapper.eq(ToolDisinfestation::getStatus, status);
        }

        List<ToolDisinfestation> list = toolDisinfestationMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/disinfestations/{id}")
    public Result<Map<String, Object>> getDisinfestationDetail(@PathVariable Long id) {
        ToolDisinfestation disinfestation = toolDisinfestationMapper.selectById(id);
        if (disinfestation == null) {
            return Result.error("消毒记录不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("disinfestation", disinfestation);

        Tool tool = toolMapper.selectById(disinfestation.getToolId());
        detail.put("tool", tool);

        if (tool != null) {
            ToolType toolType = toolTypeMapper.selectById(tool.getTypeId());
            detail.put("toolType", toolType);
        }

        return Result.success(detail);
    }

    @PostMapping("/disinfestations/{id}/complete")
    @Transactional
    public Result<ToolDisinfestation> completeDisinfestation(@PathVariable Long id) {
        ToolDisinfestation disinfestation = toolDisinfestationMapper.selectById(id);
        if (disinfestation == null) {
            return Result.error("消毒记录不存在");
        }
        if (!"PROCESSING".equals(disinfestation.getStatus())) {
            return Result.error("该消毒记录已完成");
        }

        disinfestation.setEndAt(LocalDateTime.now());
        disinfestation.setStatus("COMPLETED");

        if (disinfestation.getStartAt() != null) {
            long minutes = ChronoUnit.MINUTES.between(disinfestation.getStartAt(), LocalDateTime.now());
            disinfestation.setDurationMinutes((int) minutes);
        }

        toolDisinfestationMapper.updateById(disinfestation);

        Tool tool = toolMapper.selectById(disinfestation.getToolId());
        if (tool != null) {
            tool.setStatus("AVAILABLE");
            tool.setLastDisinfestationAt(LocalDateTime.now());
            toolMapper.updateById(tool);
        }

        return Result.success("消毒完成", disinfestation);
    }

    @GetMapping("/{toolId}/usage-history")
    public Result<List<ToolUsageRecord>> getToolUsageHistory(@PathVariable Long toolId) {
        List<ToolUsageRecord> list = toolUsageRecordMapper.selectList(
            new LambdaQueryWrapper<ToolUsageRecord>()
                .eq(ToolUsageRecord::getToolId, toolId)
                .orderByDesc(ToolUsageRecord::getUsedAt)
        );
        return Result.success(list);
    }

    @GetMapping("/{toolId}/disinfestation-history")
    public Result<List<ToolDisinfestation>> getToolDisinfestationHistory(@PathVariable Long toolId) {
        List<ToolDisinfestation> list = toolDisinfestationMapper.selectList(
            new LambdaQueryWrapper<ToolDisinfestation>()
                .eq(ToolDisinfestation::getToolId, toolId)
                .orderByDesc(ToolDisinfestation::getCreatedAt)
        );
        return Result.success(list);
    }
}
