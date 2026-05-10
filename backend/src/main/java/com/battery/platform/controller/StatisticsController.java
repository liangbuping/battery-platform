package com.battery.platform.controller;

import com.battery.platform.service.AlertService;
import com.battery.platform.service.VehicleService;
import com.battery.platform.service.WorkOrderService;
import com.battery.platform.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final VehicleService vehicleService;
    private final AlertService alertService;
    private final WorkOrderService workOrderService;

    /**
     * 获取综合统计数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 车辆统计
        Map<String, Object> vehicleStats = vehicleService.getStatistics();
        overview.put("vehicle", vehicleStats);

        // 告警统计
        Map<String, Object> alertStats = alertService.getStatistics();
        overview.put("alert", alertStats);

        // 工单统计
        Map<String, Object> workOrderStats = workOrderService.getStatistics();
        overview.put("workOrder", workOrderStats);

        return Result.success(overview);
    }
}
