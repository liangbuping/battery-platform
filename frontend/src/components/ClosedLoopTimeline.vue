<template>
  <div class="closed-loop-timeline">
    <div class="timeline">
      <div
        v-for="(step, index) in steps"
        :key="step.id || index"
        :class="['timeline-step', `step-${step.status}`]"
      >
        <!-- Timeline Dot -->
        <div :class="['timeline-dot', `dot-${step.status}`]">
          <span v-if="step.status === 'completed'">&#10003;</span>
          <span v-else-if="step.status === 'processing'">&#8635;</span>
          <span v-else>&#9634;</span>
        </div>

        <!-- Step Header -->
        <div class="timeline-step-header">
          <span class="timeline-step-title">{{ step.name }}</span>
          <span :class="['timeline-step-status', `status-${step.status}`]">
            {{ getStepText(step.status) }}
          </span>
        </div>

        <!-- Step Card -->
        <div class="timeline-card">
          <!-- Meta: time, operator -->
          <div class="timeline-meta" v-if="step.startTime || step.operatorName">
            <span v-if="step.startTime" class="timeline-meta-item">
              <span class="meta-icon">&#128339;</span>
              <strong>{{ formatDateTime(step.startTime) }}</strong>
            </span>
            <span v-if="step.operatorName" class="timeline-meta-item">
              <span class="meta-icon">&#128100;</span>
              <strong>{{ step.operatorName }}</strong>
            </span>
          </div>

          <!-- Content -->
          <div v-if="step.remark" class="timeline-content">
            {{ step.remark }}
          </div>

          <!-- Opinion -->
          <div v-if="step.opinion" class="timeline-opinion">
            <div class="opinion-label">&#128172; 处理意见</div>
            {{ step.opinion }}
          </div>

          <!-- Evidence -->
          <div v-if="step.evidence && step.evidence.length > 0" class="timeline-evidence">
            <div
              v-for="(ev, evIdx) in step.evidence"
              :key="evIdx"
              class="evidence-thumb"
              :title="ev.name"
            >
              <span class="thumb-icon">{{ ev.type === 'image' ? '&#128247;' : '&#128196;' }}</span>
              <span class="thumb-label">{{ ev.name }}</span>
            </div>
          </div>

          <!-- Duration -->
          <div v-if="step.duration" :class="['timeline-duration', { 'duration-auto': step.status === 'completed' && step.isAuto, 'duration-active': step.status === 'processing' }]">
            <span class="duration-icon">&#9201;</span>
            耗时: <strong>{{ step.duration }}</strong>
          </div>
        </div>
      </div>
    </div>

    <div v-if="steps.length === 0" class="empty-state">
      暂无流程数据
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatDateTime } from '@/utils/format'

interface Step {
  id?: string
  name: string
  status: 'pending' | 'processing' | 'completed'
  operator?: string
  operatorName?: string
  startTime?: string
  endTime?: string
  remark?: string
  opinion?: string
  duration?: string
  isAuto?: boolean
  evidence?: Array<{ name: string; type?: string; url?: string }>
}

defineProps<{
  steps: Step[]
}>()

const getStepText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待完成',
    processing: '进行中',
    completed: '已完成'
  }
  return texts[status] || status
}
</script>

<style scoped>
.closed-loop-timeline {
  padding: 0;
}

.timeline {
  position: relative;
  padding-left: 28px;
}
.timeline::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: linear-gradient(180deg, #00d4ff 0%, #2ed573 60%, rgba(255,255,255,0.08) 100%);
  border-radius: 1px;
}

.timeline-step {
  position: relative;
  margin-bottom: 24px;
}
.timeline-step:last-child {
  margin-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: -28px;
  top: 4px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  z-index: 2;
  flex-shrink: 0;
  color: #fff;
}
.dot-completed {
  background: linear-gradient(135deg, #2ed573, #6BCB77);
  box-shadow: 0 2px 8px rgba(46,213,115,0.3);
}
.dot-processing {
  background: linear-gradient(135deg, #00d4ff, #54c8ff);
  box-shadow: 0 2px 8px rgba(0,212,255,0.3);
  animation: dotPulse 2s ease-in-out infinite;
}
.dot-pending {
  background: rgba(255,255,255,0.06);
  color: #8899aa;
  border: 2px dashed rgba(255,255,255,0.12);
}

@keyframes dotPulse {
  0%, 100% { box-shadow: 0 2px 8px rgba(0,212,255,0.3); }
  50% { box-shadow: 0 2px 16px rgba(0,212,255,0.5); }
}

.timeline-step-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.timeline-step-title {
  font-size: 14px;
  font-weight: 600;
  color: #e0e0e0;
}
.timeline-step-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: 500;
}
.status-done {
  background: rgba(46,213,115,0.12);
  color: #2ed573;
}
.status-active {
  background: rgba(0,212,255,0.12);
  color: #00d4ff;
}
.status-wait {
  background: rgba(136,153,170,0.12);
  color: #8899aa;
}

.timeline-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  padding: 14px 16px;
}
.step-pending .timeline-card {
  background: rgba(255,255,255,0.02);
  border-style: dashed;
  opacity: 0.7;
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #8899aa;
  flex-wrap: wrap;
}
.timeline-meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.meta-icon {
  font-size: 13px;
  opacity: 0.7;
}
.timeline-meta-item strong {
  color: #e0e0e0;
  font-weight: 500;
}

.timeline-content {
  font-size: 13px;
  color: #c0c8d0;
  line-height: 1.7;
  margin-bottom: 10px;
  padding-left: 2px;
}

.timeline-opinion {
  background: rgba(0,212,255,0.05);
  border-left: 3px solid #00d4ff;
  border-radius: 0 6px 6px 0;
  padding: 10px 14px;
  margin-bottom: 10px;
  font-size: 12.5px;
  color: #c0c8d0;
  line-height: 1.6;
}
.opinion-label {
  font-size: 11px;
  font-weight: 600;
  color: #00d4ff;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.timeline-evidence {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
.evidence-thumb {
  width: 72px;
  height: 54px;
  border-radius: 6px;
  border: 1px solid rgba(255,255,255,0.08);
  background: rgba(255,255,255,0.03);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  cursor: pointer;
  transition: all 0.2s;
}
.evidence-thumb:hover {
  border-color: #00d4ff;
  background: rgba(0,212,255,0.06);
  transform: translateY(-1px);
}
.thumb-icon {
  font-size: 16px;
  color: #8899aa;
}
.thumb-label {
  font-size: 9px;
  color: #8899aa;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 64px;
}

.timeline-duration {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #8899aa;
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.08);
  padding: 4px 10px;
  border-radius: 6px;
}
.timeline-duration strong {
  color: #e0e0e0;
  font-weight: 600;
}
.duration-auto {
  background: rgba(46,213,115,0.05);
  border-color: rgba(46,213,115,0.2);
  color: #2ed573;
}
.duration-auto strong {
  color: #2ed573;
}
.duration-active {
  background: rgba(0,212,255,0.05);
  border-color: rgba(0,212,255,0.2);
  color: #00d4ff;
}
.duration-active strong {
  color: #00d4ff;
}

.empty-state {
  text-align: center;
  color: #8899aa;
  padding: 40px 0;
  font-size: 14px;
}
</style>
