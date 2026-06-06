-- 修复已入库的中文乱码初始数据（数据库已初始化过时使用）
-- 用法: mysql -uroot -p --default-character-set=utf8mb4 haircut_care < fix-charset-data.sql
-- Docker: docker exec -i haircut-care-mysql mysql -uroot -proot123456 --default-character-set=utf8mb4 haircut_care < haircut-care-server/src/main/resources/db/fix-charset-data.sql

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE haircut_care;

UPDATE building SET building_name = '1号楼', description = '小区东门第一栋' WHERE building_no = 'B1';
UPDATE building SET building_name = '2号楼', description = '1号楼北侧' WHERE building_no = 'B2';
UPDATE building SET building_name = '3号楼', description = '高层电梯楼' WHERE building_no = 'B3';
UPDATE building SET building_name = '4号楼', description = '小区中心位置' WHERE building_no = 'B4';
UPDATE building SET building_name = '5号楼', description = '靠近社区医院' WHERE building_no = 'B5';

UPDATE tool_type SET type_name = '理发剪', description = '专业理发剪刀' WHERE type_code = 'SCISSORS';
UPDATE tool_type SET type_name = '电推剪', description = '电动推剪' WHERE type_code = 'CLIPPER';
UPDATE tool_type SET type_name = '梳子', description = '理发梳子' WHERE type_code = 'COMB';
UPDATE tool_type SET type_name = '围布', description = '理发围布' WHERE type_code = 'CAPE';
UPDATE tool_type SET type_name = '剃刀', description = '剃须/修边剃刀' WHERE type_code = 'RAZOR';

UPDATE sys_user SET real_name = '系统管理员' WHERE username = 'admin';
UPDATE sys_user SET real_name = '张师傅' WHERE username = 'barber1';
UPDATE sys_user SET real_name = '李师傅' WHERE username = 'barber2';
UPDATE sys_user SET real_name = '王师傅' WHERE username = 'barber3';
UPDATE sys_user SET real_name = '王大爷' WHERE username = 'resident1';
UPDATE sys_user SET real_name = '李奶奶' WHERE username = 'resident2';
UPDATE sys_user SET real_name = '张先生' WHERE username = 'resident3';
UPDATE sys_user SET real_name = '陈阿姨' WHERE username = 'resident4';
UPDATE sys_user SET real_name = '刘大爷' WHERE username = 'resident5';

UPDATE volunteer SET speciality = '精通老人剪发、平头' WHERE volunteer_no = 'V001';
UPDATE volunteer SET speciality = '擅长时尚发型' WHERE volunteer_no = 'V002';
UPDATE volunteer SET speciality = '基础理发' WHERE volunteer_no = 'V003';

UPDATE tool SET tool_name = '专业理发剪A', brand = '沙宣' WHERE tool_no = 'T001';
UPDATE tool SET tool_name = '专业理发剪B', brand = '沙宣' WHERE tool_no = 'T002';
UPDATE tool SET tool_name = '电推剪A', brand = '飞利浦' WHERE tool_no = 'T003';
UPDATE tool SET tool_name = '电推剪B', brand = '飞科' WHERE tool_no = 'T004';
UPDATE tool SET tool_name = '电推剪C', brand = '飞利浦' WHERE tool_no = 'T005';
UPDATE tool SET tool_name = '理发梳A', brand = '普通' WHERE tool_no = 'T006';
UPDATE tool SET tool_name = '理发梳B', brand = '普通' WHERE tool_no = 'T007';
UPDATE tool SET tool_name = '理发围布A', brand = '普通' WHERE tool_no = 'T008';
UPDATE tool SET tool_name = '理发围布B', brand = '普通' WHERE tool_no = 'T009';
UPDATE tool SET tool_name = '修面剃刀A', brand = '吉利' WHERE tool_no = 'T010';

UPDATE activity SET
    activity_name = '2024年6月公益理发日',
    location = '社区活动中心',
    description = '每月一次的公益理发活动，面向社区所有居民，老人和行动不便者优先。'
WHERE activity_date = '2024-06-15';
