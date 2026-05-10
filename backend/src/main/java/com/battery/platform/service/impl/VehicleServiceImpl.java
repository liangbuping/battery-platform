package com.battery.platform.service.impl;

import com.battery.platform.dto.PageResult;
import com.battery.platform.entity.Vehicle;
import com.battery.platform.mapper.VehicleMapper;
import com.battery.platform.service.VehicleService;
import com.battery.platform.vo.VehicleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 车辆服务实现类
 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    private final VehicleMapper vehicleMapper;

    @Override
    public PageResult<VehicleVO> getVehiclePage(Integer current, Integer size, String vin, String plateNumber, Integer status) {
        Page<Vehicle> page = new Page<>(current, size);
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vehicle::getDeleted, 0);

        if (StringUtils.hasText(vin)) {
            wrapper.like(Vehicle::getVin, vin);
        }
        if (StringUtils.hasText(plateNumber)) {
            wrapper.like(Vehicle::getPlateNumber, plateNumber);
        }
        if (status != null) {
            wrapper.eq(Vehicle::getStatus, status);
        }

        wrapper.orderByDesc(Vehicle::getUpdateTime);
        Page<Vehicle> vehiclePage = this.page(page, wrapper);

        List<VehicleVO> records = vehiclePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.build(vehiclePage.getCurrent(), vehiclePage.getSize(), vehiclePage.getTotal(), records);
    }

    @Override
    public VehicleVO getVehicleDetail(Long id) {
        Vehicle vehicle = this.getById(id);
        if (vehicle == null || vehicle.getDeleted() == 1) {
            return null;
        }
        return convertToVO(vehicle);
    }

    @Override
    public Map<String, Object> getVehicleLocation(Long id) {
        Vehicle vehicle = this.getById(id);
        if (vehicle == null || vehicle.getDeleted() == 1) {
            return null;
        }

        Map<String, Object> location = new HashMap<>();
        location.put("vehicleId", id);
        location.put("vin", vehicle.getVin());
        location.put("plateNumber", vehicle.getPlateNumber());
        location.put("longitude", vehicle.getLongitude());
        location.put("latitude", vehicle.getLatitude());
        location.put("lastReportTime", vehicle.getLastReportTime());

        return location;
    }

    @Override
    public VehicleVO getVehicleByVin(String vin) {
        Vehicle vehicle = vehicleMapper.selectByVin(vin);
        if (vehicle == null || vehicle.getDeleted() == 1) {
            return null;
        }
        return convertToVO(vehicle);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 车辆总数
        Integer totalVehicles = vehicleMapper.countTotalVehicles();
        statistics.put("totalVehicles", totalVehicles);

        // 在线车辆数
        Integer onlineVehicles = vehicleMapper.countOnlineVehicles();
        statistics.put("onlineVehicles", onlineVehicles);

        // 风险车辆数
        Integer riskVehicles = vehicleMapper.countRiskVehicles();
        statistics.put("riskVehicles", riskVehicles);

        // 在线率
        if (totalVehicles != null && totalVehicles > 0) {
            double onlineRate = (double) onlineVehicles / totalVehicles * 100;
            statistics.put("onlineRate", String.format("%.2f", onlineRate));
        } else {
            statistics.put("onlineRate", "0.00");
        }

        return statistics;
    }

    private VehicleVO convertToVO(Vehicle vehicle) {
        VehicleVO vo = new VehicleVO();
        BeanUtils.copyProperties(vehicle, vo);
        vo.setStatusName(getStatusName(vehicle.getStatus()));
        vo.setRiskLevelName(getRiskLevelName(vehicle.getRiskLevel()));
        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "离线";
            case 1 -> "在线";
            case 2 -> "充电中";
            case 3 -> "行驶中";
            default -> "未知";
        };
    }

    private String getRiskLevelName(Integer level) {
        if (level == null) return "未知";
        return switch (level) {
            case 0 -> "正常";
            case 1 -> "低风险";
            case 2 -> "中风险";
            case 3 -> "高风险";
            default -> "未知";
        };
    }
}
