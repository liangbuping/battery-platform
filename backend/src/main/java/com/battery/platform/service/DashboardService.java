package com.battery.platform.service;

import com.battery.platform.dto.DashboardKpiDTO;
import com.battery.platform.vo.AlertVO;
import com.battery.platform.vo.VehicleVO;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取KPI数据
     */
    DashboardKpiDTO getKpiData();

    /**
     * 获取实时告警列表
     */
    List<AlertVO> getRealtimeAlerts();

    /**
     * 获取风险车辆TOP5
     */
    List<VehicleVO> getRiskTop5Vehicles();

    /**
     * 获取告警统计
     */
    Map<String, Object> getAlertStatistics();

    /**
     * 获取工单统计
     */
    Map<String, Object> getWorkOrderStatistics();
}
