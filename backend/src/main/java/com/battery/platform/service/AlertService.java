package com.battery.platform.service;

import com.battery.platform.dto.AlertDTO;
import com.battery.platform.dto.PageResult;
import com.battery.platform.entity.Alert;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 告警服务接口
 */
public interface AlertService extends IService<Alert> {

    /**
     * 分页查询告警列表
     */
    PageResult<AlertDTO> getAlertPage(Integer current, Integer size, Integer alertType, Integer alertLevel, Integer status);

    /**
     * 获取告警详情
     */
    AlertDTO getAlertDetail(Long id);

    /**
     * 处理告警
     */
    boolean processAlert(Long id, String handler, String remark);

    /**
     * 获取告警统计
     */
    Map<String, Object> getStatistics();

    /**
     * 创建告警
     */
    boolean createAlert(Alert alert);
}
