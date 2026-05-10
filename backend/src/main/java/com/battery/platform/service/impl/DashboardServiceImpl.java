package com.battery.platform.service.impl;

import com.battery.platform.dto.DashboardKpiDTO;
import com.battery.platform.entity.Alert;
import com.battery.platform.entity.Vehicle;
import com.battery.platform.mapper.AlertMapper;
import com.battery.platform.mapper.VehicleMapper;
import com.battery.platform.mapper.WorkOrderMapper;
import com.battery.platform.service.DashboardService;
import com.battery.platform.vo.AlertVO;
import com.battery.platform.vo.VehicleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VehicleMapper vehicleMapper;
    private final AlertMapper alertMapper;
    private final WorkOrderMapper workOrderMapper;

    @Override
    public DashboardKpiDTO getKpiData() {
        DashboardKpiDTO kpiDTO = new DashboardKpiDTO();

        // 车辆总数
        Integer totalVehicles = vehicleMapper.countTotalVehicles();
        kpiDTO.setTotalVehicles(totalVehicles);

        // 在线车辆数
        Integer onlineVehicles = vehicleMapper.countOnlineVehicles();
        kpiDTO.setOnlineVehicles(onlineVehicles);

        // 今日告警数
        Integer todayAlerts = alertMapper.countTodayAlerts();
        kpiDTO.setTodayAlerts(todayAlerts);

        // 未处理工单数
        Integer pendingWorkOrders = workOrderMapper.countPendingWorkOrders();
        kpiDTO.setPendingWorkOrders(pendingWorkOrders);

        // 风险车辆数
        Integer riskVehicles = vehicleMapper.countRiskVehicles();
        kpiDTO.setRiskVehicles(riskVehicles);

        // 在线率
        if (totalVehicles != null && totalVehicles > 0) {
            BigDecimal onlineRate = BigDecimal.valueOf(onlineVehicles)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalVehicles), 2, RoundingMode.HALF_UP);
            kpiDTO.setOnlineRate(onlineRate);
        } else {
            kpiDTO.setOnlineRate(BigDecimal.ZERO);
        }

        // 模拟数据：电池健康度平均值和今日行驶里程
        kpiDTO.setAvgSoh(new BigDecimal("92.5"));
        kpiDTO.setTodayMileage(new BigDecimal("12580.5"));

        return kpiDTO;
    }

    @Override
    public List<AlertVO> getRealtimeAlerts() {
        List<Alert> alerts = alertMapper.selectRealtimeAlerts();
        return alerts.stream().map(this::convertToAlertVO).collect(Collectors.toList());
    }

    @Override
    public List<VehicleVO> getRiskTop5Vehicles() {
        List<Vehicle> vehicles = vehicleMapper.selectTop5RiskVehicles();
        return vehicles.stream().map(this::convertToVehicleVO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 按类型统计
        List<Map<String, Object>> typeStats = alertMapper.countByType();
        statistics.put("byType", typeStats);

        // 按级别统计
        List<Map<String, Object>> levelStats = alertMapper.countByLevel();
        statistics.put("byLevel", levelStats);

        // 按日期统计（最近7天）
        // alertMapper.countByDate(LocalDateTime.now().minusDays(7));

        return statistics;
    }

    @Override
    public Map<String, Object> getWorkOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 按状态统计
        List<Map<String, Object>> statusStats = workOrderMapper.countByStatus();
        statistics.put("byStatus", statusStats);

        // 按类型统计
        List<Map<String, Object>> typeStats = workOrderMapper.countByType();
        statistics.put("byType", typeStats);

        return statistics;
    }

    private AlertVO convertToAlertVO(Alert alert) {
        AlertVO vo = new AlertVO();
        BeanUtils.copyProperties(alert, vo);
        vo.setAlertTypeName(getAlertTypeName(alert.getAlertType()));
        vo.setAlertLevelName(getAlertLevelName(alert.getAlertLevel()));
        vo.setStatusName(getAlertStatusName(alert.getStatus()));
        return vo;
    }

    private VehicleVO convertToVehicleVO(Vehicle vehicle) {
        VehicleVO vo = new VehicleVO();
        BeanUtils.copyProperties(vehicle, vo);
        vo.setStatusName(getVehicleStatusName(vehicle.getStatus()));
        vo.setRiskLevelName(getRiskLevelName(vehicle.getRiskLevel()));
        return vo;
    }

    private String getAlertTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "温度异常";
            case 2 -> "电压异常";
            case 3 -> "SOC异常";
            case 4 -> "绝缘故障";
            case 5 -> "其他";
            default -> "未知";
        };
    }

    private String getAlertLevelName(Integer level) {
        if (level == null) return "未知";
        return switch (level) {
            case 1 -> "一般";
            case 2 -> "严重";
            case 3 -> "紧急";
            default -> "未知";
        };
    }

    private String getAlertStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "未处理";
            case 1 -> "处理中";
            case 2 -> "已处理";
            default -> "未知";
        };
    }

    private String getVehicleStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "离线";
            case 1 -> "在线";
            case 2 -> "充电中";
            case 3 -> "行驶中";
            default -> "未知";
        };
    }

    private String getRiskLevelName(Integer level) {
        if (level == null) return "未知";
        return switch (level) {
            case 0 -> "正常";
            case 1 -> "低风险";
            case 2 -> "中风险";
            case 3 -> "高风险";
            default -> "未知";
        };
    }
}
