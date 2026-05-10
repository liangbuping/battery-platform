package com.battery.platform.service.impl;

import com.battery.platform.dto.PageResult;
import com.battery.platform.dto.WorkOrderDTO;
import com.battery.platform.entity.WorkOrder;
import com.battery.platform.mapper.WorkOrderMapper;
import com.battery.platform.service.WorkOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工单服务实现类
 */
@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    private final WorkOrderMapper workOrderMapper;

    @Override
    public PageResult<WorkOrderDTO> getWorkOrderPage(Integer current, Integer size, Integer orderType, Integer status, Integer priority) {
        Page<WorkOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrder::getDeleted, 0);

        if (orderType != null) {
            wrapper.eq(WorkOrder::getOrderType, orderType);
        }
        if (status != null) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        if (priority != null) {
            wrapper.eq(WorkOrder::getPriority, priority);
        }

        wrapper.orderByDesc(WorkOrder::getCreateTime);
        Page<WorkOrder> workOrderPage = this.page(page, wrapper);

        List<WorkOrderDTO> records = workOrderPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.build(workOrderPage.getCurrent(), workOrderPage.getSize(), workOrderPage.getTotal(), records);
    }

    @Override
    public WorkOrderDTO getWorkOrderDetail(Long id) {
        WorkOrder workOrder = this.getById(id);
        if (workOrder == null || workOrder.getDeleted() == 1) {
            return null;
        }
        return convertToDTO(workOrder);
    }

    @Override
    public boolean createWorkOrder(WorkOrder workOrder) {
        workOrder.setCreateTime(LocalDateTime.now());
        workOrder.setUpdateTime(LocalDateTime.now());
        workOrder.setDeleted(0);
        workOrder.setStatus(0);
        return this.save(workOrder);
    }

    @Override
    public boolean updateWorkOrder(Long id, WorkOrder workOrder) {
        WorkOrder existing = this.getById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }

        workOrder.setId(id);
        workOrder.setUpdateTime(LocalDateTime.now());

        // 如果状态变为已完成，设置实际完成时间
        if (workOrder.getStatus() != null && workOrder.getStatus() == 2) {
            workOrder.setActualFinishTime(LocalDateTime.now());
        }

        return this.updateById(workOrder);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 按状态统计
        List<Map<String, Object>> statusStats = workOrderMapper.countByStatus();
        statistics.put("byStatus", statusStats);

        // 按类型统计
        List<Map<String, Object>> typeStats = workOrderMapper.countByType();
        statistics.put("byType", typeStats);

        // 未处理工单数
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrder::getDeleted, 0).in(WorkOrder::getStatus, 0, 1);
        long pendingCount = this.count(wrapper);
        statistics.put("pendingCount", pendingCount);

        return statistics;
    }

    private WorkOrderDTO convertToDTO(WorkOrder workOrder) {
        WorkOrderDTO dto = new WorkOrderDTO();
        BeanUtils.copyProperties(workOrder, dto);
        dto.setOrderTypeName(getOrderTypeName(workOrder.getOrderType()));
        dto.setStatusName(getStatusName(workOrder.getStatus()));
        dto.setPriorityName(getPriorityName(workOrder.getPriority()));
        return dto;
    }

    private String getOrderTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "故障维修";
            case 2 -> "定期保养";
            case 3 -> "电池更换";
            case 4 -> "其他";
            default -> "未知";
        };
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待处理";
            case 1 -> "处理中";
            case 2 -> "已完成";
            case 3 -> "已关闭";
            default -> "未知";
        };
    }

    private String getPriorityName(Integer priority) {
        if (priority == null) return "未知";
        return switch (priority) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            case 4 -> "紧急";
            default -> "未知";
        };
    }
}
