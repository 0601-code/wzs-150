<template>
  <div class="queue">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-weight: 600">排队叫号管理</span>
          <el-select v-model="filterStatus" placeholder="筛选状态" style="width: 140px" @change="loadData">
            <el-option label="全部" value="" />
            <el-option label="待确认" value="PENDING" />
            <el-option label="已签到" value="CHECKED_IN" />
            <el-option label="服务中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </div>
      </template>

      <el-table :data="queueList" stripe>
        <el-table-column prop="queueNumber" label="号数" width="70" align="center">
          <template #default="{ row }">
            <span class="queue-number">{{ row.queueNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentNo" label="预约编号" width="140" />
        <el-table-column label="居民信息" min-width="150">
          <template #default="{ row }">
            <div class="resident-info">
              <div class="name">{{ row.residentName || '居民' + row.residentId }}</div>
              <div class="tag-row">
                <el-tag v-if="row.elderly" type="warning" size="small">老人</el-tag>
                <el-tag v-if="row.disabled" type="danger" size="small">行动不便</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="serviceType" label="服务类型" width="100">
          <template #default="{ row }">
            {{ getServiceTypeName(row.serviceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="needHomeService" label="上门" width="70" align="center">
          <template #default="{ row }">
            {{ row.needHomeService ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="barberName" label="理发师" width="100" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'" type="success" size="small" @click="handleCheckIn(row.id)">签到</el-button>
            <el-button v-if="row.status === 'CHECKED_IN'" type="primary" size="small" @click="handleAssign(row)">分配理发师</el-button>
            <el-button v-if="row.status === 'IN_PROGRESS'" type="success" size="small" @click="handleComplete(row.id)">完成</el-button>
            <el-button v-if="row.status !== 'COMPLETED' && row.status !== 'CANCELLED'" type="danger" size="small" @click="handleCancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="assignDialogVisible" title="分配理发师" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="理发师">
          <el-select v-model="assignForm.volunteerId" placeholder="请选择理发师" style="width: 100%">
            <el-option v-for="v in volunteers" :key="v.id" :label="v.volunteerNo + ' - ' + (v.skillLevel === 'EXPERT' ? '资深' : v.skillLevel === 'SKILLED' ? '熟练' : '普通')" :value="v.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAppointments, checkIn, assignBarber, completeAppointment, cancelAppointment, getAvailableVolunteers } from '../api'

const queueList = ref([])
const filterStatus = ref('')
const assignDialogVisible = ref(false)
const currentAppointmentId = ref(null)
const volunteers = ref([])

const assignForm = reactive({
  volunteerId: null
})

const loadData = async () => {
  const params = filterStatus.value ? { status: filterStatus.value } : {}
  params.needHomeService = false
  const list = await getAppointments(params)
  queueList.value = list
}

const loadVolunteers = async () => {
  volunteers.value = await getAvailableVolunteers()
}

const handleCheckIn = async (id) => {
  try {
    await checkIn(id)
    ElMessage.success('签到成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleAssign = (row) => {
  currentAppointmentId.value = row.id
  assignForm.volunteerId = null
  assignDialogVisible.value = true
}

const confirmAssign = async () => {
  if (!assignForm.volunteerId) {
    ElMessage.warning('请选择理发师')
    return
  }
  try {
    await assignBarber(currentAppointmentId.value, assignForm.volunteerId)
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleComplete = async (id) => {
  try {
    await completeAppointment(id)
    ElMessage.success('服务已完成')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleCancel = async (id) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await cancelAppointment(id)
      ElMessage.success('已取消')
      loadData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const getServiceTypeName = (type) => {
  const map = { NORMAL: '普通理发', SENIOR: '老人剪发', SPECIAL: '特殊需求' }
  return map[type] || type
}

const getStatusName = (status) => {
  const map = {
    PENDING: '待确认', CONFIRMED: '已确认', CHECKED_IN: '已签到',
    IN_PROGRESS: '服务中', COMPLETED: '已完成', CANCELLED: '已取消'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    PENDING: 'warning', CONFIRMED: 'primary', CHECKED_IN: 'success',
    IN_PROGRESS: 'warning', COMPLETED: 'success', CANCELLED: 'info'
  }
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

.queue-number {
  display: inline-block;
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background: #3b82f6;
  color: #fff;
  border-radius: 50%;
  font-weight: bold;
  font-size: 16px;
}

.resident-info .name {
  font-weight: 500;
}

.tag-row {
  margin-top: 4px;
  display: flex;
  gap: 4px;
}
</style>
