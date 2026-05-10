package com.battery.platform.service.impl;

import com.battery.platform.dto.AlertDTO;
import com.battery.platform.dto.PageResult;
import com.battery.platform.entity.Alert;
import com.battery.platform.mapper.AlertMapper;
import com.battery.platform.service.AlertService;
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
 * 告警服务实现类
 */
@Service
@RequiredArgsConstructor
public class AlertServiceImpl extends ServiceImpl<AlertMapper, Alert> implements AlertService {

    private final AlertMapper alertMapper;

    @Override
    public PageResult<AlertDTO> getAlertPage(Integer current, Integer size, Integer alertType, Integer alertLevel, Integer status) {
        Page<Alert> page = new Page<>(current, size);
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Alert::getDeleted, 0);

        if (alertType != null) {
            wrapper.eq(Alert::getAlertType, alertType);
        }
        if (alertLevel != null) {
            wrapper.eq(Alert::getAlertLevel, alertLevel);
        }
        if (status != null) {
            wrapper.eq(Alert::getStatus, status);
        }

        wrapper.orderByDesc(Alert::getAlertTime);
        Page<Alert> alertPage = this.page(page, wrapper);

        List<AlertDTO> records = alertPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.build(alertPage.getCurrent(), alertPage.getSize(), alertPage.getTotal(), records);
    }

    @Override
    public AlertDTO getAlertDetail(Long id) {
        Alert alert = this.getById(id);
        if (alert == null || alert.getDeleted() == 1) {
            return null;
        }
        return convertToDTO(alert);
    }

    @Override
    public boolean processAlert(Long id, String handler, String remark) {
        Alert alert = this.getById(id);
        if (alert == null || alert.getDeleted() == 1) {
            return false;
        }

        alert.setStatus(2);
        alert.setHandler(handler);
        alert.setHandleRemark(remark);
        alert.setHandleTime(LocalDateTime.now());
        alert.setUpdateTime(LocalDateTime.now());

        return this.updateById(alert);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 按类型统计
        List<Map<String, Object>> typeStats = alertMapper.countByType();
        statistics.put("byType", typeStats);

        // 按级别统计
        List<Map<String, Object>> levelStats = alertMapper.countByLevel();
        statistics.put("byLevel", levelStats);

        // 今日告警数
        statistics.put("todayCount", alertMapper.countTodayAlerts());

        // 未处理告警数
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Alert::getDeleted, 0).eq(Alert::getStatus, 0);
        long pendingCount = this.count(wrapper);
        statistics.put("pendingCount", pendingCount);

        return statistics;
    }

    @Override
    public boolean createAlert(Alert alert) {
        alert.setCreateTime(LocalDateTime.now());
        alert.setUpdateTime(LocalDateTime.now());
        alert.setDeleted(0);
        alert.setStatus(0);
        return this.save(alert);
    }

    private AlertDTO convertToDTO(Alert alert) {
        AlertDTO dto = new AlertDTO();
        BeanUtils.copyProperties(alert, dto);
        dto.setAlertTypeName(getAlertTypeName(alert.getAlertType()));
        dto.setAlertLevelName(getAlertLevelName(alert.getAlertLevel()));
        dto.setStatusName(getStatusName(alert.getStatus()));
        return dto;
    }

    private String getAlertTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "温度异常";
            case 2 -> "电压异常";
            case 3 -> "SOC异常";
            case 4 -> "绝缘故障";
            case 5 -> "其他";
            default -> "未知";
        };
    }

    private String getAlertLevelName(Integer level) {
        if (level == null) return "未知";
        return switch (level) {
            case 1 -> "一般";
            case 2 -> "严重";
            case 3 -> "紧急";
            default -> "未知";
        };
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "未处理";
            case 1 -> "处理中";
            case 2 -> "已处理";
            default -> "未知";
        };
    }
}
