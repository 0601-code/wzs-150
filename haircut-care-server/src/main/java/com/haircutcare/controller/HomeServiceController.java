package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.entity.*;
import com.haircutcare.mapper.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home-services")
public class HomeServiceController {

    private final HomeServiceMapper homeServiceMapper;
    private final AppointmentMapper appointmentMapper;
    private final VolunteerMapper volunteerMapper;
    private final SysUserMapper sysUserMapper;
    private final BuildingMapper buildingMapper;

    public HomeServiceController(HomeServiceMapper homeServiceMapper,
                                  AppointmentMapper appointmentMapper,
                                  VolunteerMapper volunteerMapper,
                                  SysUserMapper sysUserMapper,
                                  BuildingMapper buildingMapper) {
        this.homeServiceMapper = homeServiceMapper;
        this.appointmentMapper = appointmentMapper;
        this.volunteerMapper = volunteerMapper;
        this.sysUserMapper = sysUserMapper;
        this.buildingMapper = buildingMapper;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long volunteerId) {
        LambdaQueryWrapper<HomeService> wrapper = new LambdaQueryWrapper<HomeService>()
                .orderByDesc(HomeService::getCreatedAt);

        if (status != null) {
            wrapper.eq(HomeService::getStatus, status);
        }
        if (volunteerId != null) {
            wrapper.eq(HomeService::getVolunteerId, volunteerId);
        }

        List<HomeService> list = homeServiceMapper.selectList(wrapper);
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        for (HomeService homeService : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", homeService.getId());
            item.put("appointmentId", homeService.getAppointmentId());
            item.put("volunteerId", homeService.getVolunteerId());
            item.put("scheduledDate", homeService.getScheduledDate());
            item.put("scheduledTime", homeService.getScheduledTime());
            item.put("status", homeService.getStatus());
            item.put("createdAt", homeService.getCreatedAt());

            Appointment appointment = appointmentMapper.selectById(homeService.getAppointmentId());
            if (appointment != null) {
                item.put("homeAddress", appointment.getHomeAddress());
                item.put("serviceType", appointment.getServiceType());
                SysUser resident = sysUserMapper.selectById(appointment.getResidentId());
                if (resident != null) {
                    item.put("residentName", resident.getRealName());
                    item.put("residentPhone", resident.getPhone());
                    item.put("elderly", resident.getElderly());
                    item.put("disabled", resident.getDisabled());
                    if (resident.getBuildingId() != null) {
                        Building building = buildingMapper.selectById(resident.getBuildingId());
                        if (building != null) {
                            item.put("buildingName", building.getBuildingName());
                            item.put("roomNumber", resident.getRoomNumber());
                        }
                    }
                }
            }

            if (homeService.getVolunteerId() != null) {
                Volunteer volunteer = volunteerMapper.selectById(homeService.getVolunteerId());
                if (volunteer != null) {
                    item.put("volunteerNo", volunteer.getVolunteerNo());
                    SysUser barber = sysUserMapper.selectById(volunteer.getUserId());
                    if (barber != null) {
                        item.put("barberName", barber.getRealName());
                    }
                }
            }

            result.add(item);
        }

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        HomeService homeService = homeServiceMapper.selectById(id);
        if (homeService == null) {
            return Result.error("上门服务记录不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("homeService", homeService);

        Appointment appointment = appointmentMapper.selectById(homeService.getAppointmentId());
        detail.put("appointment", appointment);

        if (appointment != null) {
            SysUser resident = sysUserMapper.selectById(appointment.getResidentId());
            detail.put("resident", resident);
            if (resident != null && resident.getBuildingId() != null) {
                Building building = buildingMapper.selectById(resident.getBuildingId());
                detail.put("building", building);
            }
        }

        if (homeService.getVolunteerId() != null) {
            Volunteer volunteer = volunteerMapper.selectById(homeService.getVolunteerId());
            detail.put("volunteer", volunteer);
            if (volunteer != null) {
                SysUser barber = sysUserMapper.selectById(volunteer.getUserId());
                detail.put("barber", barber);
            }
        }

        return Result.success(detail);
    }

    @PostMapping
    @Transactional
    public Result<HomeService> create(@RequestBody HomeService homeService) {
        homeService.setStatus("PENDING");
        homeServiceMapper.insert(homeService);
        return Result.success("创建成功", homeService);
    }

    @PostMapping("/{id}/schedule")
    @Transactional
    public Result<HomeService> schedule(@PathVariable Long id, @RequestBody HomeService request) {
        HomeService homeService = homeServiceMapper.selectById(id);
        if (homeService == null) {
            return Result.error("上门服务记录不存在");
        }

        homeService.setVolunteerId(request.getVolunteerId());
        homeService.setScheduledDate(request.getScheduledDate());
        homeService.setScheduledTime(request.getScheduledTime());
        homeService.setStatus("SCHEDULED");
        homeServiceMapper.updateById(homeService);

        return Result.success("安排成功", homeService);
    }

    @PostMapping("/{id}/start")
    @Transactional
    public Result<HomeService> startService(@PathVariable Long id) {
        HomeService homeService = homeServiceMapper.selectById(id);
        if (homeService == null) {
            return Result.error("上门服务记录不存在");
        }

        homeService.setStatus("IN_PROGRESS");
        homeService.setActualArrivalAt(LocalDateTime.now());
        homeServiceMapper.updateById(homeService);

        Appointment appointment = appointmentMapper.selectById(homeService.getAppointmentId());
        if (appointment != null) {
            appointment.setStatus("IN_PROGRESS");
            appointment.setServiceStartAt(LocalDateTime.now());
            appointmentMapper.updateById(appointment);
        }

        return Result.success("服务已开始", homeService);
    }

    @PostMapping("/{id}/complete")
    @Transactional
    public Result<HomeService> completeService(@PathVariable Long id, @RequestParam(required = false) String feedback) {
        HomeService homeService = homeServiceMapper.selectById(id);
        if (homeService == null) {
            return Result.error("上门服务记录不存在");
        }

        homeService.setStatus("COMPLETED");
        homeService.setActualFinishAt(LocalDateTime.now());
        homeService.setFeedback(feedback);
        homeServiceMapper.updateById(homeService);

        Appointment appointment = appointmentMapper.selectById(homeService.getAppointmentId());
        if (appointment != null) {
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
        }

        return Result.success("服务已完成", homeService);
    }

    @PostMapping("/{id}/cancel")
    @Transactional
    public Result<HomeService> cancelService(@PathVariable Long id) {
        HomeService homeService = homeServiceMapper.selectById(id);
        if (homeService == null) {
            return Result.error("上门服务记录不存在");
        }

        homeService.setStatus("CANCELLED");
        homeServiceMapper.updateById(homeService);

        return Result.success("已取消", homeService);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HomeService homeService) {
        homeService.setId(id);
        homeServiceMapper.updateById(homeService);
        return Result.success();
    }
}
