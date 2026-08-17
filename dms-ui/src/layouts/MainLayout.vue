<template>
  <el-container class="layout">
    <!-- 左侧菜单 -->
    <el-aside width="210px" class="sidebar">
      <div class="logo">
        <span class="logo-icon">档</span>
        <span class="logo-text">档案管理系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <!-- 目录：有 children 且类型为目录 -->
        <el-sub-menu v-for="menu in dirMenus" :key="menu.id" :index="String(menu.id)">
          <template #title>
            <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
            <span>{{ menu.menuTitle }}</span>
          </template>
          <!-- 菜单：目录下的子菜单 -->
          <el-menu-item
            v-for="child in menu.children"
            :key="child.id"
            :index="child.path"
          >
            <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
            <span>{{ child.menuTitle }}</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 右侧：顶栏 + 内容区 -->
    <el-container>
      <el-header class="header">
        <div class="breadcrumb">{{ currentTitle }}</div>
        <el-dropdown @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="30" class="avatar">{{ avatarText }}</el-avatar>
            <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const menuStore = useMenuStore()

// 目录菜单（menuType === 1）
const dirMenus = computed(() => menuStore.menus.filter((m) => m.menuType === 1))

// 当前页面标题（面包屑）
const currentTitle = computed(() => route.meta.title || '')

// 头像文字（用户名首字母大写）
const avatarText = computed(() => {
  const name = userStore.userInfo.realName || userStore.userInfo.username || 'A'
  return name.charAt(0).toUpperCase()
})

// 下拉命令
function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        await userStore.logout()
        router.push('/login')
      })
      .catch(() => {})
  }
}

// 兜底：直接登录进来时菜单可能还没拉，挂载时补拉
onMounted(async () => {
  if (menuStore.menus.length === 0) {
    try {
      const menus = await userStore.fetchMenus()
      menuStore.setMenus(menus)
    } catch (e) {
      // 拉取失败忽略，守卫已兜底
    }
  }
})
</script>

<style scoped>
.layout {
  height: 100%;
}

.sidebar {
  background-color: #304156;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  color: #fff;
  gap: 8px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
}

.logo-text {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar :deep(.el-menu) {
  border-right: none;
}

.header {
  height: 50px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
}

.breadcrumb {
  font-size: 13px;
  color: #909399;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.avatar {
  background-color: #409eff;
}

.username {
  font-size: 13px;
  color: #303133;
}

.main {
  background-color: #f5f7fa;
  padding: 16px;
}
</style>
