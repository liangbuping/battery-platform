<template>
  <div class="alert-center-page">
    <!-- Page Title Bar -->
    <div class="page-title-bar">
      <div class="title-section">
        <div class="page-title">
          <span class="title-icon">&#128680;</span>
          预警中心
          <span class="page-subtitle">实时告警监控与处理</span>
        </div>
      </div>
      <div class="filter-bar">
        <button
          v-for="item in timeFilters"
          :key="item.value"
          :class="['filter-btn', { active: activeTimeFilter === item.value }]"
          @click="handleTimeFilter(item.value)"
        >{{ item.label }}</button>
      </div>
    </div>

    <!-- Statistics Row -->
    <div class="stats-row">
      <div class="stat-card" v-for="(card, idx) in statCards" :key="idx">
        <div class="stat-icon" :style="{ background: card.iconBg, color: card.iconColor }">
          {{ card.icon }}
        </div>
        <div class="stat-info">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value" :style="{ color: card.valueColor }">{{ card.value }}<span v-if="card.unit" class="stat-unit">{{ card.unit }}</span></div>
          <div class="stat-trend" :class="card.trendDir">{{ card.trend }}</div>
        </div>
      </div>
    </div>

    <!-- Middle Section: 3 columns -->
    <div class="middle-section">
      <!-- Alert Level Distribution -->
      <div class="panel">
        <div class="panel-title"><span class="dot"></span>预警等级分布</div>
        <div class="level-rings">
          <div class="level-item" v-for="item in levelItems" :key="item.level">
            <div class="ring-container">
              <svg class="ring-svg" viewBox="0 0 100 100">
                <circle class="ring-bg" cx="50" cy="50" r="42" />
                <circle
                  class="ring-fill"
                  :class="item.ringClass"
                  cx="50" cy="50" r="42"
                  :style="{ strokeDasharray: item.dashArray }"
                />
              </svg>
              <div class="ring-center">
                <div class="ring-number" :class="item.ringClass">{{ item.count }}</div>
                <div class="ring-label">活跃</div>
              </div>
            </div>
            <div class="level-name" :style="{ color: item.color }">{{ item.name }}</div>
            <div class="level-desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>

      <!-- Alert Trend Chart -->
      <div class="panel">
        <div class="panel-title"><span class="dot"></span>近7日告警趋势</div>
        <div class="chart-wrapper">
          <BarChart
            :stack-data="trendStackData"
            :stacked="true"
            :show-legend="true"
            style="height: 220px;"
          />
        </div>
      </div>

      <!-- Alert Type Distribution -->
      <div class="panel">
        <div class="panel-title"><span class="dot"></span>告警类型分布</div>
        <div class="donut-wrapper">
          <PieChart
            :data="typeDistributionData"
            :radius="['55%', '80%']"
            :show-legend="true"
            :center-text="typeTotal"
            center-sub-text="近7日"
            :colors="['#ff4757', '#ffa502', '#f1c40f', '#00d4ff', '#8899aa']"
            style="height: 260px;"
          />
        </div>
      </div>
    </div>

    <!-- Alert Table -->
    <div class="table-panel">
      <div class="table-header">
        <div class="table-title"><span class="dot"></span>告警列表</div>
        <div class="table-actions">
          <el-button size="small" class="dark-btn-outline" @click="handleExport">
            <el-icon><Download /></el-icon> 导出
          </el-button>
          <el-button size="small" type="primary" class="dark-btn-primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 筛选
          </el-button>
        </div>
      </div>

      <el-table
        :data="alerts"
        v-loading="loading"
        class="dark-table"
        :header-cell-style="headerCellStyle"
        :row-style="rowStyle"
        :row-class-name="rowClassName"
        style="width: 100%"
      >
        <el-table-column prop="createTime" label="时间" width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="vehicleId" label="车辆编号" width="150" show-overflow-tooltip />
        <el-table-column prop="plateNumber" label="车牌号" width="110" />
        <el-table-column prop="level" label="预警等级" width="100">
          <template #default="{ row }">
            <span :class="['badge-level', `badge-level-${getLevelClass(row.level)}`]">{{ getLevelText(row.level) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="预警类型" width="120">
          <template #default="{ row }">
            <span :class="['badge', `badge-${getTypeBadge(row.typeName)}`]">{{ row.typeName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="告警描述" min-width="240" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="['badge', `badge-${getStatusBadge(row.status)}`]">{{ getStatusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <button class="action-btn" @click="handleView(row)">查看</button>
            <button class="action-btn handle" @click="handleProcess(row)">处理</button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <div class="table-info">共 <strong>{{ total }}</strong> 条记录，当前第 <strong>{{ currentPage }}</strong> / <strong>{{ totalPages }}</strong> 页</div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="prev, pager, next"
          small
          class="dark-pagination"
          @change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Download, Search } from '@element-plus/icons-vue'
import { useAlertStore } from '@/stores/alert'
import { storeToRefs } from 'pinia'
import { formatDateTime, getAlertLevelText } from '@/utils/format'
import BarChart from '@/components/Charts/BarChart.vue'
import PieChart from '@/components/Charts/PieChart.vue'

const alertStore = useAlertStore()
const { overview, levelDistribution, trendData, typeDistribution, alerts, total, loading } = storeToRefs(alertStore)

const activeTimeFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

const timeFilters = [
  { label: '全部', value: 'all' },
  { label: '今日', value: 'today' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' }
]

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const statCards = computed(() => [
  {
    icon: '&#128308;',
    label: '今日告警',
    value: overview.value.totalAlerts || 7,
    unit: '',
    iconBg: 'rgba(255,71,87,0.12)',
    iconColor: '#ff4757',
    valueColor: '#ff4757',
    trend: '&#9650; 较昨日 +2',
    trendDir: 'up'
  },
  {
    icon: '&#9888;&#65039;',
    label: '待处理',
    value: overview.value.pendingAlerts || 4,
    unit: '',
    iconBg: 'rgba(255,165,2,0.12)',
    iconColor: '#ffa502',
    valueColor: '#ffa502',
    trend: '&#9650; 需尽快处理',
    trendDir: 'up'
  },
  {
    icon: '&#128276;',
    label: '处理中',
    value: overview.value.processingAlerts || 2,
    unit: '',
    iconBg: 'rgba(0,212,255,0.12)',
    iconColor: '#00d4ff',
    valueColor: '#00d4ff',
    trend: '&#9679; 进行中',
    trendDir: ''
  },
  {
    icon: '&#9989;',
    label: '已解决',
    value: overview.value.resolvedAlerts || 38,
    unit: '',
    iconBg: 'rgba(46,213,115,0.12)',
    iconColor: '#2ed573',
    valueColor: '#2ed573',
    trend: '&#9660; 解决率 86.4%',
    trendDir: 'down'
  },
  {
    icon: '&#9201;',
    label: '平均响应',
    value: '8.5',
    unit: '分钟',
    iconBg: 'rgba(136,153,170,0.12)',
    iconColor: '#8899aa',
    valueColor: '#8899aa',
    trend: '&#9660; 较上周 -1.2min',
    trendDir: 'down'
  }
])

const levelItems = computed(() => {
  const levels = [
    { level: 1, name: '一级预警', desc: '立即停车检查', color: '#ff4757', ringClass: 'level1' },
    { level: 2, name: '二级预警', desc: '限制运营范围', color: '#ffa502', ringClass: 'level2' },
    { level: 3, name: '三级预警', desc: '加强监控频率', color: '#00d4ff', ringClass: 'level3' }
  ]
  const distData = levelDistribution.value.length > 0 ? levelDistribution.value : [
    { name: '一级预警', value: 2 },
    { name: '二级预警', value: 3 },
    { name: '三级预警', value: 2 }
  ]
  return levels.map((l, i) => {
    const count = distData[i]?.value || 0
    const pct = Math.min(count / 10, 1)
    const circumference = 2 * Math.PI * 42 // ~264
    return {
      ...l,
      count,
      dashArray: `${pct * circumference} ${circumference}`
    }
  })
})

const trendStackData = computed(() => {
  const td = trendData.value
  if (td && td.length > 0) {
    return [
      { name: '一级预警', xAxis: td.map(d => d.date), data: td.map(d => d.critical) },
      { name: '二级预警', xAxis: td.map(d => d.date), data: td.map(d => d.warning) },
      { name: '三级预警', xAxis: td.map(d => d.date), data: td.map(d => d.info) }
    ]
  }
  // Mock data matching design
  return [
    { name: '一级预警', xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], data: [1, 2, 0, 2, 1, 0, 1] },
    { name: '二级预警', xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], data: [1, 2, 1, 3, 2, 0, 1] },
    { name: '三级预警', xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], data: [1, 1, 1, 2, 1, 1, 1] }
  ]
})

const typeDistributionData = computed(() => {
  const td = typeDistribution.value
  if (td && td.length > 0) {
    return td.map(d => ({ name: d.name, value: d.value }))
  }
  return [
    { name: '热失控风险', value: 20 },
    { name: '电压异常', value: 30 },
    { name: '温度异常', value: 25 },
    { name: '绝缘故障', value: 15 },
    { name: '容量衰减', value: 10 }
  ]
})

const typeTotal = computed(() => {
  const td = typeDistributionData.value
  return String(td.reduce((sum, d) => sum + d.value, 0))
})

const getLevelClass = (level: string) => {
  const map: Record<string, string> = { critical: '1', warning: '2', info: '3', '1': '1', '2': '2', '3': '3' }
  return map[level] || '3'
}

const getLevelText = (level: string) => {
  if (['critical', '1'].includes(level)) return '一级'
  if (['warning', '2'].includes(level)) return '二级'
  if (['info', '3'].includes(level)) return '三级'
  return getAlertLevelText(level)
}

const getTypeBadge = (typeName: string) => {
  if (typeName?.includes('热失控')) return 'danger'
  if (typeName?.includes('电压')) return 'warning'
  if (typeName?.includes('温度')) return 'warning'
  if (typeName?.includes('绝缘')) return 'info'
  return 'info'
}

const getStatusBadge = (status: string) => {
  const map: Record<string, string> = { pending: 'danger', processing: 'info', resolved: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待处理', processing: '处理中', resolved: '已解决' }
  return map[status] || status
}

const headerCellStyle = () => ({
  background: 'rgba(255,255,255,0.04)',
  color: '#8899aa',
  borderBottom: '1px solid rgba(255,255,255,0.08)',
  fontSize: '12px',
  fontWeight: '600',
  textTransform: 'uppercase' as const,
  letterSpacing: '0.5px'
})

const rowStyle = () => ({
  borderBottom: '1px solid rgba(255,255,255,0.05)'
})

const rowClassName = () => 'dark-row'

const handleTimeFilter = (value: string) => {
  activeTimeFilter.value = value
  alertStore.fetchAlertList({ page: 1, pageSize: pageSize.value, timeRange: value })
}

const handleSearch = () => {
  alertStore.fetchAlertList({ page: 1, pageSize: pageSize.value })
}

const handleExport = () => {
  // Export logic placeholder
}

const handleView = (row: any) => {
  alertStore.fetchAlertDetail(row.id)
}

const handleProcess = (row: any) => {
  alertStore.handleProcessAlert(row.id, 'process')
}

const handlePageChange = () => {
  alertStore.fetchAlertList({ page: currentPage.value, pageSize: pageSize.value })
}

onMounted(() => {
  alertStore.fetchCenterData()
  alertStore.fetchAlertList()
})
</script>

<style scoped>
.alert-center-page {
  padding: 20px 24px;
  background: #0d1b2a;
  min-height: calc(100vh - 60px);
  color: #e0e0e0;
}

/* Page Title Bar */
.page-title-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  gap: 10px;
}
.title-icon {
  font-size: 24px;
}
.page-subtitle {
  font-size: 13px;
  color: #8899aa;
  margin-left: 4px;
  font-weight: 400;
}
.filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
.filter-btn {
  padding: 7px 18px;
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 6px;
  background: transparent;
  font-size: 13px;
  color: #8899aa;
  cursor: pointer;
  transition: all 0.2s;
}
.filter-btn:hover,
.filter-btn.active {
  border-color: #00d4ff;
  color: #00d4ff;
  background: rgba(0,212,255,0.08);
}
.filter-btn.active {
  font-weight: 600;
}

/* Statistics Row */
.stats-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  background: #1b2838;
  border-radius: 12px;
  padding: 20px 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s;
  position: relative;
  border: 1px solid rgba(255,255,255,0.06);
}
.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.3);
}
.stat-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0;
  width: 4px; height: 100%;
  border-radius: 4px 0 0 4px;
}
.stat-card:nth-child(1)::before { background: #ff4757; }
.stat-card:nth-child(2)::before { background: #ffa502; }
.stat-card:nth-child(3)::before { background: #00d4ff; }
.stat-card:nth-child(4)::before { background: #2ed573; }
.stat-card:nth-child(5)::before { background: #8899aa; }
.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.stat-info { flex: 1; min-width: 0; }
.stat-label {
  font-size: 13px;
  color: #8899aa;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.1;
}
.stat-unit {
  font-size: 13px;
  font-weight: 400;
  color: #8899aa;
  margin-left: 2px;
}
.stat-trend {
  font-size: 12px;
  margin-top: 4px;
  color: #8899aa;
}
.stat-trend.up { color: #ff4757; }
.stat-trend.down { color: #2ed573; }

/* Middle Section */
.middle-section {
  display: grid;
  grid-template-columns: 5fr 6fr 5fr;
  gap: 16px;
  margin-bottom: 20px;
  align-items: start;
}
.panel {
  background: #1b2838;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255,255,255,0.06);
}
.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #e0e0e0;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.panel-title .dot {
  width: 4px; height: 16px;
  border-radius: 2px;
  background: #00d4ff;
}

/* Level Rings */
.level-rings {
  display: flex;
  justify-content: space-evenly;
  align-items: flex-start;
  padding: 10px 0;
  gap: 12px;
}
.level-item {
  text-align: center;
  flex: 1;
}
.ring-container {
  position: relative;
  width: 100px; height: 100px;
  margin: 0 auto 12px;
}
.ring-svg {
  width: 100px; height: 100px;
  transform: rotate(-90deg);
}
.ring-bg {
  fill: none;
  stroke: rgba(255,255,255,0.06);
  stroke-width: 8;
}
.ring-fill {
  fill: none;
  stroke-width: 8;
  stroke-linecap: round;
  transition: stroke-dasharray 1s ease;
}
.ring-fill.level1 { stroke: #ff4757; }
.ring-fill.level2 { stroke: #ffa502; }
.ring-fill.level3 { stroke: #00d4ff; }
.ring-center {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}
.ring-number {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}
.ring-number.level1 { color: #ff4757; }
.ring-number.level2 { color: #ffa502; }
.ring-number.level3 { color: #00d4ff; }
.ring-label {
  font-size: 11px;
  color: #8899aa;
  margin-top: 2px;
}
.level-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}
.level-desc {
  font-size: 11px;
  color: #8899aa;
  padding: 0 4px;
}

/* Chart wrapper */
.chart-wrapper {
  height: 220px;
}

/* Donut wrapper */
.donut-wrapper {
  padding: 10px 0;
}

/* Table Panel */
.table-panel {
  background: #1b2838;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255,255,255,0.06);
}
.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.table-title {
  font-size: 15px;
  font-weight: 700;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.table-title .dot {
  width: 4px; height: 16px;
  border-radius: 2px;
  background: #00d4ff;
}
.table-actions {
  display: flex;
  gap: 10px;
}

/* Dark theme buttons */
.dark-btn-primary {
  background: #00d4ff !important;
  border-color: #00d4ff !important;
  color: #0d1b2a !important;
  font-weight: 500;
}
.dark-btn-primary:hover {
  background: #33ddff !important;
  border-color: #33ddff !important;
}
.dark-btn-outline {
  background: transparent !important;
  border: 1px solid rgba(255,255,255,0.12) !important;
  color: #8899aa !important;
}
.dark-btn-outline:hover {
  border-color: #00d4ff !important;
  color: #00d4ff !important;
}

/* Dark Table */
.dark-table :deep(.el-table__header-wrapper th) {
  background: rgba(255,255,255,0.04) !important;
  color: #8899aa !important;
  border-bottom: 1px solid rgba(255,255,255,0.08) !important;
}
.dark-table :deep(.el-table__body-wrapper) {
  background: transparent !important;
}
.dark-table :deep(.el-table__body tr) {
  background: transparent !important;
  color: #e0e0e0 !important;
  border-bottom: 1px solid rgba(255,255,255,0.05) !important;
}
.dark-table :deep(.el-table__body tr:hover > td) {
  background: rgba(0,212,255,0.04) !important;
}
.dark-table :deep(.el-table__empty-block) {
  background: transparent !important;
}
.dark-table :deep(.el-table__inner-wrapper::before) {
  background: rgba(255,255,255,0.08) !important;
}

/* Badges */
.badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}
.badge-danger { background: rgba(255,71,87,0.12); color: #ff4757; }
.badge-warning { background: rgba(255,165,2,0.12); color: #ffa502; }
.badge-info { background: rgba(0,212,255,0.12); color: #00d4ff; }
.badge-success { background: rgba(46,213,115,0.12); color: #2ed573; }

.badge-level {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
}
.badge-level-1 { background: #ff4757; }
.badge-level-2 { background: #ffa502; }
.badge-level-3 { background: #00d4ff; }

/* Action buttons */
.action-btn {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid rgba(255,255,255,0.12);
  background: transparent;
  color: #00d4ff;
  transition: all 0.2s;
  margin-right: 4px;
}
.action-btn:hover {
  background: #00d4ff;
  color: #0d1b2a;
  border-color: #00d4ff;
}
.action-btn.handle {
  color: #ffa502;
  border-color: rgba(255,165,2,0.3);
}
.action-btn.handle:hover {
  background: #ffa502;
  color: #0d1b2a;
  border-color: #ffa502;
}

/* Table Footer */
.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid rgba(255,255,255,0.06);
}
.table-info {
  font-size: 13px;
  color: #8899aa;
}
.table-info strong {
  color: #e0e0e0;
}

/* Dark Pagination */
.dark-pagination :deep(.el-pager li) {
  background: transparent !important;
  color: #8899aa !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  border-radius: 6px !important;
  margin: 0 2px !important;
}
.dark-pagination :deep(.el-pager li.is-active) {
  background: #00d4ff !important;
  color: #0d1b2a !important;
  border-color: #00d4ff !important;
  font-weight: 600;
}
.dark-pagination :deep(.btn-prev),
.dark-pagination :deep(.btn-next) {
  background: transparent !important;
  color: #8899aa !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  border-radius: 6px !important;
}
.dark-pagination :deep(.btn-prev:hover),
.dark-pagination :deep(.btn-next:hover) {
  color: #00d4ff !important;
  border-color: #00d4ff !important;
}

/* Responsive */
@media (max-width: 1400px) {
  .middle-section {
    grid-template-columns: 1fr 1fr;
  }
}
@media (max-width: 900px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .middle-section {
    grid-template-columns: 1fr;
  }
  .page-title-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
