package com.battery.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 仪表盘KPI数据传输对象
 */
@Data
public class DashboardKpiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车辆总数
     */
    private Integer totalVehicles;

    /**
     * 在线车辆数
     */
    private Integer onlineVehicles;

    /**
     * 今日告警数
     */
    private Integer todayAlerts;

    /**
     * 未处理工单数
     */
    private Integer pendingWorkOrders;

    /**
     * 电池健康度平均值(%)
     */
    private BigDecimal avgSoh;

    /**
     * 风险车辆数
     */
    private Integer riskVehicles;

    /**
     * 今日行驶里程(km)
     */
    private BigDecimal todayMileage;

    /**
     * 在线率(%)
     */
    private BigDecimal onlineRate;
}
