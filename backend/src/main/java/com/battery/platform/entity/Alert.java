package com.battery.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 告警实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("alert")
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 告警类型：1-温度异常，2-电压异常，3-SOC异常，4-绝缘故障，5-其他
     */
    private Integer alertType;

    /**
     * 告警级别：1-一般，2-严重，3-紧急
     */
    private Integer alertLevel;

    /**
     * 告警描述
     */
    private String description;

    /**
     * 告警状态：0-未处理，1-处理中，2-已处理
     */
    private Integer status;

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
     * 关联工单ID
     */
    private Long workOrderId;

    /**
     * 告警发生时间
     */
    private LocalDateTime alertTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    private Integer deleted;
}
