package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.*;
import com.haircutcare.mapper.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentMapper appointmentMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final SysUserMapper sysUserMapper;
    private final BuildingMapper buildingMapper;
    private final VolunteerMapper volunteerMapper;
    private final HomeServiceMapper homeServiceMapper;

    public AppointmentController(AppointmentMapper appointmentMapper,
                                  TimeSlotMapper timeSlotMapper,
                                  SysUserMapper sysUserMapper,
                                  BuildingMapper buildingMapper,
                                  VolunteerMapper volunteerMapper,
                                  HomeServiceMapper homeServiceMapper) {
        this.appointmentMapper = appointmentMapper;
        this.timeSlotMapper = timeSlotMapper;
        this.sysUserMapper = sysUserMapper;
        this.buildingMapper = buildingMapper;
        this.volunteerMapper = volunteerMapper;
        this.homeServiceMapper = homeServiceMapper;
    }

    @GetMapping
    public Result<List<Appointment>> list(
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean needHomeService) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
                .orderByDesc(Appointment::getCreatedAt);

        if (activityId != null) {
            wrapper.eq(Appointment::getActivityId, activityId);
        }
        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        if (needHomeService != null) {
            wrapper.eq(Appointment::getNeedHomeService, needHomeService);
        }

        List<Appointment> list = appointmentMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("appointment", appointment);

        SysUser resident = sysUserMapper.selectById(appointment.getResidentId());
        detail.put("resident", resident);

        if (resident != null && resident.getBuildingId() != null) {
            Building building = buildingMapper.selectById(resident.getBuildingId());
            detail.put("building", building);
        }

        if (appointment.getVolunteerId() != null) {
            Volunteer volunteer = volunteerMapper.selectById(appointment.getVolunteerId());
            detail.put("volunteer", volunteer);
            if (volunteer != null) {
                SysUser barber = sysUserMapper.selectById(volunteer.getUserId());
                detail.put("barber", barber);
            }
        }

        if (appointment.getTimeSlotId() != null) {
            TimeSlot timeSlot = timeSlotMapper.selectById(appointment.getTimeSlotId());
            detail.put("timeSlot", timeSlot);
        }

        return Result.success(detail);
    }

    @GetMapping("/public/{id}")
    public Result<Appointment> getByIdPublic(@PathVariable Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        return Result.success(appointment);
    }

    @GetMapping("/resident/{residentId}")
    public Result<List<Appointment>> getByResidentId(@PathVariable Long residentId) {
        List<Appointment> list = appointmentMapper.selectList(
            new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getResidentId, residentId)
                .orderByDesc(Appointment::getCreatedAt)
        );
        return Result.success(list);
    }

    @PostMapping("/public")
    @Transactional
    public Result<Appointment> createPublic(@RequestBody Appointment appointment) {
        String appointmentNo = "APPT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        appointment.setAppointmentNo(appointmentNo);
        appointment.setStatus("PENDING");

        if (appointment.getTimeSlotId() != null) {
            TimeSlot timeSlot = timeSlotMapper.selectById(appointment.getTimeSlotId());
            if (timeSlot != null && timeSlot.getCurrentCount() < timeSlot.getMaxCount()) {
                timeSlot.setCurrentCount(timeSlot.getCurrentCount() + 1);
                timeSlotMapper.updateById(timeSlot);
            } else {
                return Result.error("该时段已满，请选择其他时段");
            }
        }

        Long maxQueue = appointmentMapper.selectCount(
            new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getActivityId, appointment.getActivityId())
        );
        appointment.setQueueNumber(maxQueue.intValue() + 1);

        appointmentMapper.insert(appointment);

        if (Boolean.TRUE.equals(appointment.getNeedHomeService())) {
            HomeService homeService = new HomeService();
            homeService.setAppointmentId(appointment.getId());
            homeService.setStatus("PENDING");
            homeServiceMapper.insert(homeService);
        }

        return Result.success("预约成功", appointment);
    }

    @PostMapping
    @Transactional
    public Result<Appointment> create(@RequestBody Appointment appointment) {
        return createPublic(appointment);
    }

    @PostMapping("/{id}/check-in")
    @Transactional
    public Result<Appointment> checkIn(@PathVariable Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }

        appointment.setStatus("CHECKED_IN");
        appointment.setCheckInAt(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        return Result.success("签到成功", appointment);
    }

    @PostMapping("/{id}/assign-barber")
    @Transactional
    public Result<Appointment> assignBarber(@PathVariable Long id, @RequestParam Long volunteerId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }

        Volunteer volunteer = volunteerMapper.selectById(volunteerId);
        if (volunteer == null || !volunteer.getAvailable()) {
            return Result.error("该理发师不可用");
        }

        appointment.setVolunteerId(volunteerId);
        appointment.setStatus("IN_PROGRESS");
        appointment.setServiceStartAt(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        return Result.success("分配成功", appointment);
    }

    @PostMapping("/{id}/complete")
    @Transactional
    public Result<Appointment> complete(@PathVariable Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }

        appointment.setStatus("COMPLETED");
        appointment.setServiceEndAt(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        if (appointment.getVolunteerId() != null) {
            Volunteer volunteer = volunteerMapper.selectById(appointment.getVolunteerId());
            if (volunteer != null) {
                volunteer.setTotalServed(volunteer.getTotalServed() + 1);
                volunteerMapper.updateById(volunteer);
            }
        }

        return Result.success("服务完成", appointment);
    }

    @PostMapping("/{id}/cancel")
    @Transactional
    public Result<Appointment> cancel(@PathVariable Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }

        if (appointment.getTimeSlotId() != null) {
            TimeSlot timeSlot = timeSlotMapper.selectById(appointment.getTimeSlotId());
            if (timeSlot != null && timeSlot.getCurrentCount() > 0) {
                timeSlot.setCurrentCount(timeSlot.getCurrentCount() - 1);
                timeSlotMapper.updateById(timeSlot);
            }
        }

        appointment.setStatus("CANCELLED");
        appointmentMapper.updateById(appointment);

        return Result.success("已取消预约", appointment);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Appointment appointment) {
        appointment.setId(id);
        appointmentMapper.updateById(appointment);
        return Result.success();
    }
}
