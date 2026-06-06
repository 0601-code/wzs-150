package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.ToolType;
import com.haircutcare.mapper.ToolTypeMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tool-types")
public class ToolTypeController {

    private final ToolTypeMapper toolTypeMapper;

    public ToolTypeController(ToolTypeMapper toolTypeMapper) {
        this.toolTypeMapper = toolTypeMapper;
    }

    @GetMapping
    public Result<List<ToolType>> list() {
        List<ToolType> list = toolTypeMapper.selectList(
            new LambdaQueryWrapper<ToolType>().orderByAsc(ToolType::getId)
        );
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<ToolType> getById(@PathVariable Long id) {
        ToolType toolType = toolTypeMapper.selectById(id);
        return Result.success(toolType);
    }

    @PostMapping
    public Result<Void> create(@RequestBody ToolType toolType) {
        toolTypeMapper.insert(toolType);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ToolType toolType) {
        toolType.setId(id);
        toolTypeMapper.updateById(toolType);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        toolTypeMapper.deleteById(id);
        return Result.success();
    }
}
