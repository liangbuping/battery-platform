package com.battery.platform.mapper;

import com.battery.platform.entity.ClosedLoopRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 闭环管理记录数据访问层
 */
@Mapper
public interface ClosedLoopRecordMapper extends BaseMapper<ClosedLoopRecord> {

    /**
     * 根据车辆ID查询闭环记录
     */
    @Select("SELECT * FROM closed_loop_record WHERE vehicle_id = #{vehicleId} AND deleted = 0 ORDER BY create_time DESC")
    List<ClosedLoopRecord> selectByVehicleId(@Param("vehicleId") Long vehicleId);

    /**
     * 根据告警ID查询闭环记录
     */
    @Select("SELECT * FROM closed_loop_record WHERE alert_id = #{alertId} AND deleted = 0 LIMIT 1")
    ClosedLoopRecord selectByAlertId(@Param("alertId") Long alertId);

    /**
     * 根据工单ID查询闭环记录
     */
    @Select("SELECT * FROM closed_loop_record WHERE work_order_id = #{workOrderId} AND deleted = 0 LIMIT 1")
    ClosedLoopRecord selectByWorkOrderId(@Param("workOrderId") Long workOrderId);

    /**
     * 统计待闭环记录数
     */
    @Select("SELECT COUNT(*) FROM closed_loop_record WHERE status = 0 AND deleted = 0")
    Integer countPendingRecords();
}
