package com.battery.platform.controller;

import com.battery.platform.dto.PageResult;
import com.battery.platform.dto.WorkOrderDTO;
import com.battery.platform.entity.WorkOrder;
import com.battery.platform.service.WorkOrderService;
import com.battery.platform.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 工单控制器
 */
@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    /**
     * 分页查询工单列表
     */
    @GetMapping
    public Result<PageResult<WorkOrderDTO>> getWorkOrderPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer priority) {
        PageResult<WorkOrderDTO> pageResult = workOrderService.getWorkOrderPage(current, size, orderType, status, priority);
        return Result.success(pageResult);
    }

    /**
     * 获取工单详情
     */
    @GetMapping("/{id}")
    public Result<WorkOrderDTO> getWorkOrderDetail(@PathVariable Long id) {
        WorkOrderDTO workOrder = workOrderService.getWorkOrderDetail(id);
        if (workOrder == null) {
            return Result.error("工单不存在");
        }
        return Result.success(workOrder);
    }

    /**
     * 创建工单
     */
    @PostMapping
    public Result<Boolean> createWorkOrder(@RequestBody WorkOrder workOrder) {
        boolean result = workOrderService.createWorkOrder(workOrder);
        if (result) {
            return Result.success(true);
        }
        return Result.error("创建失败");
    }

    /**
     * 更新工单
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateWorkOrder(@PathVariable Long id, @RequestBody WorkOrder workOrder) {
        boolean result = workOrderService.updateWorkOrder(id, workOrder);
        if (result) {
            return Result.success(true);
        }
        return Result.error("更新失败");
    }

    /**
     * 获取工单统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = workOrderService.getStatistics();
        return Result.success(statistics);
    }
}
