<template>
  <div class="tools">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span style="font-weight: 600">工具状态</span>
              <el-select v-model="filterStatus" placeholder="筛选状态" style="width: 120px" @change="loadTools">
                <el-option label="全部" value="" />
                <el-option label="可用" value="AVAILABLE" />
                <el-option label="使用中" value="IN_USE" />
                <el-option label="消毒中" value="DISINFECTING" />
              </el-select>
            </div>
          </template>

          <el-table :data="tools" stripe size="small">
            <el-table-column prop="toolNo" label="编号" width="80" />
            <el-table-column prop="toolName" label="名称" width="100" />
            <el-table-column prop="typeName" label="类型" width="90" />
            <el-table-column prop="useCount" label="使用次数" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="getToolStatusType(row.status)" size="small">
                  {{ getToolStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="130">
              <template #default="{ row }">
                <el-button v-if="row.status === 'AVAILABLE'" type="primary" size="small" @click="handleUse(row)">领取</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span style="font-weight: 600">消毒记录</span>
          </template>
          <el-table :data="disinfestations" stripe size="small">
            <el-table-column prop="id" label="ID" width="50" />
            <el-table-column prop="toolId" label="工具ID" width="70" />
            <el-table-column prop="disinfectionMethod" label="方式" width="90" />
            <el-table-column prop="durationMinutes" label="时长(分)" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'COMPLETED' ? '已完成' : '进行中' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button v-if="row.status !== 'COMPLETED'" type="success" size="small" @click="handleCompleteDisinfestation(row.id)">完成</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span style="font-weight: 600">使用记录</span>
      </template>
      <el-alert type="info" :closable="false" style="margin-bottom: 12px">
        工具归还后自动进入消毒流程，消毒完成后状态变为可用。
      </el-alert>
      <el-empty v-if="usageRecords.length === 0" description="暂无使用记录" />
      <el-table v-else :data="usageRecords" stripe size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="toolId" label="工具ID" width="80" />
        <el-table-column prop="volunteerId" label="使用人ID" width="100" />
        <el-table-column prop="usedAt" label="领取时间" width="160" />
        <el-table-column prop="returnedAt" label="归还时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'RETURNED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'RETURNED' ? '已归还' : '使用中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button v-if="row.status === 'IN_USE'" type="warning" size="small" @click="handleReturn(row.id)">归还</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="useDialogVisible" title="领取工具" width="350px">
      <el-form label-width="80px">
        <el-form-item label="理发师ID">
          <el-input-number v-model="useForm.volunteerId" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="useDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUse">确认领取</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTools, getToolTypes, useTool, returnTool, getDisinfestations, completeDisinfestation } from '../api'

const tools = ref([])
const toolTypes = ref([])
const disinfestations = ref([])
const usageRecords = ref([])
const filterStatus = ref('')
const useDialogVisible = ref(false)
const currentToolId = ref(null)

const useForm = reactive({
  volunteerId: 1
})

const loadTools = async () => {
  const params = filterStatus.value ? { status: filterStatus.value } : {}
  const list = await getTools(params)
  tools.value = list.map(t => ({
    ...t,
    typeName: getTypeName(t.typeId)
  }))
}

const getTypeName = (typeId) => {
  const t = toolTypes.value.find(t => t.id === typeId)
  return t ? t.typeName : '-'
}

const loadToolTypes = async () => {
  toolTypes.value = await getToolTypes()
}

const loadDisinfestations = async () => {
  disinfestations.value = await getDisinfestations({})
}

const handleUse = (row) => {
  currentToolId.value = row.id
  useForm.volunteerId = 1
  useDialogVisible.value = true
}

const confirmUse = async () => {
  try {
    await useTool(currentToolId.value, useForm.volunteerId)
    ElMessage.success('领取成功')
    useDialogVisible.value = false
    loadTools()
  } catch (e) {
    console.error(e)
  }
}

const handleReturn = async (recordId) => {
  try {
    await returnTool(recordId)
    ElMessage.success('归还成功，已进入消毒流程')
    loadTools()
    loadDisinfestations()
  } catch (e) {
    console.error(e)
  }
}

const handleCompleteDisinfestation = async (id) => {
  try {
    await completeDisinfestation(id)
    ElMessage.success('消毒完成')
    loadTools()
    loadDisinfestations()
  } catch (e) {
    console.error(e)
  }
}

const getToolStatusName = (status) => {
  const map = { AVAILABLE: '可用', IN_USE: '使用中', DISINFECTING: '消毒中', MAINTENANCE: '维护中' }
  return map[status] || status
}

const getToolStatusType = (status) => {
  const map = { AVAILABLE: 'success', IN_USE: 'warning', DISINFECTING: 'primary', MAINTENANCE: 'info' }
  return map[status] || ''
}

onMounted(() => {
  loadToolTypes().then(() => loadTools())
  loadDisinfestations()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
