<template>
  <div class="log-page">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="query.username" placeholder="操作人" clearable style="width: 150px" @keyup.enter="handleSearch" />
        <el-input v-model="query.operation" placeholder="操作描述" clearable style="width: 180px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div style="flex: 1"></div>
        <el-tag type="info">操作日志由 AOP 自动记录（@OpLog）</el-tag>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="操作人" min-width="100" />
        <el-table-column prop="operation" label="操作" min-width="120" />
        <el-table-column prop="method" label="方法" min-width="260" show-overflow-tooltip />
        <el-table-column prop="params" label="参数" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">{{ formatParams(row.params) }}</template>
        </el-table-column>
        <el-table-column prop="ip" label="IP" min-width="130" />
        <el-table-column prop="spendTime" label="耗时(ms)" width="90" />
        <el-table-column prop="createTime" label="时间" min-width="170" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageLogs } from '@/api/log'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive({ username: '', operation: '', pageNum: 1, pageSize: 10 })

function formatParams(params) {
  if (!params) return '-'
  const text = String(params)
  return text.length > 60 ? text.substring(0, 60) + '...' : text
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: query.pageNum, pageSize: query.pageSize, username: query.username || undefined, operation: query.operation || undefined }
    const data = await pageLogs(params)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.pageNum = 1
  fetchData()
}

function handleReset() {
  query.username = ''
  query.operation = ''
  handleSearch()
}

onMounted(fetchData)
</script>
