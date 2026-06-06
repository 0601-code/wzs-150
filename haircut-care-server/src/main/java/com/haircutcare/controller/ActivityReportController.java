package com.haircutcare.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.dto.AppointmentExportDTO;
import com.haircutcare.entity.*;
import com.haircutcare.mapper.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/activity-reports")
public class ActivityReportController {

    private final ActivityMapper activityMapper;
    private final AppointmentMapper appointmentMapper;
    private final SysUserMapper sysUserMapper;
    private final BuildingMapper buildingMapper;
    private final VolunteerMapper volunteerMapper;
    private final ToolDisinfestationMapper toolDisinfestationMapper;
    private final ToolMapper toolMapper;

    public ActivityReportController(ActivityMapper activityMapper,
                                     AppointmentMapper appointmentMapper,
                                     SysUserMapper sysUserMapper,
                                     BuildingMapper buildingMapper,
                                     VolunteerMapper volunteerMapper,
                                     ToolDisinfestationMapper toolDisinfestationMapper,
                                     ToolMapper toolMapper) {
        this.activityMapper = activityMapper;
        this.appointmentMapper = appointmentMapper;
        this.sysUserMapper = sysUserMapper;
        this.buildingMapper = buildingMapper;
        this.volunteerMapper = volunteerMapper;
        this.toolDisinfestationMapper = toolDisinfestationMapper;
        this.toolMapper = toolMapper;
    }

    @GetMapping("/{id}/statistics")
    public Result<Map<String, Object>> getStatistics(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        List<Appointment> appointments = appointmentMapper.selectList(
            new LambdaQueryWrapper<Appointment>().eq(Appointment::getActivityId, id)
        );

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("activity", activity);
        statistics.put("totalAppointments", appointments.size());
        statistics.put("confirmedCount", appointments.stream().filter(a -> "CONFIRMED".equals(a.getStatus())).count());
        statistics.put("checkedInCount", appointments.stream().filter(a -> "CHECKED_IN".equals(a.getStatus())).count());
        statistics.put("inProgressCount", appointments.stream().filter(a -> "IN_PROGRESS".equals(a.getStatus())).count());
        statistics.put("completedCount", appointments.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count());
        statistics.put("cancelledCount", appointments.stream().filter(a -> "CANCELLED".equals(a.getStatus())).count());
        statistics.put("homeServiceCount", appointments.stream().filter(a -> Boolean.TRUE.equals(a.getNeedHomeService())).count());
        statistics.put("elderlyCount", 0);
        statistics.put("disabledCount", 0);

        Set<Long> volunteerIds = new HashSet<>();
        for (Appointment appt : appointments) {
            if (appt.getResidentId() != null) {
                SysUser resident = sysUserMapper.selectById(appt.getResidentId());
                if (resident != null) {
                    if (Boolean.TRUE.equals(resident.getElderly())) {
                        statistics.put("elderlyCount", (Integer) statistics.get("elderlyCount") + 1);
                    }
                    if (Boolean.TRUE.equals(resident.getDisabled())) {
                        statistics.put("disabledCount", (Integer) statistics.get("disabledCount") + 1);
                    }
                }
            }
            if (appt.getVolunteerId() != null) {
                volunteerIds.add(appt.getVolunteerId());
            }
        }
        statistics.put("volunteerCount", volunteerIds.size());

        Long totalDisinfestations = toolDisinfestationMapper.selectCount(
            new LambdaQueryWrapper<ToolDisinfestation>().eq(ToolDisinfestation::getStatus, "COMPLETED")
        );
        statistics.put("totalDisinfestations", totalDisinfestations);

        return Result.success(statistics);
    }

    @GetMapping("/{id}/export")
    public void exportAppointments(@PathVariable Long id, HttpServletResponse response) throws IOException {
        List<Appointment> appointments = appointmentMapper.selectList(
            new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getActivityId, id)
                .orderByAsc(Appointment::getQueueNumber)
        );

        List<AppointmentExportDTO> exportList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Appointment appt : appointments) {
            AppointmentExportDTO dto = new AppointmentExportDTO();
            dto.setAppointmentNo(appt.getAppointmentNo());
            dto.setQueueNumber(appt.getQueueNumber());
            dto.setServiceType(getServiceTypeName(appt.getServiceType()));
            dto.setNeedHomeService(Boolean.TRUE.equals(appt.getNeedHomeService()) ? "是" : "否");
            dto.setHomeAddress(appt.getHomeAddress());
            dto.setStatus(getStatusName(appt.getStatus()));
            dto.setCheckInAt(appt.getCheckInAt() != null ? appt.getCheckInAt().format(formatter) : "");
            dto.setServiceStartAt(appt.getServiceStartAt() != null ? appt.getServiceStartAt().format(formatter) : "");
            dto.setServiceEndAt(appt.getServiceEndAt() != null ? appt.getServiceEndAt().format(formatter) : "");
            dto.setCreatedAt(appt.getCreatedAt() != null ? appt.getCreatedAt().format(formatter) : "");

            if (appt.getResidentId() != null) {
                SysUser resident = sysUserMapper.selectById(appt.getResidentId());
                if (resident != null) {
                    dto.setResidentName(resident.getRealName());
                    dto.setPhone(resident.getPhone());
                    dto.setRoomNumber(resident.getRoomNumber());
                    dto.setElderly(Boolean.TRUE.equals(resident.getElderly()) ? "是" : "否");
                    dto.setDisabled(Boolean.TRUE.equals(resident.getDisabled()) ? "是" : "否");
                    if (resident.getBuildingId() != null) {
                        Building building = buildingMapper.selectById(resident.getBuildingId());
                        if (building != null) {
                            dto.setBuildingName(building.getBuildingName());
                        }
                    }
                }
            }

            if (appt.getVolunteerId() != null) {
                Volunteer volunteer = volunteerMapper.selectById(appt.getVolunteerId());
                if (volunteer != null && volunteer.getUserId() != null) {
                    SysUser barber = sysUserMapper.selectById(volunteer.getUserId());
                    if (barber != null) {
                        dto.setBarberName(barber.getRealName());
                    }
                }
            }

            exportList.add(dto);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("活动参与名单.xlsx", StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        EasyExcel.write(response.getOutputStream(), AppointmentExportDTO.class)
                .sheet("参与名单")
                .doWrite(exportList);
    }

    private String getServiceTypeName(String type) {
        if (type == null) return "普通理发";
        switch (type) {
            case "SENIOR": return "老人剪发";
            case "SPECIAL": return "特殊需求";
            default: return "普通理发";
        }
    }

    private String getStatusName(String status) {
        if (status == null) return "";
        switch (status) {
            case "PENDING": return "待确认";
            case "CONFIRMED": return "已确认";
            case "CHECKED_IN": return "已签到";
            case "IN_PROGRESS": return "服务中";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }
}
