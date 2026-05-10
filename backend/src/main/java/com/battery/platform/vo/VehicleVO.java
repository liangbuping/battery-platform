package com.battery.platform.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆视图对象
 */
@Data
public class VehicleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车辆ID
     */
    private Long id;

    /**
     * 车辆VIN码
     */
    private String vin;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车辆型号
     */
    private String model;

    /**
     * 电池组编号
     */
    private String batteryCode;

    /**
     * 车辆状态：0-离线，1-在线，2-充电中，3-行驶中
     */
    private Integer status;

    /**
     * 车辆状态名称
     */
    private String statusName;

    /**
     * 电池SOC(%)
     */
    private Integer soc;

    /**
     * 电池SOH(%)
     */
    private Integer soh;

    /**
     * 电池温度(℃)
     */
    private BigDecimal temperature;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 风险等级：0-正常，1-低风险，2-中风险，3-高风险
     */
    private Integer riskLevel;

    /**
     * 风险等级名称
     */
    private String riskLevelName;

    /**
     * 累计里程(km)
     */
    private BigDecimal totalMileage;

    /**
     * 所属运营商
     */
    private String operator;

    /**
     * 最后上报时间
     */
    private LocalDateTime lastReportTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
