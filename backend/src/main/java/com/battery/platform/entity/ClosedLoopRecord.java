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
 * 闭环管理记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("closed_loop_record")
public class ClosedLoopRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 记录编号
     */
    private String recordNo;

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
     * 关联工单ID
     */
    private Long workOrderId;

    /**
     * 闭环类型：1-告警闭环，2-工单闭环
     */
    private Integer loopType;

    /**
     * 闭环状态：0-待闭环，1-已闭环
     */
    private Integer status;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 处理措施
     */
    private String solution;

    /**
     * 处理结果
     */
    private String result;

    /**
     * 验证人
     */
    private String verifier;

    /**
     * 验证时间
     */
    private LocalDateTime verifyTime;

    /**
     * 验证意见
     */
    private String verifyComment;

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
