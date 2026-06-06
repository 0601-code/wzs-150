package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.Tool;
import com.haircutcare.mapper.ToolMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tools")
public class ToolController {

    private final ToolMapper toolMapper;

    public ToolController(ToolMapper toolMapper) {
        this.toolMapper = toolMapper;
    }

    @GetMapping
    public Result<List<Tool>> list(@RequestParam(required = false) String status) {
        LambdaQueryWrapper<Tool> wrapper = new LambdaQueryWrapper<Tool>().orderByAsc(Tool::getToolNo);
        if (status != null) {
            wrapper.eq(Tool::getStatus, status);
        }
        List<Tool> list = toolMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Tool> getById(@PathVariable Long id) {
        Tool tool = toolMapper.selectById(id);
        return Result.success(tool);
    }

    @GetMapping("/type/{typeId}")
    public Result<List<Tool>> getByTypeId(@PathVariable Long typeId) {
        List<Tool> list = toolMapper.selectList(
            new LambdaQueryWrapper<Tool>().eq(Tool::getTypeId, typeId).orderByAsc(Tool::getToolNo)
        );
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Tool tool) {
        toolMapper.insert(tool);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Tool tool) {
        tool.setId(id);
        toolMapper.updateById(tool);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        toolMapper.deleteById(id);
        return Result.success();
    }
}
