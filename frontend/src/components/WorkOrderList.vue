<template>
  <div class="work-order-list">
    <div class="list-header">
      <h3 class="title">&#128203; 工单列表</h3>
      <span class="record-count">共 <strong>{{ total }}</strong> 条记录</span>
    </div>

    <el-table
      :data="workOrders"
      size="small"
      v-loading="loading"
      class="dark-table"
      :header-cell-style="headerCellStyle"
      :row-style="rowStyle"
      style="width: 100%"
    >
      <el-table-column prop="id" label="工单编号" width="140" show-overflow-tooltip>
        <template #default="{ row }">
          <span class="order-id">{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="vehicleId" label="车辆编号" width="140" show-overflow-tooltip>
        <template #default="{ row }">
          <span class="vehicle-id">{{ row.vehicleId || row.vin }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="level" label="预警等级" width="100">
        <template #default="{ row }">
          <span :class="['badge', `badge-${getLevelBadge(row.priority || row.level)}`]">
            {{ getLevelText(row.priority || row.level) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="typeName" label="预警类型" width="130">
        <template #default="{ row }">
          {{ row.typeName || getTypeText(row.type) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="工单状态" width="100">
        <template #default="{ row }">
          <span :class="['badge', `badge-${getStatusBadge(row.status)}`]">
            {{ getStatusText(row.status) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="assigneeName" label="负责人" width="100" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.assigneeName || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <div class="action-btns">
            <button class="btn-detail" @click.stop="handleView(row)">&#128196; 详情</button>
            <button
              v-if="row.status === 'pending'"
              class="btn-handle"
              @click.stop="handleProcess(row)"
            >处理</button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <div class="pagination-info">显示第 1-{{ workOrders.length }} 条，共 {{ total }} 条</div>
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
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { formatDateTime, getWorkOrderStatusText, getWorkOrderTypeText } from '@/utils/format'

interface WorkOrder {
  id: string
  vehicleId?: string
  vin?: string
  plateNumber?: string
  type?: 'repair' | 'maintenance' | 'inspection'
  typeName?: string
  level?: string
  priority?: string
  status: 'pending' | 'assigned' | 'processing' | 'completed' | 'closed'
  description?: string
  assignee?: string
  assigneeName?: string
  createTime: string
  deadline?: string
}

const props = defineProps<{
  workOrders: WorkOrder[]
  total: number
  loading?: boolean
}>()

const emit = defineEmits<{
  'update:page': [page: number]
  'update:pageSize': [pageSize: number]
  rowClick: [workOrder: WorkOrder]
  view: [workOrder: WorkOrder]
  create: []
  process: [workOrder: WorkOrder]
}>()

const currentPage = ref(1)
const pageSize = ref(20)

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

const getStatusBadge = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    assigned: 'info',
    processing: 'info',
    completed: 'success',
    closed: 'gray'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  return getWorkOrderStatusText(status)
}

const headerCellStyle = () => ({
  background: 'rgba(255,255,255,0.04)',
  color: '#8899aa',
  borderBottom: '1px solid rgba(255,255,255,0.08)',
  fontSize: '12px',
  fontWeight: '600'
})

const rowStyle = () => ({
  borderBottom: '1px solid rgba(255,255,255,0.05)'
})

const handleView = (row: WorkOrder) => {
  emit('view', row)
}

const handleProcess = (row: WorkOrder) => {
  emit('process', row)
}

const handlePageChange = () => {
  emit('update:page', currentPage.value)
  emit('update:pageSize', pageSize.value)
}
</script>

<style scoped>
.work-order-list {
  background: #1b2838;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255,255,255,0.06);
}

.list-header {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.title {
  font-size: 15px;
  font-weight: 600;
  color: #e0e0e0;
  margin: 0;
}

.record-count {
  font-size: 13px;
  color: #8899aa;
}
.record-count strong {
  color: #e0e0e0;
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

.order-id {
  font-family: "SF Mono", "Monaco", "Menlo", "Consolas", monospace;
  color: #00d4ff;
  font-weight: 500;
  font-size: 12.5px;
}
.vehicle-id {
  font-family: "SF Mono", "Monaco", "Menlo", "Consolas", monospace;
  color: #e0e0e0;
  font-weight: 500;
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
.badge-gray { background: rgba(136,153,170,0.12); color: #8899aa; border: 1px solid rgba(136,153,170,0.2); }

/* Action buttons */
.action-btns {
  display: flex;
  gap: 6px;
}
.btn-detail {
  background: linear-gradient(135deg, #00d4ff, #54c8ff);
  color: #0d1b2a;
  border: none;
  padding: 5px 14px;
  font-size: 12px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.btn-detail:hover {
  box-shadow: 0 3px 10px rgba(0,212,255,0.4);
  transform: translateY(-1px);
}
.btn-handle {
  background: transparent;
  color: #2ed573;
  border: 1px solid rgba(46,213,115,0.3);
  padding: 5px 12px;
  font-size: 12px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}
.btn-handle:hover {
  background: #2ed573;
  color: #0d1b2a;
  border-color: #2ed573;
}

/* Pagination */
.pagination-wrapper {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid rgba(255,255,255,0.06);
}
.pagination-info {
  font-size: 13px;
  color: #8899aa;
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
</style>
