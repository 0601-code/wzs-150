<template>
  <div class="report">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-weight: 600">选择活动</span>
          <el-select v-model="selectedActivityId" placeholder="请选择活动" style="width: 240px" @change="loadReport">
            <el-option v-for="act in activities" :key="act.id" :label="act.activityName" :value="act.id" />
          </el-select>
        </div>
      </template>

      <el-row :gutter="20" v-if="statistics">
        <el-col :span="4">
          <div class="stat-box">
            <div class="stat-num">{{ statistics.totalAppointments || 0 }}</div>
            <div class="stat-label">总预约数</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-box" style="background: #dbeafe">
            <div class="stat-num" style="color: #2563eb">{{ statistics.confirmedCount || 0 }}</div>
            <div class="stat-label" style="color: #3b82f6">已确认</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-box" style="background: #dcfce7">
            <div class="stat-num" style="color: #15803d">{{ statistics.checkedInCount || 0 }}</div>
            <div class="stat-label" style="color: #22c55e">已签到</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-box" style="background: #fef3c7">
            <div class="stat-num" style="color: #b45309">{{ statistics.inProgressCount || 0 }}</div>
            <div class="stat-label" style="color: #f59e0b">服务中</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-box" style="background: #d1fae5">
            <div class="stat-num" style="color: #047857">{{ statistics.completedCount || 0 }}</div>
            <div class="stat-label" style="color: #10b981">已完成</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-box" style="background: #fee2e2">
            <div class="stat-num" style="color: #b91c1c">{{ statistics.cancelledCount || 0 }}</div>
            <div class="stat-label" style="color: #ef4444">已取消</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span style="font-weight: 600">服务分类统计</span>
          </template>
          <div class="stats-list" v-if="statistics">
            <div class="stats-item">
              <span>上门服务</span>
              <span class="count">{{ statistics.homeServiceCount || 0 }}</span>
            </div>
            <div class="stats-item">
              <span>老人</span>
              <span class="count elderly">{{ statistics.elderlyCount || 0 }}</span>
            </div>
            <div class="stats-item">
              <span>行动不便</span>
              <span class="count disabled">{{ statistics.disabledCount || 0 }}</span>
            </div>
            <div class="stats-item">
              <span>参与理发师</span>
              <span class="count">{{ statistics.volunteerCount || 0 }}</span>
            </div>
            <div class="stats-item">
              <span>工具消毒次数</span>
              <span class="count">{{ statistics.totalDisinfestations || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span style="font-weight: 600">活动信息</span>
          </template>
          <div class="activity-info" v-if="statistics && statistics.activity">
            <div class="info-item">
              <span class="label">活动名称：</span>
              <span>{{ statistics.activity.activityName }}</span>
            </div>
            <div class="info-item">
              <span class="label">活动日期：</span>
              <span>{{ statistics.activity.activityDate }}</span>
            </div>
            <div class="info-item">
              <span class="label">活动时间：</span>
              <span>{{ statistics.activity.startTime }} - {{ statistics.activity.endTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">活动地点：</span>
              <span>{{ statistics.activity.location }}</span>
            </div>
            <div class="info-item">
              <span class="label">最大容量：</span>
              <span>{{ statistics.activity.maxCapacity }} 人</span>
            </div>
            <div class="info-item">
              <span class="label">活动状态：</span>
              <el-tag :type="getActivityStatusType(statistics.activity.status)">
                {{ getActivityStatusName(statistics.activity.status) }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span style="font-weight: 600">参与名单导出</span>
          <el-button type="primary" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出 Excel
          </el-button>
        </div>
      </template>
      <el-alert type="success" :closable="false">
        点击导出按钮可下载当前活动的所有参与人员名单，包含居民信息、服务类型、理发师、签到时间等详细数据。
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getActivities, getActivityStatistics, exportAppointments } from '../api'

const activities = ref([])
const selectedActivityId = ref(null)
const statistics = ref(null)

const loadActivities = async () => {
  activities.value = await getActivities()
  if (activities.value.length > 0) {
    selectedActivityId.value = activities.value[0].id
    await loadReport()
  }
}

const loadReport = async () => {
  if (!selectedActivityId.value) return
  statistics.value = await getActivityStatistics(selectedActivityId.value)
}

const handleExport = () => {
  if (!selectedActivityId.value) {
    ElMessage.warning('请先选择活动')
    return
  }
  exportAppointments(selectedActivityId.value)
  ElMessage.success('导出成功')
}

const getActivityStatusName = (status) => {
  const map = { UPCOMING: '未开始', ONGOING: '进行中', COMPLETED: '已结束' }
  return map[status] || status
}

const getActivityStatusType = (status) => {
  const map = { UPCOMING: 'primary', ONGOING: 'success', COMPLETED: 'info' }
  return map[status] || ''
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-box {
  background: #f0f9ff;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.stat-num {
  font-size: 32px;
  font-weight: bold;
  color: #0ea5e9;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-top: 8px;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stats-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.stats-item .count {
  font-weight: 600;
  font-size: 18px;
  color: #3b82f6;
}

.stats-item .count.elderly {
  color: #f59e0b;
}

.stats-item .count.disabled {
  color: #ef4444;
}

.activity-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  width: 100px;
  color: #6b7280;
}
</style>
