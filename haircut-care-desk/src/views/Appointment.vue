<template>
  <div class="appointment">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-weight: 600">居民预约登记</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px">
        <el-form-item label="选择活动" prop="activityId">
          <el-select v-model="form.activityId" placeholder="请选择活动" style="width: 100%" @change="loadTimeSlots">
            <el-option v-for="act in activities" :key="act.id" :label="act.activityName" :value="act.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="选择时段" prop="timeSlotId">
          <el-select v-model="form.timeSlotId" placeholder="请选择时段" style="width: 100%">
            <el-option
              v-for="slot in timeSlots"
              :key="slot.id"
              :label="`${slot.startTime} - ${slot.endTime} (${slot.currentCount}/${slot.maxCount})`"
              :value="slot.id"
              :disabled="slot.currentCount >= slot.maxCount"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="居民ID" prop="residentId">
          <el-input v-model="form.residentId" type="number" placeholder="请输入居民ID" />
          <div style="font-size: 12px; color: #999; margin-top: 4px">测试居民ID: 5,6,7,8,9</div>
        </el-form-item>

        <el-form-item label="服务类型" prop="serviceType">
          <el-radio-group v-model="form.serviceType">
            <el-radio label="NORMAL">普通理发</el-radio>
            <el-radio label="SENIOR">老人剪发</el-radio>
            <el-radio label="SPECIAL">特殊需求</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="需要上门">
          <el-switch v-model="form.needHomeService" />
        </el-form-item>

        <el-form-item v-if="form.needHomeService" label="上门地址" prop="homeAddress">
          <el-input v-model="form.homeAddress" type="textarea" :rows="2" placeholder="请输入详细上门地址" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交预约</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span style="font-weight: 600">最近预约记录</span>
      </template>
      <el-table :data="appointments" stripe>
        <el-table-column prop="appointmentNo" label="预约编号" width="160" />
        <el-table-column prop="queueNumber" label="排队号" width="80" />
        <el-table-column prop="serviceType" label="服务类型" width="100">
          <template #default="{ row }">
            {{ getServiceTypeName(row.serviceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="needHomeService" label="上门" width="80">
          <template #default="{ row }">
            {{ row.needHomeService ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="预约时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPublicActivities, getActivityTimeSlots, createAppointment, getAppointments } from '../api'

const formRef = ref()
const loading = ref(false)
const activities = ref([])
const timeSlots = ref([])
const appointments = ref([])

const form = reactive({
  activityId: null,
  timeSlotId: null,
  residentId: 5,
  serviceType: 'NORMAL',
  needHomeService: false,
  homeAddress: '',
  remark: ''
})

const rules = {
  activityId: [{ required: true, message: '请选择活动', trigger: 'change' }],
  residentId: [{ required: true, message: '请输入居民ID', trigger: 'blur' }]
}

const loadActivities = async () => {
  activities.value = await getPublicActivities()
  if (activities.value.length > 0) {
    form.activityId = activities.value[0].id
    await loadTimeSlots(activities.value[0].id)
  }
}

const loadTimeSlots = async (activityId) => {
  if (!activityId) return
  timeSlots.value = await getActivityTimeSlots(activityId)
}

const loadAppointments = async () => {
  appointments.value = await getAppointments({ activityId: form.activityId })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await createAppointment(form)
        ElMessage.success('预约成功！')
        resetForm()
        loadTimeSlots(form.activityId)
        loadAppointments()
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  form.timeSlotId = null
  form.serviceType = 'NORMAL'
  form.needHomeService = false
  form.homeAddress = ''
  form.remark = ''
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
  loadActivities()
  loadAppointments()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
