-- 电池安全管理平台数据库初始化脚本
-- 数据库: battery_platform

-- 创建数据库
CREATE DATABASE IF NOT EXISTS battery_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE battery_platform;

-- 车辆表
CREATE TABLE IF NOT EXISTS vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    vin VARCHAR(17) NOT NULL COMMENT '车辆VIN码',
    plate_number VARCHAR(20) COMMENT '车牌号',
    model VARCHAR(50) COMMENT '车辆型号',
    battery_code VARCHAR(50) COMMENT '电池组编号',
    status TINYINT DEFAULT 0 COMMENT '车辆状态：0-离线，1-在线，2-充电中，3-行驶中',
    soc INT COMMENT '电池SOC(%)',
    soh INT COMMENT '电池SOH(%)',
    temperature DECIMAL(5,2) COMMENT '电池温度(℃)',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    risk_level TINYINT DEFAULT 0 COMMENT '风险等级：0-正常，1-低风险，2-中风险，3-高风险',
    total_mileage DECIMAL(10,2) COMMENT '累计里程(km)',
    operator VARCHAR(50) COMMENT '所属运营商',
    last_report_time DATETIME COMMENT '最后上报时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_vin (vin),
    INDEX idx_status (status),
    INDEX idx_risk_level (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';

-- 告警表
CREATE TABLE IF NOT EXISTS alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    alert_no VARCHAR(32) NOT NULL COMMENT '告警编号',
    vehicle_id BIGINT COMMENT '车辆ID',
    vin VARCHAR(17) COMMENT '车辆VIN码',
    alert_type TINYINT COMMENT '告警类型：1-温度异常，2-电压异常，3-SOC异常，4-绝缘故障，5-其他',
    alert_level TINYINT COMMENT '告警级别：1-一般，2-严重，3-紧急',
    description VARCHAR(500) COMMENT '告警描述',
    status TINYINT DEFAULT 0 COMMENT '告警状态：0-未处理，1-处理中，2-已处理',
    handler VARCHAR(50) COMMENT '处理人',
    handle_time DATETIME COMMENT '处理时间',
    handle_remark VARCHAR(500) COMMENT '处理备注',
    work_order_id BIGINT COMMENT '关联工单ID',
    alert_time DATETIME COMMENT '告警发生时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_alert_no (alert_no),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_vin (vin),
    INDEX idx_status (status),
    INDEX idx_alert_time (alert_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警信息表';

-- 工单表
CREATE TABLE IF NOT EXISTS work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL COMMENT '工单编号',
    order_type TINYINT COMMENT '工单类型：1-故障维修，2-定期保养，3-电池更换，4-其他',
    title VARCHAR(100) COMMENT '工单标题',
    description VARCHAR(500) COMMENT '工单描述',
    vehicle_id BIGINT COMMENT '关联车辆ID',
    vin VARCHAR(17) COMMENT '车辆VIN码',
    alert_id BIGINT COMMENT '关联告警ID',
    status TINYINT DEFAULT 0 COMMENT '工单状态：0-待处理，1-处理中，2-已完成，3-已关闭',
    priority TINYINT DEFAULT 2 COMMENT '优先级：1-低，2-中，3-高，4-紧急',
    assignee VARCHAR(50) COMMENT '指派人',
    creator VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    plan_finish_time DATETIME COMMENT '计划完成时间',
    actual_finish_time DATETIME COMMENT '实际完成时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_order_no (order_no),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_vin (vin),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单信息表';

-- 闭环管理记录表
CREATE TABLE IF NOT EXISTS closed_loop_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    record_no VARCHAR(32) NOT NULL COMMENT '记录编号',
    vehicle_id BIGINT COMMENT '关联车辆ID',
    vin VARCHAR(17) COMMENT '车辆VIN码',
    alert_id BIGINT COMMENT '关联告警ID',
    work_order_id BIGINT COMMENT '关联工单ID',
    loop_type TINYINT COMMENT '闭环类型：1-告警闭环，2-工单闭环',
    status TINYINT DEFAULT 0 COMMENT '闭环状态：0-待闭环，1-已闭环',
    problem_desc VARCHAR(500) COMMENT '问题描述',
    solution VARCHAR(500) COMMENT '处理措施',
    result VARCHAR(500) COMMENT '处理结果',
    verifier VARCHAR(50) COMMENT '验证人',
    verify_time DATETIME COMMENT '验证时间',
    verify_comment VARCHAR(500) COMMENT '验证意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_record_no (record_no),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_alert_id (alert_id),
    INDEX idx_work_order_id (work_order_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闭环管理记录表';

-- 插入测试数据

-- 车辆测试数据
INSERT INTO vehicle (vin, plate_number, model, battery_code, status, soc, soh, temperature, longitude, latitude, risk_level, total_mileage, operator, last_report_time) VALUES
('LSVAG2180E2100001', '京A12345', 'Model S', 'BAT001', 1, 85, 95, 25.5, 116.4074, 39.9042, 0, 15234.5, '北京运营中心', NOW()),
('LSVAG2180E2100002', '京A12346', 'Model 3', 'BAT002', 1, 72, 88, 32.8, 116.3974, 39.9142, 2, 28567.3, '北京运营中心', NOW()),
('LSVAG2180E2100003', '京A12347', 'Model X', 'BAT003', 2, 45, 92, 28.3, 116.4174, 39.9242, 1, 32145.7, '北京运营中心', NOW()),
('LSVAG2180E2100004', '京A12348', 'Model Y', 'BAT004', 1, 90, 97, 24.1, 116.4274, 39.9342, 0, 18923.4, '北京运营中心', NOW()),
('LSVAG2180E2100005', '京A12349', 'Model S', 'BAT005', 3, 65, 85, 35.6, 116.4374, 39.9442, 3, 45678.9, '北京运营中心', NOW()),
('LSVAG2180E2100006', '沪B56789', 'Model 3', 'BAT006', 1, 78, 93, 26.7, 121.4737, 31.2304, 0, 23456.8, '上海运营中心', NOW()),
('LSVAG2180E2100007', '沪B56790', 'Model X', 'BAT007', 0, 0, 89, 22.0, 121.4837, 31.2404, 1, 12345.6, '上海运营中心', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('LSVAG2180E2100008', '沪B56791', 'Model Y', 'BAT008', 1, 82, 94, 27.5, 121.4937, 31.2504, 0, 34567.2, '上海运营中心', NOW()),
('LSVAG2180E2100009', '沪B56792', 'Model S', 'BAT009', 2, 38, 87, 31.2, 121.5037, 31.2604, 2, 28901.5, '上海运营中心', NOW()),
('LSVAG2180E2100010', '沪B56793', 'Model 3', 'BAT010', 1, 91, 96, 23.8, 121.5137, 31.2704, 0, 16789.3, '上海运营中心', NOW());

-- 告警测试数据
INSERT INTO alert (alert_no, vehicle_id, vin, alert_type, alert_level, description, status, alert_time) VALUES
('ALT202405010001', 2, 'LSVAG2180E2100002', 1, 2, '电池温度过高，当前温度32.8℃', 0, NOW()),
('ALT202405010002', 5, 'LSVAG2180E2100005', 1, 3, '电池温度严重超标，当前温度35.6℃', 0, NOW()),
('ALT202405010003', 3, 'LSVAG2180E2100003', 3, 1, 'SOC偏低，当前电量45%', 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('ALT202405010004', 9, 'LSVAG2180E2100009', 2, 2, '电压异常波动', 0, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('ALT202405010005', 7, 'LSVAG2180E2100007', 4, 1, '绝缘电阻偏低', 2, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('ALT202405010006', 2, 'LSVAG2180E2100002', 2, 1, '单体电压不均衡', 0, DATE_SUB(NOW(), INTERVAL 15 MINUTE));

-- 工单测试数据
INSERT INTO work_order (order_no, order_type, title, description, vehicle_id, vin, alert_id, status, priority, assignee, creator, plan_finish_time) VALUES
('WO202405010001', 1, '电池温度异常处理', '车辆京A12346电池温度过高，需现场检查', 2, 'LSVAG2180E2100002', 1, 1, 3, '张三', '系统', DATE_ADD(NOW(), INTERVAL 2 HOUR)),
('WO202405010002', 1, '紧急温度故障处理', '车辆京A12349电池温度严重超标，需立即处理', 5, 'LSVAG2180E2100005', 2, 0, 4, '李四', '系统', DATE_ADD(NOW(), INTERVAL 1 HOUR)),
('WO202405010003', 2, '定期保养', '车辆京A12347定期保养', 3, 'LSVAG2180E2100003', NULL, 1, 2, '王五', '系统', DATE_ADD(NOW(), INTERVAL 24 HOUR)),
('WO202405010004', 1, '电压异常检修', '车辆沪B56791电压异常波动检修', 9, 'LSVAG2180E2100009', 4, 0, 3, '赵六', '系统', DATE_ADD(NOW(), INTERVAL 4 HOUR)),
('WO202405010005', 3, '电池组更换', '车辆沪B56790电池老化需更换', 7, 'LSVAG2180E2100007', NULL, 2, 2, '钱七', '系统', DATE_ADD(NOW(), INTERVAL 48 HOUR));

-- 闭环记录测试数据
INSERT INTO closed_loop_record (record_no, vehicle_id, vin, alert_id, work_order_id, loop_type, status, problem_desc, solution, result, verifier, verify_time, verify_comment) VALUES
('CL202405010001', 7, 'LSVAG2180E2100007', 5, 5, 1, 1, '绝缘电阻偏低', '更换绝缘垫片', '绝缘电阻恢复正常', '质量部', NOW(), '验证通过，问题已解决');
