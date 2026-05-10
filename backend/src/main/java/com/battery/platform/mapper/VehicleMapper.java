package com.battery.platform.mapper;

import com.battery.platform.entity.Vehicle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 车辆数据访问层
 */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {

    /**
     * 查询风险车辆TOP5
     */
    @Select("SELECT * FROM vehicle WHERE risk_level > 0 AND deleted = 0 ORDER BY risk_level DESC, update_time DESC LIMIT 5")
    List<Vehicle> selectTop5RiskVehicles();

    /**
     * 统计车辆总数
     */
    @Select("SELECT COUNT(*) FROM vehicle WHERE deleted = 0")
    Integer countTotalVehicles();

    /**
     * 统计在线车辆数
     */
    @Select("SELECT COUNT(*) FROM vehicle WHERE status = 1 AND deleted = 0")
    Integer countOnlineVehicles();

    /**
     * 统计风险车辆数
     */
    @Select("SELECT COUNT(*) FROM vehicle WHERE risk_level > 0 AND deleted = 0")
    Integer countRiskVehicles();

    /**
     * 根据VIN查询车辆
     */
    @Select("SELECT * FROM vehicle WHERE vin = #{vin} AND deleted = 0 LIMIT 1")
    Vehicle selectByVin(@Param("vin") String vin);
}
