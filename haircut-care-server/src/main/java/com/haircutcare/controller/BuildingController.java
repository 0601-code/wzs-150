package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.Building;
import com.haircutcare.mapper.BuildingMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingMapper buildingMapper;

    public BuildingController(BuildingMapper buildingMapper) {
        this.buildingMapper = buildingMapper;
    }

    @GetMapping
    public Result<List<Building>> list() {
        List<Building> list = buildingMapper.selectList(
            new LambdaQueryWrapper<Building>().orderByAsc(Building::getBuildingNo)
        );
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Building> getById(@PathVariable Long id) {
        Building building = buildingMapper.selectById(id);
        return Result.success(building);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Building building) {
        buildingMapper.insert(building);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Building building) {
        building.setId(id);
        buildingMapper.updateById(building);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        buildingMapper.deleteById(id);
        return Result.success();
    }
}
