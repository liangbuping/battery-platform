<template>
  <div class="maintenance-page">
    <!-- Page Title -->
    <div class="page-title-bar">
      <h2 class="page-title">维保管理闭环</h2>
      <div class="breadcrumb">首页 / <span>维保管理</span></div>
    </div>

    <!-- Closed-loop Flow Visualization -->
    <div class="flow-container">
      <div class="flow-title">
        <div class="flow-icon">&#8634;</div>
        闭环管理流程
      </div>
      <div class="flow-steps">
        <div class="flow-step" v-for="(step, idx) in flowSteps" :key="idx">
          <div class="flow-step-number" :style="{ background: step.bg }">{{ idx + 1 }}</div>
          <div class="flow-step-label">{{ step.name }}</div>
          <div class="flow-step-desc">{{ step.desc }}</div>
        </div>
        <div class="flow-arrow" v-for="(arrow, idx) in 5" :key="'arrow-' + idx">&#10140;</div>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <label>关键词搜索:</label>
      <el-input
        v-model="searchKeyword"
        placeholder="工单编号 / 车辆编号 / 负责人"
        size="default"
        class="dark-input"
        clearable
        style="width: 200px;"
      />
      <label>工单状态:</label>
      <el-select v-model="filterStatus" placeholder="全部" size="default" class="dark-select" style="width: 130px;">
        <el-option label="全部" value="all" />
        <el-option label="待处理" value="pending" />
        <el-option label="处理中" value="processing" />
        <el-option label="已完成" value="completed" />
        <el-option label="已关闭" value="closed" />
      </el-select>
      <label>日期范围:</label>
      <el-date-picker
        v-model="dateFrom"
        type="date"
        placeholder="开始日期"
        size="default"
        class="dark-date-picker"
        style="width: 150px;"
      />
      <span class="date-separator">至</span>
      <el-date-picker
        v-model="dateTo"
        type="date"
        placeholder="结束日期"
        size="default"
        class="dark-date-picker"
        style="width: 150px;"
      />
      <el-button type="primary" class="dark-btn-primary" @click="handleSearch">
        &#128269; 查询
      </el-button>
      <el-button class="dark-btn-reset" @click="handleReset">
        &#8634; 重置
      </el-button>
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

    <!-- Work Order Table -->
    <div class="table-container">
      <WorkOrderList
        :work-orders="workOrders"
        :total="total"
        :loading="loading"
        v-model:page="currentPage"
        v-model:page-size="pageSize"
        @view="handleView"
        @process="handleProcessOrder"
      />
    </div>

    <!-- Detail Drawer -->
    <el-drawer
      v-model="drawerVisible"
      title=""
      direction="rtl"
      size="560px"
      :show-close="false"
      class="dark-drawer"
    >
      <!-- Panel Header -->
      <template #header>
        <div class="panel-header">
          <div class="panel-header-top">
            <span class="panel-order-id">{{ currentOrder?.id || '' }}</span>
            <button class="panel-close-btn" @click="drawerVisible = false">&times;</button>
          </div>
          <div class="panel-header-meta">
            <span :class="['panel-status-badge', getStatusBadgeClass(currentOrder?.status)]">
              <span v-if="currentOrder?.status === 'processing'" class="pulse-dot"></span>
              {{ getStatusText(currentOrder?.status) }}
            </span>
            <span class="meta-sep">|</span>
            <span class="meta-time">创建于 {{ currentOrder?.createTime ? formatDateTime(currentOrder.createTime, 'YYYY-MM-DD HH:mm') : '-' }}</span>
          </div>
        </div>
      </template>

      <!-- Panel Body -->
      <div class="panel-body">
        <!-- Basic Info Section -->
        <div class="panel-section">
          <div class="panel-section-title">
            <span class="section-icon blue">&#9432;</span>
            基本信息
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">车辆编号</span>
              <span class="info-value mono">{{ currentOrder?.vehicleId || currentOrder?.vin || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">车牌号</span>
              <span class="info-value">{{ currentOrder?.plateNumber || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">预警等级</span>
              <span class="info-value">
                <span :class="['badge', `badge-${getLevelBadge(currentOrder?.priority || currentOrder?.level)}`]">
                  {{ getLevelText(currentOrder?.priority || currentOrder?.level) }}
                </span>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">预警类型</span>
              <span class="info-value">{{ currentOrder?.typeName || getTypeText(currentOrder?.type) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ currentOrder?.createTime ? formatDateTime(currentOrder.createTime) : '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">负责人</span>
              <span class="info-value">{{ currentOrder?.assigneeName || '-' }}</span>
            </div>
          </div>
        </div>

        <!-- Closed-loop Timeline Section -->
        <div class="panel-section">
          <div class="panel-section-title">
            <span class="section-icon green">&#8634;</span>
            闭环处理流程
          </div>
          <ClosedLoopTimeline :steps="closedLoopSteps" />
        </div>
      </div>

      <!-- Panel Footer -->
      <template #footer>
        <div class="panel-footer">
          <div class="panel-elapsed">
            <span class="elapsed-icon">&#9201;</span>
            <span>已耗时:</span>
            <span class="elapsed-value">2小时32分钟</span>
          </div>
          <el-button class="dark-btn-export" @click="handleExport">
            &#128228; 导出工单报告
          </el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useWorkOrderStore } from '@/stores/workOrder'
import { storeToRefs } from 'pinia'
import { formatDateTime, getWorkOrderStatusText, getWorkOrderTypeText } from '@/utils/format'
import { ElMessage } from 'element-plus'
import ClosedLoopTimeline from '@/components/ClosedLoopTimeline.vue'
import WorkOrderList from '@/components/WorkOrderList.vue'

const workOrderStore = useWorkOrderStore()
const { workOrders, total, currentWorkOrder, closedLoopSteps, stats, loading, pendingCount, processingCount, completedCount } = storeToRefs(workOrderStore)

const currentPage = ref(1)
const pageSize = ref(20)
const drawerVisible = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('all')
const dateFrom = ref('')
const dateTo = ref('')

const currentOrder = computed(() => currentWorkOrder.value)

const flowSteps = [
  { name: '系统识别', desc: 'CAN数据异常检测', bg: 'linear-gradient(135deg, #00d4ff, #54c8ff)' },
  { name: '触发预警', desc: '智能分级预警', bg: 'linear-gradient(135deg, #ff4757, #ff6b81)' },
  { name: '推送告警', desc: '多渠道实时通知', bg: 'linear-gradient(135deg, #ffa502, #ffcb6e)' },
  { name: '现场处置', desc: '派发工单执行', bg: 'linear-gradient(135deg, #00d4ff, #54c8ff)' },
  { name: '复检校验', desc: '二次确认评估', bg: 'linear-gradient(135deg, #8e44ad, #bb6bd9)' },
  { name: '解除预警', desc: '闭环归档记录', bg: 'linear-gradient(135deg, #2ed573, #6bcb77)' }
]

const statCards = computed(() => [
  {
    icon: '&#9888;',
    label: '待处理',
    value: pendingCount.value || 12,
    unit: '件',
    iconBg: 'rgba(255,165,2,0.12)',
    iconColor: '#ffa502',
    valueColor: '#ffa502',
    trend: '&#9650; 较昨日 +3',
    trendDir: 'up'
  },
  {
    icon: '&#9881;',
    label: '处理中',
    value: processingCount.value || 5,
    unit: '件',
    iconBg: 'rgba(0,212,255,0.12)',
    iconColor: '#00d4ff',
    valueColor: '#00d4ff',
    trend: '&#9660; 较昨日 -1',
    trendDir: 'down'
  },
  {
    icon: '&#10003;',
    label: '本月完成',
    value: completedCount.value || 38,
    unit: '件',
    iconBg: 'rgba(46,213,115,0.12)',
    iconColor: '#2ed573',
    valueColor: '#2ed573',
    trend: '&#9650; 环比 +12.5%',
    trendDir: 'up'
  },
  {
    icon: '&#9201;',
    label: '平均处理时长',
    value: '4.2',
    unit: '小时',
    iconBg: 'rgba(136,153,170,0.12)',
    iconColor: '#8899aa',
    valueColor: '#8899aa',
    trend: '&#9660; 较上月 -0.8h',
    trendDir: 'down'
  }
])

const getLevelBadge = (level?: string) => {
  if (!level) return 'info'
  const map: Record<string, string> = {
    high: 'danger', critical: 'danger', '1': 'danger',
    medium: 'warning', warning: 'warning', '2': 'warning',
    low: 'info', info: 'info', '3': 'info'
  }
  return map[level] || 'info'
}

const getLevelText = (level?: string) => {
  if (!level) return '-'
  const map: Record<string, string> = {
    high: '一级', critical: '一级', '1': '一级',
    medium: '二级', warning: '二级', '2': '二级',
    low: '三级', info: '三级', '3': '三级'
  }
  return map[level] || level
}

const getTypeText = (type?: string) => {
  return type ? getWorkOrderTypeText(type) : '-'
}

const getStatusText = (status?: string) => {
  if (!status) return '-'
  return getWorkOrderStatusText(status)
}

const getStatusBadgeClass = (status?: string) => {
  if (status === 'processing') return 'status-processing'
  if (status === 'pending') return 'status-pending'
  if (status === 'completed') return 'status-completed'
  return 'status-completed'
}

const handleSearch = () => {
  workOrderStore.fetchWorkOrderList({
    page: 1,
    pageSize: pageSize.value,
    keyword: searchKeyword.value,
    status: filterStatus.value === 'all' ? undefined : filterStatus.value
  })
}

const handleReset = () => {
  searchKeyword.value = ''
  filterStatus.value = 'all'
  dateFrom.value = ''
  dateTo.value = ''
  workOrderStore.fetchWorkOrderList({ page: 1, pageSize: pageSize.value })
}

const handleView = (row: any) => {
  workOrderStore.setCurrentWorkOrder(row)
  workOrderStore.fetchClosedLoopData(row.id)
  drawerVisible.value = true
}

const handleProcessOrder = (row: any) => {
  ElMessage.success(`工单 ${row.id} 已开始处理`)
}

const handleExport = () => {
  drawerVisible.value = false
  ElMessage.success('工单报告已生成，正在下载...')
}

onMounted(() => {
  workOrderStore.fetchWorkOrderList()
  workOrderStore.fetchStats()
})
</script>

<style scoped>
.maintenance-page {
  padding: 20px 24px;
  background: #0d1b2a;
  min-height: calc(100vh - 60px);
  color: #e0e0e0;
}

/* Page Title */
.page-title-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #e0e0e0;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.page-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 20px;
  background: #00d4ff;
  border-radius: 2px;
}
.breadcrumb {
  font-size: 13px;
  color: #8899aa;
}
.breadcrumb span {
  color: #00d4ff;
}

/* Flow Container */
.flow-container {
  background: #1b2838;
  border-radius: 12px;
  padding: 24px 32px;
  margin-bottom: 20px;
  border: 1px solid rgba(255,255,255,0.06);
}
.flow-title {
  font-size: 14px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.flow-icon {
  width: 20px; height: 20px;
  background: linear-gradient(135deg, #00d4ff, #54c8ff);
  border-radius: 4px;
  display: flex; align-items: center; justify-content: center;
  font-size: 11px;
  color: #0d1b2a;
}
.flow-steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  padding: 0 10px;
}
.flow-steps::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 60px;
  right: 60px;
  height: 2px;
  background: linear-gradient(90deg, #00d4ff, #2ed573);
  transform: translateY(-50%);
  z-index: 0;
}
.flow-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;
  cursor: default;
  transition: transform 0.2s;
}
.flow-step:hover {
  transform: translateY(-3px);
}
.flow-step-number {
  width: 48px; height: 48px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 10px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.3);
}
.flow-step-label {
  font-size: 13px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 2px;
}
.flow-step-desc {
  font-size: 11px;
  color: #8899aa;
}
.flow-arrow {
  font-size: 20px;
  color: #8899aa;
  z-index: 1;
  margin-bottom: 30px;
}

/* Filter Bar */
.filter-bar {
  background: #1b2838;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 20px;
  border: 1px solid rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.filter-bar label {
  font-size: 13px;
  color: #8899aa;
  white-space: nowrap;
}
.date-separator {
  color: #8899aa;
  font-size: 13px;
}
.dark-input :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.04) !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  box-shadow: none !important;
  color: #e0e0e0;
}
.dark-input :deep(.el-input__inner) {
  color: #e0e0e0;
}
.dark-input :deep(.el-input__inner::placeholder) {
  color: #667788;
}
.dark-select :deep(.el-select__wrapper) {
  background: rgba(255,255,255,0.04) !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  box-shadow: none !important;
  color: #e0e0e0;
}
.dark-date-picker :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.04) !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
  box-shadow: none !important;
  color: #e0e0e0;
}
.dark-date-picker :deep(.el-input__inner) {
  color: #e0e0e0;
}

.dark-btn-primary {
  background: linear-gradient(135deg, #00d4ff, #54c8ff) !important;
  border: none !important;
  color: #0d1b2a !important;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(0,212,255,0.3);
}
.dark-btn-primary:hover {
  box-shadow: 0 4px 12px rgba(0,212,255,0.4) !important;
  transform: translateY(-1px);
}
.dark-btn-reset {
  background: rgba(255,255,255,0.04) !important;
  color: #8899aa !important;
  border: 1px solid rgba(255,255,255,0.08) !important;
}
.dark-btn-reset:hover {
  background: rgba(255,255,255,0.08) !important;
  color: #e0e0e0 !important;
}

/* Statistics Row */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  background: #1b2838;
  border-radius: 12px;
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid rgba(255,255,255,0.06);
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
}
.stat-icon {
  width: 52px; height: 52px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.stat-info { flex: 1; }
.stat-label {
  font-size: 13px;
  color: #8899aa;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}
.stat-unit {
  font-size: 13px;
  font-weight: 400;
  color: #8899aa;
  margin-left: 4px;
}
.stat-trend {
  font-size: 11px;
  margin-top: 2px;
  color: #8899aa;
}
.stat-trend.up { color: #ff4757; }
.stat-trend.down { color: #2ed573; }

/* Table Container */
.table-container {
  margin-bottom: 20px;
}

/* Drawer Styles */
.dark-drawer :deep(.el-drawer) {
  background: #1b2838 !important;
  border-left: 1px solid rgba(255,255,255,0.08);
}
.dark-drawer :deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 0;
  border-bottom: none;
}
.dark-drawer :deep(.el-drawer__body) {
  padding: 0;
}

/* Panel Header */
.panel-header {
  padding: 20px 24px;
  background: linear-gradient(135deg, #0d1b2a 0%, #1b2838 100%);
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.panel-header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.panel-order-id {
  font-family: "SF Mono", "Monaco", "Menlo", "Consolas", monospace;
  font-size: 16px;
  font-weight: 700;
  color: #e0e0e0;
  letter-spacing: 0.5px;
}
.panel-close-btn {
  width: 32px; height: 32px;
  border: none;
  background: rgba(255,255,255,0.08);
  border-radius: 8px;
  font-size: 20px;
  color: #8899aa;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
  line-height: 1;
}
.panel-close-btn:hover {
  background: rgba(255,255,255,0.15);
  color: #e0e0e0;
}
.panel-header-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}
.panel-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}
.status-processing {
  background: rgba(0,212,255,0.15);
  color: #7ec8ff;
  border: 1px solid rgba(0,212,255,0.25);
}
.status-pending {
  background: rgba(255,165,2,0.15);
  color: #fdcb6e;
  border: 1px solid rgba(255,165,2,0.25);
}
.status-completed {
  background: rgba(46,213,115,0.15);
  color: #6bcb77;
  border: 1px solid rgba(46,213,115,0.25);
}
.pulse-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.3); }
}
.meta-sep {
  opacity: 0.5;
}
.meta-time {
  opacity: 0.8;
  color: #8899aa;
}

/* Panel Body */
.panel-body {
  flex: 1;
  overflow-y: auto;
}
.panel-body::-webkit-scrollbar {
  width: 5px;
}
.panel-body::-webkit-scrollbar-track {
  background: transparent;
}
.panel-body::-webkit-scrollbar-thumb {
  background: rgba(255,255,255,0.08);
  border-radius: 3px;
}

/* Panel Section */
.panel-section {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.panel-section:last-child {
  border-bottom: none;
}
.panel-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.section-icon {
  width: 18px; height: 18px;
  border-radius: 4px;
  display: flex; align-items: center; justify-content: center;
  font-size: 10px;
  color: #fff;
}
.section-icon.blue { background: #00d4ff; }
.section-icon.green { background: #2ed573; }

/* Info Grid */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 20px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.info-label {
  font-size: 12px;
  color: #8899aa;
}
.info-value {
  font-size: 13px;
  color: #e0e0e0;
  font-weight: 500;
}
.info-value.mono {
  font-family: "SF Mono", "Monaco", "Menlo", "Consolas", monospace;
}

/* Badges */
.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  line-height: 1.5;
}
.badge-danger { background: rgba(255,71,87,0.12); color: #ff4757; border: 1px solid rgba(255,71,87,0.2); }
.badge-warning { background: rgba(255,165,2,0.12); color: #ffa502; border: 1px solid rgba(255,165,2,0.2); }
.badge-info { background: rgba(0,212,255,0.12); color: #00d4ff; border: 1px solid rgba(0,212,255,0.2); }
.badge-success { background: rgba(46,213,115,0.12); color: #2ed573; border: 1px solid rgba(46,213,115,0.2); }

/* Panel Footer */
.panel-footer {
  padding: 16px 24px;
  border-top: 1px solid rgba(255,255,255,0.06);
  background: rgba(255,255,255,0.02);
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.panel-elapsed {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #8899aa;
}
.elapsed-icon {
  font-size: 16px;
}
.elapsed-value {
  font-weight: 700;
  color: #ffa502;
  font-size: 15px;
}
.dark-btn-export {
  padding: 10px 24px;
  background: linear-gradient(135deg, #0d1b2a, #00d4ff) !important;
  color: #fff !important;
  border: none !important;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(0,212,255,0.3);
}
.dark-btn-export:hover {
  box-shadow: 0 4px 16px rgba(0,212,255,0.4);
  transform: translateY(-1px);
}

/* Responsive */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .flow-steps {
    flex-wrap: wrap;
    gap: 12px;
  }
  .flow-steps::before {
    display: none;
  }
  .flow-arrow {
    display: none;
  }
}
@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
