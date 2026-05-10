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
 * 工单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("work_order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 关联告警ID
     */
    private Long alertId;

    /**
     * 工单状态：0-待处理，1-处理中，2-已完成，3-已关闭
     */
    private Integer status;

    /**
     * 优先级：1-低，2-中，3-高，4-紧急
     */
    private Integer priority;

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

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    private Integer deleted;
}
