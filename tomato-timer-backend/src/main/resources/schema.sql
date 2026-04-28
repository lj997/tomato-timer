-- 创建数据库
CREATE DATABASE IF NOT EXISTS tomato_timer DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tomato_timer;

-- 任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '任务标题',
    estimated_pomodoros INT DEFAULT 1 COMMENT '预估番茄数',
    completed_pomodoros INT DEFAULT 0 COMMENT '已完成番茄数',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/in_progress/completed',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 番茄记录表
CREATE TABLE IF NOT EXISTS pomodoro_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT COMMENT '关联任务ID',
    task_title VARCHAR(255) COMMENT '任务标题(冗余字段)',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    duration INT COMMENT '持续时间(分钟)',
    is_completed TINYINT DEFAULT 1 COMMENT '是否完成: 0-未完成, 1-完成',
    type VARCHAR(20) DEFAULT 'work' COMMENT '类型: work-工作, break-休息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='番茄记录表';
