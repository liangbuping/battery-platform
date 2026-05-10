package com.battery.platform.service;

import com.battery.platform.dto.PageResult;
import com.battery.platform.entity.Vehicle;
import com.battery.platform.vo.VehicleVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 车辆服务接口
 */
public interface VehicleService extends IService<Vehicle> {

    /**
     * 分页查询车辆列表
     */
    PageResult<VehicleVO> getVehiclePage(Integer current, Integer size, String vin, String plateNumber, Integer status);

    /**
     * 获取车辆详情
     */
    VehicleVO getVehicleDetail(Long id);

    /**
     * 获取车辆位置
     */
    Map<String, Object> getVehicleLocation(Long id);

    /**
     * 根据VIN查询车辆
     */
    VehicleVO getVehicleByVin(String vin);

    /**
     * 获取车辆统计
     */
    Map<String, Object> getStatistics();
}
