package com.battery.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单数据传输对象
 */
@Data
public class WorkOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    private Long id;

    /**
     * 工单编号
     */
    private String orderNo;

    /**
     * 工单类型：1-故障维修，2-定期保养，3-电池更换，4-其他
     */
    private Integer orderType;

    /**
     * 工单类型名称
     */
    private String orderTypeName;

    /**
     * 工单标题
     */
    private String title;

    /**
     * 工单描述
     */
    private String description;

    /**
     * 关联车辆ID
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
     * 关联告警ID
     */
    private Long alertId;

    /**
     * 工单状态：0-待处理，1-处理中，2-已完成，3-已关闭
     */
    private Integer status;

    /**
     * 工单状态名称
     */
    private String statusName;

    /**
     * 优先级：1-低，2-中，3-高，4-紧急
     */
    private Integer priority;

    /**
     * 优先级名称
     */
    private String priorityName;

    /**
     * 指派人
     */
    private String assignee;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 计划完成时间
     */
    private LocalDateTime planFinishTime;

    /**
     * 实际完成时间
     */
    private LocalDateTime actualFinishTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
