package com.battery.platform.controller;

import com.battery.platform.dto.PageResult;
import com.battery.platform.service.VehicleService;
import com.battery.platform.utils.Result;
import com.battery.platform.vo.VehicleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 车辆控制器
 */
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * 分页查询车辆列表
     */
    @GetMapping
    public Result<PageResult<VehicleVO>> getVehiclePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String vin,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer status) {
        PageResult<VehicleVO> pageResult = vehicleService.getVehiclePage(current, size, vin, plateNumber, status);
        return Result.success(pageResult);
    }

    /**
     * 获取车辆详情
     */
    @GetMapping("/{id}")
    public Result<VehicleVO> getVehicleDetail(@PathVariable Long id) {
        VehicleVO vehicle = vehicleService.getVehicleDetail(id);
        if (vehicle == null) {
            return Result.error("车辆不存在");
        }
        return Result.success(vehicle);
    }

    /**
     * 获取车辆位置
     */
    @GetMapping("/{id}/location")
    public Result<Map<String, Object>> getVehicleLocation(@PathVariable Long id) {
        Map<String, Object> location = vehicleService.getVehicleLocation(id);
        if (location == null) {
            return Result.error("车辆不存在");
        }
        return Result.success(location);
    }

    /**
     * 根据VIN查询车辆
     */
    @GetMapping("/by-vin/{vin}")
    public Result<VehicleVO> getVehicleByVin(@PathVariable String vin) {
        VehicleVO vehicle = vehicleService.getVehicleByVin(vin);
        if (vehicle == null) {
            return Result.error("车辆不存在");
        }
        return Result.success(vehicle);
    }

    /**
     * 获取车辆统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = vehicleService.getStatistics();
        return Result.success(statistics);
    }
}
