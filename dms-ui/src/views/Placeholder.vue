<template>
  <div class="placeholder">
    <el-icon :size="48" color="#c0c4cc"><Tools /></el-icon>
    <p>「{{ title }}」功能开发中，敬请期待</p>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const title = route.meta.title || '该'
</script>

<style scoped>
.placeholder {
  height: 100%;
  min-height: 360px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: #909399;
  background: #fff;
  border-radius: 10px;
}
</style>
