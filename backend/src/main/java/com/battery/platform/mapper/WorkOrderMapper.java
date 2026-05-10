package com.battery.platform.mapper;

import com.battery.platform.entity.WorkOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 工单数据访问层
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /**
     * 统计未处理工单数
     */
    @Select("SELECT COUNT(*) FROM work_order WHERE status IN (0, 1) AND deleted = 0")
    Integer countPendingWorkOrders();

    /**
     * 统计工单数量（按状态分组）
     */
    @Select("SELECT status, COUNT(*) as count FROM work_order WHERE deleted = 0 GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 统计工单数量（按类型分组）
     */
    @Select("SELECT order_type as type, COUNT(*) as count FROM work_order WHERE deleted = 0 GROUP BY order_type")
    List<Map<String, Object>> countByType();

    /**
     * 根据工单编号查询
     */
    @Select("SELECT * FROM work_order WHERE order_no = #{orderNo} AND deleted = 0 LIMIT 1")
    WorkOrder selectByOrderNo(@Param("orderNo") String orderNo);
}
