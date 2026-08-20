<template>
  <div class="dashboard">
    <el-card shadow="never">
      <h2 style="margin: 0 0 8px; font-size: 18px;">
        欢迎使用档案管理系统
      </h2>
      <p style="color: #909399; margin: 0; font-size: 13px;">
        当前登录：{{ userStore.userInfo.realName || userStore.userInfo.username }}，这里是系统首页驾驶舱。
      </p>
    </el-card>

    <!-- 统计卡片（真实数据，点击跳转对应管理页） -->
    <div class="stat-grid">
      <el-card
        v-for="s in statCards"
        :key="s.label"
        shadow="hover"
        class="stat-card"
        :class="{ clickable: s.canClick }"
        @click="handleCardClick(s)"
      >
        <div class="stat-label">
          <el-icon style="vertical-align: -2px; margin-right: 4px"><component :is="s.icon" /></el-icon>
          {{ s.label }}
        </div>
        <div class="stat-num">{{ stats[s.key] ?? 0 }}</div>
      </el-card>
    </div>

    <!-- 快捷入口 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header>快捷入口</template>
      <div class="quick-links">
        <el-button v-if="userStore.hasPerm('archive:volume:query')" @click="$router.push('/archive/overview')">档案浏览</el-button>
        <el-button v-if="userStore.hasPerm('archive:volume:add')" @click="$router.push('/archive/volume')">案卷管理</el-button>
        <el-button v-if="userStore.hasPerm('archive:file:add')" @click="$router.push('/archive/file')">档案管理</el-button>
        <el-button v-if="userStore.hasPerm('system:user:query')" @click="$router.push('/system/user')">用户管理</el-button>
        <el-button v-if="userStore.hasPerm('system:role:query')" @click="$router.push('/system/role')">角色管理</el-button>
      </div>
    </el-card>

    <!-- 开发进度 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header>开发进度</template>
      <el-timeline style="max-width: 640px">
        <el-timeline-item v-for="p in progress" :key="p.d" :type="p.done ? 'success' : 'primary'" :timestamp="p.d">
          <b>{{ p.name }}</b>
          <el-tag v-if="p.done" type="success" size="small" style="margin-left: 6px">已完成</el-tag>
          <el-tag v-else type="primary" size="small" style="margin-left: 6px">进行中</el-tag>
          <div style="color: #909399; font-size: 12px">{{ p.desc }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Files, Folder, Document, Collection, Tickets } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

const stats = reactive({ userCount: 0, fondsCount: 0, typeCount: 0, volumeCount: 0, fileCount: 0, electronicCount: 0 })

// 统计卡片（canClick 按当前角色权限动态）
const statCards = computed(() => [
  { key: 'userCount', label: '系统用户', icon: User, path: '/system/user', canClick: userStore.hasPerm('system:user:query') },
  { key: 'typeCount', label: '档案门类', icon: Collection, path: '/archive/type', canClick: userStore.hasPerm('archive:type:query') },
  { key: 'volumeCount', label: '案卷总数', icon: Folder, path: '/archive/volume', canClick: userStore.hasPerm('archive:volume:query') },
  { key: 'fileCount', label: '档案文件', icon: Document, path: '/archive/file', canClick: userStore.hasPerm('archive:file:query') },
  { key: 'electronicCount', label: '电子原文', icon: Tickets, path: '/archive/file', canClick: userStore.hasPerm('archive:file:query') },
  { key: 'fondsCount', label: '档案全宗', icon: Files, path: '/archive/fonds', canClick: userStore.hasPerm('archive:fonds:query') }
])

// 开发进度（当前真实进度，随开发更新）
const progress = [
  { d: 'D1-D7', name: '地基 + 系统管理', desc: '数据库设计、登录、用户/角色/菜单/字典、操作日志、前端起步', done: true },
  { d: 'D8-D10', name: '档案核心：全宗/门类/案卷/文件 + 档号生成', desc: '全宗、门类管理；档案树；案卷 CRUD、档号自动生成、文件著录', done: true },
  { d: 'D11-D13', name: '从属展示 + 原文 + 批量导入', desc: '案卷-文件从属、电子原文上传/预览、Excel 批量导入', done: true },
  { d: 'D14-D15', name: '缓冲联调 + 状态流转', desc: '端到端联调零 bug；整理中→已归档→已封库 + 生命周期履历', done: true },
  { d: 'D16+', name: '检索利用与借阅流转', desc: '多条件检索页、借阅申请/审批、归还；首页统计图表', done: false }
]

function handleCardClick(card) {
  if (card.canClick) {
    router.push(card.path)
  }
}

async function fetchStats() {
  try {
    Object.assign(stats, await request.get('/dashboard/stats'))
  } catch (e) {
    // 统计拉取失败不影响页面
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 16px;
}
.stat-card {
  cursor: default;
  transition: transform 0.2s;
}
.stat-card.clickable {
  cursor: pointer;
}
.stat-card.clickable:hover {
  transform: translateY(-2px);
}
.stat-label {
  font-size: 13px;
  color: #909399;
}
.stat-num {
  font-size: 26px;
  font-weight: 600;
  color: #303133;
  margin-top: 8px;
}
.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
