<template>
  <div class="borrow-page">
    <el-card shadow="never">
      <div class="search-bar">
        <el-tag type="info">借阅流程：申请 → 管理员审批 → 通过后可在线浏览原文 → 归还</el-tag>
        <div style="flex: 1"></div>
        <el-button @click="fetchData">刷新</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px" empty-text="暂无借阅记录">
        <el-table-column prop="borrowNo" label="借阅单号" min-width="170" />
        <el-table-column prop="archiveNo" label="档号" min-width="170" />
        <el-table-column prop="archiveTitle" label="档案题名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reason" label="借阅理由" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusMap[row.status] || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveComment" label="审批意见" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ row.approveComment || '-' }}</template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" min-width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyBorrows } from '@/api/archive'

const loading = ref(false)
const tableData = ref([])

const statusMap = { 1: '待审批', 2: '已通过', 3: '已驳回', 4: '已归还' }
function statusTagType(status) {
  return { 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }[status] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    tableData.value = await getMyBorrows()
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
