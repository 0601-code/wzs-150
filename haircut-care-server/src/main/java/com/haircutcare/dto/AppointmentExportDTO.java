package com.haircutcare.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AppointmentExportDTO {

    @ExcelProperty("预约编号")
    private String appointmentNo;

    @ExcelProperty("排队号")
    private Integer queueNumber;

    @ExcelProperty("居民姓名")
    private String residentName;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("楼栋")
    private String buildingName;

    @ExcelProperty("房间号")
    private String roomNumber;

    @ExcelProperty("是否老人")
    private String elderly;

    @ExcelProperty("是否行动不便")
    private String disabled;

    @ExcelProperty("服务类型")
    private String serviceType;

    @ExcelProperty("是否上门")
    private String needHomeService;

    @ExcelProperty("上门地址")
    private String homeAddress;

    @ExcelProperty("理发师")
    private String barberName;

    @ExcelProperty("预约状态")
    private String status;

    @ExcelProperty("签到时间")
    private String checkInAt;

    @ExcelProperty("服务开始时间")
    private String serviceStartAt;

    @ExcelProperty("服务结束时间")
    private String serviceEndAt;

    @ExcelProperty("预约时间")
    private String createdAt;
}
