<template>
  <div class="app-container">
    <!-- 侧边导航栏 - 桌面端显示 -->
    <aside class="sidebar" v-if="!isMobileRoute">
      <div class="logo">
        <el-icon class="logo-icon"><Lightning /></el-icon>
        <span class="logo-text">电池安全管理平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#fff"
      >
        <el-menu-item index="/">
          <el-icon><DataLine /></el-icon>
          <span>实时监控大屏</span>
        </el-menu-item>
        <el-menu-item index="/alert">
          <el-icon><Warning /></el-icon>
          <span>预警中心</span>
        </el-menu-item>
        <el-menu-item index="/maintenance">
          <el-icon><Tools /></el-icon>
          <span>维保管理</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content" :class="{ 'mobile': isMobileRoute }">
      <!-- 顶部导航栏 - 桌面端显示 -->
      <header class="header" v-if="!isMobileRoute">
        <div class="header-left">
          <el-breadcrumb>
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="message-badge">
            <el-icon class="header-icon" @click="handleMessage"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <div class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataLine, Warning, Tools, Bell, UserFilled, ArrowDown, Lightning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 判断是否为移动端路由
const isMobileRoute = computed(() => {
  return route.path.startsWith('/mobile')
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 页面标题
const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/': '实时监控大屏',
    '/alert': '预警中心',
    '/maintenance': '维保管理'
  }
  return titles[route.path] || ''
})

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心功能开发中...')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'logout':
      ElMessage.success('退出登录成功')
      break
  }
}

// 处理消息点击
const handleMessage = () => {
  router.push('/alert')
}
</script>

<style scoped>
.app-container {
  display: flex;
  min-height: 100vh;
}

/* 侧边栏 */
.sidebar {
  width: 256px;
  background: #001529;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  transition: all 0.3s;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: #002140;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  font-size: 28px;
  color: #1890ff;
  margin-right: 12px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
}

/* 主内容区 */
.main-content {
  flex: 1;
  margin-left: 256px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f0f2f5;
}

.main-content.mobile {
  margin-left: 0;
}

/* 顶部导航栏 */
.header {
  height: 64px;
  background: #fff;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 99;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-icon {
  font-size: 20px;
  color: #595959;
  cursor: pointer;
  transition: color 0.2s;
}

.header-icon:hover {
  color: #1890ff;
}

.message-badge :deep(.el-badge__content) {
  top: 8px;
  right: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #262626;
}

/* 页面内容 */
.page-content {
  flex: 1;
  overflow: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
