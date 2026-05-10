<template>
  <div class="mobile-workorder-page">
    <div class="mobile-header">
      <h1>工单处理</h1>
      <el-tag type="primary">{{ workOrders.length }}个待办</el-tag>
    </div>
    
    <div class="filter-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
        <span class="count" v-if="tab.count > 0">({{ tab.count }})</span>
      </div>
    </div>
    
    <div class="workorder-list">
      <div
        v-for="workOrder in filteredWorkOrders"
        :key="workOrder.id"
        class="workorder-card"
        @click="handleClick(workOrder)"
      >
        <div class="card-header">
          <span class="order-id">{{ workOrder.id }}</span>
          <el-tag :type="getStatusType(workOrder.status)" size="small">
            {{ getStatusText(workOrder.status) }}
          </el-tag>
        </div>
        <div class="card-body">
          <div class="vehicle-info">
            <span class="plate">{{ workOrder.plateNumber }}</span>
            <span class="vin">{{ workOrder.vin }}</span>
          </div>
          <div class="order-info">
            <div class="info-row">
              <span class="label">类型:</span>
              <el-tag :type="getTypeType(workOrder.type)" size="small">
                {{ getTypeText(workOrder.type) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">优先级:</span>
              <el-tag :type="getPriorityType(workOrder.priority)" size="small">
                {{ getPriorityText(workOrder.priority) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">截止时间:</span>
              <span :class="{ 'text-danger': isOverdue(workOrder.deadline) }">
                {{ formatDate(workOrder.deadline) }}
              </span>
            </div>
          </div>
          <div class="description">{{ workOrder.description }}</div>
        </div>
        <div class="card-footer">
          <el-button
            v-if="workOrder.status === 'pending'"
            type="primary"
            size="small"
            @click.stop="handleAccept(workOrder)"
          >
            接单
          </el-button>
          <el-button
            v-else-if="workOrder.status === 'processing'"
            type="success"
            size="small"
            @click.stop="handleComplete(workOrder)"
          >
            完成
          </el-button>
          <el-button size="small" @click.stop="handleDetail(workOrder)">
            详情
          </el-button>
        </div>
      </div>
      
      <el-empty v-if="filteredWorkOrders.length === 0" description="暂无工单" />
    </div>
    
    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="工单详情"
      width="90%"
      class="mobile-dialog"
    >
      <el-descriptions :column="1" border v-if="currentWorkOrder">
        <el-descriptions-item label="工单号">{{ currentWorkOrder.id }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ currentWorkOrder.plateNumber }}</el-descriptions-item>
        <el-descriptions-item label="VIN">{{ currentWorkOrder.vin }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ getTypeText(currentWorkOrder.type) }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ getPriorityText(currentWorkOrder.priority) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(currentWorkOrder.status) }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ formatDateTime(currentWorkOrder.deadline) }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ currentWorkOrder.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { formatDate, formatDateTime, getWorkOrderStatusText, getWorkOrderTypeText } from '@/utils/format'
import { ElMessage } from 'element-plus'

interface WorkOrder {
  id: string
  vehicleId: string
  vin: string
  plateNumber: string
  type: 'repair' | 'maintenance' | 'inspection'
  status: 'pending' | 'assigned' | 'processing' | 'completed' | 'closed'
  priority: 'high' | 'medium' | 'low'
  description: string
  createTime: string
  deadline: string
}

// 模拟数据
const workOrders = ref<WorkOrder[]>([
  {
    id: 'WO001',
    vehicleId: 'V001',
    vin: 'LSVAG2180E2100001',
    plateNumber: '京A12345',
    type: 'repair',
    status: 'pending',
    priority: 'high',
    description: '电池包温度异常，需要现场检查',
    createTime: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
    deadline: new Date(Date.now() + 1000 * 60 * 60 * 4).toISOString()
  },
  {
    id: 'WO002',
    vehicleId: 'V002',
    vin: 'LSVAG2180E2100002',
    plateNumber: '京A12346',
    type: 'maintenance',
    status: 'processing',
    priority: 'medium',
    description: '定期保养，更换空调滤芯',
    createTime: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    deadline: new Date(Date.now() + 1000 * 60 * 60 * 6).toISOString()
  },
  {
    id: 'WO003',
    vehicleId: 'V003',
    vin: 'LSVAG2180E2100003',
    plateNumber: '京A12347',
    type: 'inspection',
    status: 'pending',
    priority: 'low',
    description: '季度巡检',
    createTime: new Date(Date.now() - 1000 * 60 * 60).toISOString(),
    deadline: new Date(Date.now() + 1000 * 60 * 60 * 24).toISOString()
  }
])

const activeTab = ref('all')
const detailVisible = ref(false)
const currentWorkOrder = ref<WorkOrder | null>(null)

const tabs = computed(() => [
  { label: '全部', value: 'all', count: workOrders.value.length },
  { label: '待处理', value: 'pending', count: workOrders.value.filter(w => w.status === 'pending').length },
  { label: '处理中', value: 'processing', count: workOrders.value.filter(w => w.status === 'processing').length }
])

const filteredWorkOrders = computed(() => {
  if (activeTab.value === 'all') return workOrders.value
  return workOrders.value.filter(w => w.status === activeTab.value)
})

const getTypeType = (type: string) => {
  const types: Record<string, 'primary' | 'success' | 'warning'> = {
    repair: 'danger',
    maintenance: 'warning',
    inspection: 'success'
  }
  return types[type] || 'info'
}

const getTypeText = (type: string) => {
  return getWorkOrderTypeText(type)
}

const getPriorityType = (priority: string) => {
  const types: Record<string, 'danger' | 'warning' | 'info'> = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[priority] || 'info'
}

const getPriorityText = (priority: string) => {
  const texts: Record<string, string> = {
    high: '高',
    medium: '中',
    low: '低'
  }
  return texts[priority] || priority
}

const getStatusType = (status: string) => {
  const types: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    pending: 'info',
    assigned: 'warning',
    processing: 'primary',
    completed: 'success',
    closed: 'info'
  }
  return types[status] || ''
}

const getStatusText = (status: string) => {
  return getWorkOrderStatusText(status)
}

const isOverdue = (deadline: string) => {
  return new Date(deadline) < new Date()
}

const handleClick = (workOrder: WorkOrder) => {
  currentWorkOrder.value = workOrder
}

const handleAccept = (workOrder: WorkOrder) => {
  workOrder.status = 'processing'
  ElMessage.success(`已接单: ${workOrder.id}`)
}

const handleComplete = (workOrder: WorkOrder) => {
  workOrder.status = 'completed'
  ElMessage.success(`工单已完成: ${workOrder.id}`)
}

const handleDetail = (workOrder: WorkOrder) => {
  currentWorkOrder.value = workOrder
  detailVisible.value = true
}
</script>

<style scoped>
.mobile-workorder-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 20px;
}

.mobile-header {
  background: #fff;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.mobile-header h1 {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.filter-tabs {
  display: flex;
  background: #fff;
  padding: 0 16px;
  border-bottom: 1px solid #f0f0f0;
}

.tab-item {
  padding: 12px 16px;
  font-size: 14px;
  color: #595959;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.tab-item.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

.tab-item .count {
  font-size: 12px;
  color: #8c8c8c;
}

.workorder-list {
  padding: 12px;
}

.workorder-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
}

.card-body {
  margin-bottom: 12px;
}

.vehicle-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.plate {
  font-weight: 600;
  color: #262626;
  font-size: 15px;
}

.vin {
  font-size: 12px;
  color: #8c8c8c;
}

.order-info {
  margin-bottom: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 13px;
}

.info-row .label {
  color: #8c8c8c;
  width: 60px;
}

.text-danger {
  color: #f5222d;
}

.description {
  font-size: 13px;
  color: #595959;
  line-height: 1.5;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.card-footer {
  display: flex;
  gap: 8px;
}
</style>
