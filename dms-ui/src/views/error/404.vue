<template>
  <div class="not-found">
    <div class="code">404</div>
    <p>页面不存在或已被移除</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.code {
  font-size: 72px;
  font-weight: 600;
  color: #409eff;
}

.not-found p {
  color: #909399;
  font-size: 14px;
}
</style>
