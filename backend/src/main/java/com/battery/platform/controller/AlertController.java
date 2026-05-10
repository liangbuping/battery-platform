package com.battery.platform.controller;

import com.battery.platform.dto.AlertDTO;
import com.battery.platform.dto.PageResult;
import com.battery.platform.service.AlertService;
import com.battery.platform.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 告警控制器
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /**
     * 分页查询告警列表
     */
    @GetMapping
    public Result<PageResult<AlertDTO>> getAlertPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer alertType,
            @RequestParam(required = false) Integer alertLevel,
            @RequestParam(required = false) Integer status) {
        PageResult<AlertDTO> pageResult = alertService.getAlertPage(current, size, alertType, alertLevel, status);
        return Result.success(pageResult);
    }

    /**
     * 获取告警详情
     */
    @GetMapping("/{id}")
    public Result<AlertDTO> getAlertDetail(@PathVariable Long id) {
        AlertDTO alert = alertService.getAlertDetail(id);
        if (alert == null) {
            return Result.error("告警不存在");
        }
        return Result.success(alert);
    }

    /**
     * 获取告警统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = alertService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 处理告警
     */
    @PostMapping("/{id}/process")
    public Result<Boolean> processAlert(
            @PathVariable Long id,
            @RequestParam String handler,
            @RequestParam(required = false) String remark) {
        boolean result = alertService.processAlert(id, handler, remark);
        if (result) {
            return Result.success(true);
        }
        return Result.error("处理失败");
    }
}
