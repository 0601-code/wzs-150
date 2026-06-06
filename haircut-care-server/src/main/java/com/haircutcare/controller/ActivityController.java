package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.Activity;
import com.haircutcare.entity.TimeSlot;
import com.haircutcare.mapper.ActivityMapper;
import com.haircutcare.mapper.TimeSlotMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityMapper activityMapper;
    private final TimeSlotMapper timeSlotMapper;

    public ActivityController(ActivityMapper activityMapper, TimeSlotMapper timeSlotMapper) {
        this.activityMapper = activityMapper;
        this.timeSlotMapper = timeSlotMapper;
    }

    @GetMapping
    public Result<List<Activity>> list() {
        List<Activity> list = activityMapper.selectList(
            new LambdaQueryWrapper<Activity>().orderByDesc(Activity::getActivityDate)
        );
        return Result.success(list);
    }

    @GetMapping("/public")
    public Result<List<Activity>> listPublic() {
        List<Activity> list = activityMapper.selectList(
            new LambdaQueryWrapper<Activity>()
                .in(Activity::getStatus, "UPCOMING", "ONGOING")
                .orderByAsc(Activity::getActivityDate)
        );
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Activity> getById(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        return Result.success(activity);
    }

    @GetMapping("/{id}/time-slots")
    public Result<List<TimeSlot>> getTimeSlots(@PathVariable Long id) {
        List<TimeSlot> list = timeSlotMapper.selectList(
            new LambdaQueryWrapper<TimeSlot>()
                .eq(TimeSlot::getActivityId, id)
                .orderByAsc(TimeSlot::getStartTime)
        );
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Activity activity) {
        activityMapper.insert(activity);
        return Result.success();
    }

    @PostMapping("/{id}/time-slots")
    public Result<Void> addTimeSlots(@PathVariable Long id, @RequestBody List<TimeSlot> timeSlots) {
        for (TimeSlot slot : timeSlots) {
            slot.setActivityId(id);
            timeSlotMapper.insert(slot);
        }
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        activityMapper.updateById(activity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityMapper.deleteById(id);
        return Result.success();
    }
}
