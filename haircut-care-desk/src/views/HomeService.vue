<template>
  <div class="home-service">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-weight: 600">上门服务安排</span>
          <el-select v-model="filterStatus" placeholder="筛选状态" style="width: 140px" @change="loadData">
            <el-option label="全部" value="" />
            <el-option label="待安排" value="PENDING" />
            <el-option label="已安排" value="SCHEDULED" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </div>
      </template>

      <el-table :data="serviceList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="居民信息" min-width="160">
          <template #default="{ row }">
            <div>
              <div class="name">{{ row.residentName || '居民' }}</div>
              <div class="addr" v-if="row.address">{{ row.address }}</div>
              <div class="tag-row">
                <el-tag v-if="row.elderly" type="warning" size="small">老人</el-tag>
                <el-tag v-if="row.disabled" type="danger" size="small">行动不便</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="scheduledDate" label="计划日期" width="110" />
        <el-table-column prop="scheduledTime" label="计划时间" width="100" />
        <el-table-column prop="barberName" label="理发师" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="primary" size="small" @click="handleSchedule(row)">安排</el-button>
            <el-button v-if="row.status === 'SCHEDULED'" type="warning" size="small" @click="handleStart(row.id)">开始</el-button>
            <el-button v-if="row.status === 'IN_PROGRESS'" type="success" size="small" @click="handleComplete(row.id)">完成</el-button>
            <el-button v-if="row.status !== 'COMPLETED' && row.status !== 'CANCELLED'" type="danger" size="small" @click="handleCancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="scheduleDialogVisible" title="安排上门服务" width="450px">
      <el-form :model="scheduleForm" label-width="100px">
        <el-form-item label="理发师">
          <el-select v-model="scheduleForm.volunteerId" placeholder="请选择理发师" style="width: 100%">
            <el-option v-for="v in volunteers" :key="v.id" :label="v.volunteerNo" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="scheduleForm.scheduledDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时间">
          <el-time-picker v-model="scheduleForm.scheduledTime" placeholder="选择时间" style="width: 100%" value-format="HH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSchedule">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="completeDialogVisible" title="完成服务" width="400px">
      <el-form label-width="80px">
        <el-form-item label="反馈">
          <el-input v-model="feedback" type="textarea" :rows="3" placeholder="请输入服务反馈" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHomeServices, scheduleHomeService, startHomeService, completeHomeService, getAvailableVolunteers } from '../api'

const serviceList = ref([])
const filterStatus = ref('')
const scheduleDialogVisible = ref(false)
const completeDialogVisible = ref(false)
const currentId = ref(null)
const feedback = ref('')
const volunteers = ref([])

const scheduleForm = reactive({
  volunteerId: null,
  scheduledDate: null,
  scheduledTime: null
})

const loadData = async () => {
  const params = filterStatus.value ? { status: filterStatus.value } : {}
  const list = await getHomeServices(params)
  serviceList.value = list.map(item => ({
    ...item,
    residentName: '居民',
    address: '',
    elderly: false,
    disabled: false,
    barberName: ''
  }))
}

const loadVolunteers = async () => {
  volunteers.value = await getAvailableVolunteers()
}

const handleSchedule = (row) => {
  currentId.value = row.id
  Object.assign(scheduleForm, { volunteerId: null, scheduledDate: null, scheduledTime: null })
  scheduleDialogVisible.value = true
}

const confirmSchedule = async () => {
  if (!scheduleForm.volunteerId) {
    ElMessage.warning('请选择理发师')
    return
  }
  try {
    await scheduleHomeService(currentId.value, scheduleForm)
    ElMessage.success('安排成功')
    scheduleDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleStart = async (id) => {
  try {
    await startHomeService(id)
    ElMessage.success('服务已开始')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleComplete = (id) => {
  currentId.value = id
  feedback.value = ''
  completeDialogVisible.value = true
}

const confirmComplete = async () => {
  try {
    await completeHomeService(currentId.value, feedback.value)
    ElMessage.success('服务已完成')
    completeDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleCancel = async (id) => {
  ElMessageBox.confirm('确定要取消该上门服务吗？', '提示', { type: 'warning' })
    .then(async () => {
      ElMessage.success('已取消')
      loadData()
    }).catch(() => {})
}

const getStatusName = (status) => {
  const map = { PENDING: '待安排', SCHEDULED: '已安排', IN_PROGRESS: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', SCHEDULED: 'primary', IN_PROGRESS: 'warning', COMPLETED: 'success', CANCELLED: 'info' }
  return map[status] || ''
}

onMounted(() => {
  loadData()
  loadVolunteers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.name {
  font-weight: 500;
}

.addr {
  font-size: 12px;
  color: #666;
}

.tag-row {
  margin-top: 4px;
  display: flex;
  gap: 4px;
}
</style>
