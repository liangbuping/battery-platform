# 电池安全管理平台后端服务

基于 Spring Boot 3.2+ 开发的电池安全管理平台后端服务。

## 技术栈

- **Spring Boot**: 3.2.5
- **Java**: 17
- **MySQL**: 8.0
- **MyBatis Plus**: 3.5.5
- **Maven**: 构建工具

## 项目结构

```
battery-platform-backend/
├── src/main/java/com/battery/platform/
│   ├── BatteryPlatformApplication.java    # 启动类
│   ├── config/                            # 配置类
│   │   ├── MyBatisPlusConfig.java         # MyBatis Plus配置
│   │   ├── WebMvcConfig.java              # Web MVC配置
│   │   └── CorsConfig.java                # 跨域配置
│   ├── controller/                        # 控制器层
│   │   ├── DashboardController.java       # 仪表盘接口
│   │   ├── AlertController.java           # 告警接口
│   │   ├── WorkOrderController.java       # 工单接口
│   │   ├── VehicleController.java         # 车辆接口
│   │   └── StatisticsController.java      # 统计接口
│   ├── service/                           # 服务层
│   │   ├── impl/                          # 服务实现
│   │   ├── DashboardService.java
│   │   ├── AlertService.java
│   │   ├── WorkOrderService.java
│   │   └── VehicleService.java
│   ├── mapper/                            # 数据访问层
│   │   ├── VehicleMapper.java
│   │   ├── AlertMapper.java
│   │   ├── WorkOrderMapper.java
│   │   └── ClosedLoopRecordMapper.java
│   ├── entity/                            # 实体类
│   │   ├── Vehicle.java
│   │   ├── Alert.java
│   │   ├── WorkOrder.java
│   │   └── ClosedLoopRecord.java
│   ├── dto/                               # 数据传输对象
│   │   ├── DashboardKpiDTO.java
│   │   ├── AlertDTO.java
│   │   ├── WorkOrderDTO.java
│   │   └── PageResult.java
│   ├── vo/                                # 视图对象
│   │   ├── KpiVO.java
│   │   ├── AlertVO.java
│   │   ├── WorkOrderVO.java
│   │   └── VehicleVO.java
│   └── utils/                             # 工具类
│       └── Result.java                    # 统一响应结果
├── src/main/resources/
│   ├── application.yml                    # 主配置文件
│   ├── application-dev.yml                # 开发环境配置
│   ├── mapper/                            # Mapper XML文件
│   └── db/init.sql                        # 数据库初始化脚本
└── pom.xml                                # Maven配置
```

## API接口

### 仪表盘接口
- `GET /api/dashboard/kpi` - 获取KPI数据
- `GET /api/dashboard/alerts/realtime` - 实时告警列表
- `GET /api/dashboard/vehicles/risk-top5` - 风险车辆TOP5
- `GET /api/dashboard/statistics/alerts` - 告警统计
- `GET /api/dashboard/statistics/work-orders` - 工单统计

### 告警接口
- `GET /api/alerts` - 告警列表（分页）
- `GET /api/alerts/{id}` - 告警详情
- `GET /api/alerts/statistics` - 告警统计
- `POST /api/alerts/{id}/process` - 处理告警

### 工单接口
- `GET /api/work-orders` - 工单列表（分页）
- `GET /api/work-orders/{id}` - 工单详情
- `POST /api/work-orders` - 创建工单
- `PUT /api/work-orders/{id}` - 更新工单
- `GET /api/work-orders/statistics` - 工单统计

### 车辆接口
- `GET /api/vehicles` - 车辆列表（分页）
- `GET /api/vehicles/{id}` - 车辆详情
- `GET /api/vehicles/{id}/location` - 车辆位置
- `GET /api/vehicles/by-vin/{vin}` - 根据VIN查询车辆
- `GET /api/vehicles/statistics` - 车辆统计

### 统计接口
- `GET /api/statistics/overview` - 综合统计数据

## 快速开始

### 1. 数据库准备

执行数据库初始化脚本：
```bash
mysql -u root -p < src/main/resources/db/init.sql
```

### 2. 修改数据库配置

编辑 `src/main/resources/application-dev.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/battery_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 3. 编译运行

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/battery-platform-backend-1.0.0.jar
```

### 4. 访问服务

启动成功后，访问地址：http://localhost:8080

## 响应格式

统一响应格式：
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1714896000000
}
```

## 数据字典

### 车辆状态
- 0: 离线
- 1: 在线
- 2: 充电中
- 3: 行驶中

### 风险等级
- 0: 正常
- 1: 低风险
- 2: 中风险
- 3: 高风险

### 告警类型
- 1: 温度异常
- 2: 电压异常
- 3: SOC异常
- 4: 绝缘故障
- 5: 其他

### 告警级别
- 1: 一般
- 2: 严重
- 3: 紧急

### 告警状态
- 0: 未处理
- 1: 处理中
- 2: 已处理

### 工单类型
- 1: 故障维修
- 2: 定期保养
- 3: 电池更换
- 4: 其他

### 工单状态
- 0: 待处理
- 1: 处理中
- 2: 已完成
- 3: 已关闭

### 优先级
- 1: 低
- 2: 中
- 3: 高
- 4: 紧急
