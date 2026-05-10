# 超期服役新能源公交电池安全防控系统

Battery Safety Management Platform for Overdue Service Electric Buses

## 项目结构

```
├── backend/          # Java Spring Boot 后端
│   ├── src/
│   │   ├── main/java/com/battery/platform/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── mapper/          # MyBatis Mapper
│   │   │   ├── service/         # 服务层
│   │   │   ├── utils/           # 工具类
│   │   │   └── vo/              # 视图对象
│   │   └── resources/
│   │       ├── db/              # 数据库初始化脚本
│   │       ├── mapper/          # MyBatis XML
│   │       └── application.yml  # 配置文件
│   └── pom.xml
│
└── frontend/         # Vue3 + TypeScript 前端
    ├── src/
    │   ├── api/                 # API 接口
    │   ├── components/          # 组件
    │   ├── router/              # 路由
    │   ├── stores/              # Pinia 状态管理
    │   ├── types/               # TypeScript 类型
    │   ├── utils/               # 工具函数
    │   └── views/               # 页面视图
    │       ├── Dashboard/       # 实时监控
    │       ├── AlertCenter/     # 预警中心
    │       ├── Maintenance/     # 维保管理
    │       └── Mobile/          # 移动端页面
    └── package.json
```

## 功能模块

### 1. 实时监控 (Dashboard)
- 车辆总数、在线车辆、预警车辆、今日预警 KPI 卡片
- 车辆实时分布地图
- 预警类型统计、预警等级分布
- TOP5 风险车辆

### 2. 预警中心 (Alert Center)
- 预警列表（支持筛选、分页）
- 预警等级分布图
- 7天预警趋势图
- 预警类型分布图

### 3. 维保管理 (Maintenance)
- 工单列表
- 6步闭环流程可视化
- 工单详情抽屉面板
- 处理进度时间线

## 技术栈

### 后端
- Java 17
- Spring Boot 3.x
- MyBatis Plus
- SQLite / MySQL

### 前端
- Vue 3
- TypeScript
- Vite
- Pinia
- ECharts

## 快速开始

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

## 访问地址

- 前端：http://localhost:5173
- 后端 API：http://localhost:8080
