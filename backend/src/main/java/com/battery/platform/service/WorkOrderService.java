package com.battery.platform.service;

import com.battery.platform.dto.PageResult;
import com.battery.platform.dto.WorkOrderDTO;
import com.battery.platform.entity.WorkOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 工单服务接口
 */
public interface WorkOrderService extends IService<WorkOrder> {

    /**
     * 分页查询工单列表
     */
    PageResult<WorkOrderDTO> getWorkOrderPage(Integer current, Integer size, Integer orderType, Integer status, Integer priority);

    /**
     * 获取工单详情
     */
    WorkOrderDTO getWorkOrderDetail(Long id);

    /**
     * 创建工单
     */
    boolean createWorkOrder(WorkOrder workOrder);

    /**
     * 更新工单
     */
    boolean updateWorkOrder(Long id, WorkOrder workOrder);

    /**
     * 获取工单统计
     */
    Map<String, Object> getStatistics();
}
