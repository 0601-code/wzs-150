<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #3b82f6">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalAppointments || 0 }}</div>
              <div class="stat-label">总预约数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #10b981">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completedCount || 0 }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f59e0b">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.checkedInCount || 0 }}</div>
              <div class="stat-label">等待中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ef4444">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.homeServiceCount || 0 }}</div>
              <div class="stat-label">上门服务</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span style="font-weight: 600">今日预约状态分布</span>
          </template>
          <div class="status-list">
            <div class="status-item">
              <span>待确认</span>
              <span class="status-count">{{ stats.confirmedCount || 0 }}</span>
            </div>
            <div class="status-item">
              <span>已签到</span>
              <span class="status-count">{{ stats.checkedInCount || 0 }}</span>
            </div>
            <div class="status-item">
              <span>服务中</span>
              <span class="status-count">{{ stats.inProgressCount || 0 }}</span>
            </div>
            <div class="status-item">
              <span>已完成</span>
              <span class="status-count">{{ stats.completedCount || 0 }}</span>
            </div>
            <div class="status-item">
              <span>已取消</span>
              <span class="status-count">{{ stats.cancelledCount || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span style="font-weight: 600">特殊人群统计</span>
          </template>
          <div class="status-list">
            <div class="status-item">
              <span>老人</span>
              <span class="status-count elderly">{{ stats.elderlyCount || 0 }}</span>
            </div>
            <div class="status-item">
              <span>行动不便</span>
              <span class="status-count disabled">{{ stats.disabledCount || 0 }}</span>
            </div>
            <div class="status-item">
              <span>参与理发师</span>
              <span class="status-count">{{ stats.volunteerCount || 0 }}</span>
            </div>
            <div class="status-item">
              <span>工具消毒次数</span>
              <span class="status-count">{{ stats.totalDisinfestations || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getActivityStatistics } from '../api'

const stats = ref({})

const loadData = async () => {
  try {
    const data = await getActivityStatistics(1)
    stats.value = data
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.stat-card {
  border: none;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.status-count {
  font-weight: 600;
  color: #3b82f6;
  font-size: 18px;
}

.status-count.elderly {
  color: #f59e0b;
}

.status-count.disabled {
  color: #ef4444;
}
</style>
