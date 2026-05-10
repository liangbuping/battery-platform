package com.battery.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 告警数据传输对象
 */
@Data
public class AlertDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警ID
     */
    private Long id;

    /**
     * 告警编号
     */
    private String alertNo;

    /**
     * 车辆ID
     */
    private Long vehicleId;

    /**
     * 车辆VIN码
     */
    private String vin;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 告警类型：1-温度异常，2-电压异常，3-SOC异常，4-绝缘故障，5-其他
     */
    private Integer alertType;

    /**
     * 告警类型名称
     */
    private String alertTypeName;

    /**
     * 告警级别：1-一般，2-严重，3-紧急
     */
    private Integer alertLevel;

    /**
     * 告警级别名称
     */
    private String alertLevelName;

    /**
     * 告警描述
     */
    private String description;

    /**
     * 告警状态：0-未处理，1-处理中，2-已处理
     */
    private Integer status;

    /**
     * 告警状态名称
     */
    private String statusName;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 告警发生时间
     */
    private LocalDateTime alertTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
