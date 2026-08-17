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

    <div class="stat-grid">
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">系统用户</div>
        <div class="stat-num">1</div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">档案门类</div>
        <div class="stat-num">0</div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">案卷总数</div>
        <div class="stat-num">0</div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">档案文件</div>
        <div class="stat-num">0</div>
      </el-card>
    </div>

    <el-card shadow="never" style="margin-top: 16px;">
      <template #header>开发进度说明</template>
      <p style="font-size: 13px; color: #606266; line-height: 1.8; margin: 0;">
        当前已完成系统管理域后端（D3-D6），本阶段（D7）落地前端框架与用户管理页面。
        后续 D8+ 将逐步实现档案核心业务（全宗/门类/案卷/文件）与首页数据统计图表。
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.stat-num {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-top: 8px;
}
</style>
