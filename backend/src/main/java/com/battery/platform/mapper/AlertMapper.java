package com.battery.platform.mapper;

import com.battery.platform.entity.Alert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警数据访问层
 */
@Mapper
public interface AlertMapper extends BaseMapper<Alert> {

    /**
     * 查询实时告警列表
     */
    @Select("SELECT * FROM alert WHERE status = 0 AND deleted = 0 ORDER BY alert_time DESC LIMIT 10")
    List<Alert> selectRealtimeAlerts();

    /**
     * 统计今日告警数
     */
    @Select("SELECT COUNT(*) FROM alert WHERE DATE(alert_time) = CURDATE() AND deleted = 0")
    Integer countTodayAlerts();

    /**
     * 统计告警数量（按类型分组）
     */
    @Select("SELECT alert_type as type, COUNT(*) as count FROM alert WHERE deleted = 0 GROUP BY alert_type")
    List<Map<String, Object>> countByType();

    /**
     * 统计告警数量（按级别分组）
     */
    @Select("SELECT alert_level as level, COUNT(*) as count FROM alert WHERE deleted = 0 GROUP BY alert_level")
    List<Map<String, Object>> countByLevel();

    /**
     * 统计告警数量（按日期分组）
     */
    @Select("SELECT DATE(alert_time) as date, COUNT(*) as count FROM alert WHERE alert_time >= #{startDate} AND deleted = 0 GROUP BY DATE(alert_time)")
    List<Map<String, Object>> countByDate(@Param("startDate") LocalDateTime startDate);
}
