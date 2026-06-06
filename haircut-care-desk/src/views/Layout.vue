<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon><Scissors /></el-icon>
        <span>理发管理系统</span>
      </div>
      <el-menu :default-active="activeMenu" router background-color="#1f2937" text-color="#d1d5db" active-text-color="#fff">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/appointment">
          <el-icon><Calendar /></el-icon>
          <span>预约登记</span>
        </el-menu-item>
        <el-menu-item index="/queue">
          <el-icon><List /></el-icon>
          <span>排队叫号</span>
        </el-menu-item>
        <el-menu-item index="/home-service">
          <el-icon><House /></el-icon>
          <span>上门安排</span>
        </el-menu-item>
        <el-menu-item index="/tools">
          <el-icon><Tools /></el-icon>
          <span>工具消毒</span>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><Document /></el-icon>
          <span>活动复盘</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="title">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userInfo?.realName || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userInfo = ref(null)

const menuTitles = {
  '/dashboard': '数据概览',
  '/appointment': '预约登记',
  '/queue': '排队叫号',
  '/home-service': '上门安排',
  '/tools': '工具消毒',
  '/report': '活动复盘'
}

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => menuTitles[route.path] || '社区公益理发管理系统')

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #1f2937;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  gap: 8px;
  border-bottom: 1px solid #374151;
}

.logo .el-icon {
  font-size: 24px;
}

.el-menu {
  border-right: none;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid #e5e7eb;
}

.header-left .title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #4b5563;
}

.main {
  background-color: #f3f4f6;
  padding: 20px;
  overflow-y: auto;
}
</style>
