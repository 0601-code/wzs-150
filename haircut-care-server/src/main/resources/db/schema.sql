CREATE DATABASE IF NOT EXISTS haircut_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE haircut_care;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    real_name VARCHAR(64) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(32) NOT NULL DEFAULT 'RESIDENT' COMMENT '角色：ADMIN-管理员，BARBER-理发师，RESIDENT-居民',
    building_id BIGINT COMMENT '所属楼栋ID',
    room_number VARCHAR(32) COMMENT '房间号',
    elderly TINYINT(1) DEFAULT 0 COMMENT '是否老人',
    disabled TINYINT(1) DEFAULT 0 COMMENT '是否行动不便',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS building (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    building_no VARCHAR(32) NOT NULL UNIQUE COMMENT '楼栋号',
    building_name VARCHAR(64) COMMENT '楼栋名称',
    unit_count INT DEFAULT 0 COMMENT '单元数',
    floor_count INT DEFAULT 0 COMMENT '层数',
    description VARCHAR(255) COMMENT '描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区楼栋表';

CREATE TABLE IF NOT EXISTS volunteer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    volunteer_no VARCHAR(32) UNIQUE COMMENT '志愿者编号',
    skill_level VARCHAR(32) DEFAULT 'NORMAL' COMMENT '技能等级：NORMAL-普通，SKILLED-熟练，EXPERT-资深',
    speciality VARCHAR(255) COMMENT '专长描述',
    available TINYINT(1) DEFAULT 1 COMMENT '是否可接单',
    total_served INT DEFAULT 0 COMMENT '累计服务人数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='志愿者（理发师）表';

CREATE TABLE IF NOT EXISTS tool_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type_name VARCHAR(64) NOT NULL UNIQUE COMMENT '工具类型名称',
    type_code VARCHAR(32) UNIQUE COMMENT '工具类型编码',
    disinfection_minutes INT DEFAULT 30 COMMENT '建议消毒时长（分钟）',
    description VARCHAR(255) COMMENT '描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具类型表';

CREATE TABLE IF NOT EXISTS tool (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tool_no VARCHAR(32) NOT NULL UNIQUE COMMENT '工具编号',
    tool_name VARCHAR(64) NOT NULL COMMENT '工具名称',
    type_id BIGINT NOT NULL COMMENT '工具类型ID',
    brand VARCHAR(64) COMMENT '品牌',
    model VARCHAR(64) COMMENT '型号',
    status VARCHAR(32) DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可用，IN_USE-使用中，DISINFECTING-消毒中，MAINTENANCE-维护中',
    purchase_date DATE COMMENT '采购日期',
    last_disinfestation_at DATETIME COMMENT '上次消毒时间',
    use_count INT DEFAULT 0 COMMENT '累计使用次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    INDEX idx_type_id (type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具表';

CREATE TABLE IF NOT EXISTS tool_usage_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tool_id BIGINT NOT NULL COMMENT '工具ID',
    appointment_id BIGINT COMMENT '关联预约ID',
    volunteer_id BIGINT COMMENT '使用志愿者ID',
    used_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '使用时间',
    returned_at DATETIME COMMENT '归还时间',
    status VARCHAR(32) DEFAULT 'IN_USE' COMMENT '状态：IN_USE-使用中，RETURNED-已归还',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_tool_id (tool_id),
    INDEX idx_appointment_id (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具使用记录表';

CREATE TABLE IF NOT EXISTS tool_disinfestation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tool_id BIGINT NOT NULL COMMENT '工具ID',
    operator_id BIGINT COMMENT '操作员ID',
    start_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '消毒开始时间',
    end_at DATETIME COMMENT '消毒结束时间',
    disinfection_method VARCHAR(64) COMMENT '消毒方式',
    duration_minutes INT COMMENT '实际消毒时长（分钟）',
    status VARCHAR(32) DEFAULT 'PROCESSING' COMMENT '状态：PROCESSING-进行中，COMPLETED-已完成',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_tool_id (tool_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具消毒记录表';

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    activity_name VARCHAR(128) NOT NULL COMMENT '活动名称',
    activity_date DATE NOT NULL COMMENT '活动日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    location VARCHAR(128) COMMENT '活动地点',
    max_capacity INT DEFAULT 50 COMMENT '最大容纳人数',
    status VARCHAR(32) DEFAULT 'UPCOMING' COMMENT '状态：UPCOMING-未开始，ONGOING-进行中，COMPLETED-已结束',
    description VARCHAR(500) COMMENT '活动描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

CREATE TABLE IF NOT EXISTS time_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    slot_date DATE NOT NULL COMMENT '日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    max_count INT DEFAULT 10 COMMENT '该时段最大预约数',
    current_count INT DEFAULT 0 COMMENT '当前预约数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_activity_id (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时段表';

CREATE TABLE IF NOT EXISTS appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    appointment_no VARCHAR(32) UNIQUE COMMENT '预约编号',
    resident_id BIGINT NOT NULL COMMENT '居民ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    time_slot_id BIGINT COMMENT '时段ID',
    service_type VARCHAR(32) DEFAULT 'NORMAL' COMMENT '服务类型：NORMAL-普通理发，SENIOR-老人剪发，SPECIAL-特殊需求',
    need_home_service TINYINT(1) DEFAULT 0 COMMENT '是否需要上门服务',
    home_address VARCHAR(255) COMMENT '上门地址',
    volunteer_id BIGINT COMMENT '分配的理发师ID',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认，CONFIRMED-已确认，CHECKED_IN-已签到，IN_PROGRESS-服务中，COMPLETED-已完成，CANCELLED-已取消',
    queue_number INT COMMENT '排队号',
    check_in_at DATETIME COMMENT '签到时间',
    service_start_at DATETIME COMMENT '服务开始时间',
    service_end_at DATETIME COMMENT '服务结束时间',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    INDEX idx_resident_id (resident_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_volunteer_id (volunteer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

CREATE TABLE IF NOT EXISTS home_service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    appointment_id BIGINT NOT NULL COMMENT '预约ID',
    volunteer_id BIGINT NOT NULL COMMENT '上门理发师ID',
    scheduled_date DATE COMMENT '计划上门日期',
    scheduled_time TIME COMMENT '计划上门时间',
    actual_arrival_at DATETIME COMMENT '实际到达时间',
    actual_finish_at DATETIME COMMENT '实际完成时间',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态：PENDING-待安排，SCHEDULED-已安排，IN_PROGRESS-进行中，COMPLETED-已完成，CANCELLED-已取消',
    feedback VARCHAR(500) COMMENT '反馈',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_volunteer_id (volunteer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上门服务安排表';

INSERT INTO building (building_no, building_name, unit_count, floor_count, description) VALUES
('B1', '1号楼', 3, 6, '小区东门第一栋'),
('B2', '2号楼', 2, 6, '1号楼北侧'),
('B3', '3号楼', 3, 11, '高层电梯楼'),
('B4', '4号楼', 2, 11, '小区中心位置'),
('B5', '5号楼', 2, 6, '靠近社区医院');

INSERT INTO tool_type (type_name, type_code, disinfection_minutes, description) VALUES
('理发剪', 'SCISSORS', 30, '专业理发剪刀'),
('电推剪', 'CLIPPER', 30, '电动推剪'),
('梳子', 'COMB', 15, '理发梳子'),
('围布', 'CAPE', 20, '理发围布'),
('剃刀', 'RAZOR', 45, '剃须/修边剃刀');

INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '13800000000', 'ADMIN', 1),
('barber1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张师傅', '13800000001', 'BARBER', 1),
('barber2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李师傅', '13800000002', 'BARBER', 1),
('barber3', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王师傅', '13800000003', 'BARBER', 1);

INSERT INTO volunteer (user_id, volunteer_no, skill_level, speciality, available) VALUES
(2, 'V001', 'EXPERT', '精通老人剪发、平头', 1),
(3, 'V002', 'SKILLED', '擅长时尚发型', 1),
(4, 'V003', 'NORMAL', '基础理发', 1);

INSERT INTO tool (tool_no, tool_name, type_id, brand, status) VALUES
('T001', '专业理发剪A', 1, '沙宣', 'AVAILABLE'),
('T002', '专业理发剪B', 1, '沙宣', 'AVAILABLE'),
('T003', '电推剪A', 2, '飞利浦', 'AVAILABLE'),
('T004', '电推剪B', 2, '飞科', 'AVAILABLE'),
('T005', '电推剪C', 2, '飞利浦', 'AVAILABLE'),
('T006', '理发梳A', 3, '普通', 'AVAILABLE'),
('T007', '理发梳B', 3, '普通', 'AVAILABLE'),
('T008', '理发围布A', 4, '普通', 'AVAILABLE'),
('T009', '理发围布B', 4, '普通', 'AVAILABLE'),
('T010', '修面剃刀A', 5, '吉利', 'AVAILABLE');

INSERT INTO sys_user (username, password, real_name, phone, role, building_id, room_number, elderly, disabled, status) VALUES
('resident1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王大爷', '13900000001', 'RESIDENT', 1, '101', 1, 0, 1),
('resident2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李奶奶', '13900000002', 'RESIDENT', 1, '203', 1, 1, 1),
('resident3', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张先生', '13900000003', 'RESIDENT', 2, '302', 0, 0, 1),
('resident4', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '陈阿姨', '13900000004', 'RESIDENT', 3, '501', 1, 0, 1),
('resident5', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '刘大爷', '13900000005', 'RESIDENT', 4, '1102', 1, 1, 1);

INSERT INTO activity (activity_name, activity_date, start_time, end_time, location, max_capacity, status, description) VALUES
('2024年6月公益理发日', '2024-06-15', '09:00:00', '12:00:00', '社区活动中心', 50, 'UPCOMING', '每月一次的公益理发活动，面向社区所有居民，老人和行动不便者优先。');

INSERT INTO time_slot (activity_id, slot_date, start_time, end_time, max_count) VALUES
(1, '2024-06-15', '09:00:00', '09:30:00', 10),
(1, '2024-06-15', '09:30:00', '10:00:00', 10),
(1, '2024-06-15', '10:00:00', '10:30:00', 10),
(1, '2024-06-15', '10:30:00', '11:00:00', 10),
(1, '2024-06-15', '11:00:00', '11:30:00', 10),
(1, '2024-06-15', '11:30:00', '12:00:00', 10);
