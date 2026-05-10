package com.battery.platform.controller;

import com.battery.platform.dto.DashboardKpiDTO;
import com.battery.platform.service.DashboardService;
import com.battery.platform.utils.Result;
import com.battery.platform.vo.AlertVO;
import com.battery.platform.vo.VehicleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取KPI数据
     */
    @GetMapping("/kpi")
    public Result<DashboardKpiDTO> getKpiData() {
        DashboardKpiDTO kpiData = dashboardService.getKpiData();
        return Result.success(kpiData);
    }

    /**
     * 获取实时告警列表
     */
    @GetMapping("/alerts/realtime")
    public Result<List<AlertVO>> getRealtimeAlerts() {
        List<AlertVO> alerts = dashboardService.getRealtimeAlerts();
        return Result.success(alerts);
    }

    /**
     * 获取风险车辆TOP5
     */
    @GetMapping("/vehicles/risk-top5")
    public Result<List<VehicleVO>> getRiskTop5Vehicles() {
        List<VehicleVO> vehicles = dashboardService.getRiskTop5Vehicles();
        return Result.success(vehicles);
    }

    /**
     * 获取告警统计
     */
    @GetMapping("/statistics/alerts")
    public Result<Map<String, Object>> getAlertStatistics() {
        Map<String, Object> statistics = dashboardService.getAlertStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取工单统计
     */
    @GetMapping("/statistics/work-orders")
    public Result<Map<String, Object>> getWorkOrderStatistics() {
        Map<String, Object> statistics = dashboardService.getWorkOrderStatistics();
        return Result.success(statistics);
    }
}
