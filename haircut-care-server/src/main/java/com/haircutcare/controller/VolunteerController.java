package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.SysUser;
import com.haircutcare.entity.Volunteer;
import com.haircutcare.mapper.SysUserMapper;
import com.haircutcare.mapper.VolunteerMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {

    private final VolunteerMapper volunteerMapper;
    private final SysUserMapper sysUserMapper;

    public VolunteerController(VolunteerMapper volunteerMapper, SysUserMapper sysUserMapper) {
        this.volunteerMapper = volunteerMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @GetMapping
    public Result<List<Volunteer>> list() {
        List<Volunteer> list = volunteerMapper.selectList(
            new LambdaQueryWrapper<Volunteer>().orderByAsc(Volunteer::getId)
        );
        return Result.success(list);
    }

    @GetMapping("/available")
    public Result<List<Volunteer>> listAvailable() {
        List<Volunteer> list = volunteerMapper.selectList(
            new LambdaQueryWrapper<Volunteer>().eq(Volunteer::getAvailable, true)
        );
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Volunteer> getById(@PathVariable Long id) {
        Volunteer volunteer = volunteerMapper.selectById(id);
        return Result.success(volunteer);
    }

    @GetMapping("/{id}/user")
    public Result<SysUser> getUserByVolunteerId(@PathVariable Long id) {
        Volunteer volunteer = volunteerMapper.selectById(id);
        if (volunteer == null) {
            return Result.error("志愿者不存在");
        }
        SysUser user = sysUserMapper.selectById(volunteer.getUserId());
        return Result.success(user);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Volunteer volunteer) {
        volunteerMapper.insert(volunteer);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Volunteer volunteer) {
        volunteer.setId(id);
        volunteerMapper.updateById(volunteer);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        volunteerMapper.deleteById(id);
        return Result.success();
    }
}
