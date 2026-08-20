<template>
  <div class="search-page">
    <el-card shadow="never">
      <!-- 检索条件 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="题名">
          <el-input v-model="query.title" placeholder="题名模糊" clearable style="width: 150px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="档号">
          <el-input v-model="query.archiveNo" placeholder="如 JSXY-WS-2025" clearable style="width: 170px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keywords" placeholder="关键词模糊" clearable style="width: 140px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="门类">
          <el-select v-model="query.typeId" placeholder="全部" clearable style="width: 140px" @change="handleSearch">
            <el-option v-for="t in typeOptions" :key="t.id" :label="t.typeName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="年度">
          <el-input v-model="query.year" placeholder="年度" clearable style="width: 90px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="保管期限">
          <el-select v-model="query.retentionPeriod" placeholder="全部" clearable style="width: 110px" @change="handleSearch">
            <el-option v-for="r in retentionOptions" :key="r.itemCode" :label="r.itemName" :value="r.itemCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="密级">
          <el-select v-model="query.securityLevel" placeholder="全部" clearable style="width: 110px" @change="handleSearch">
            <el-option v-for="s in securityOptions" :key="s.itemCode" :label="s.itemName" :value="s.itemCode" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 结果表格 -->
      <el-table :data="tableData" v-loading="loading" style="margin-top: 8px" empty-text="没有符合条件的档案">
        <el-table-column prop="archiveNo" label="档号" min-width="180" />
        <el-table-column prop="title" label="题名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="责任者" min-width="100" />
        <el-table-column prop="year" label="年度" width="70" />
        <el-table-column label="保管期限" min-width="100">
          <template #default="{ row }">{{ retentionMap[row.retentionPeriod] || '-' }}</template>
        </el-table-column>
        <el-table-column label="密级" min-width="90">
          <template #default="{ row }">{{ securityMap[row.securityLevel] || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusMap[row.status] || '-' }}</el-tag>
          </template>
        </el-table-column>
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
import { listTypes } from '@/api/archive'
import request from '@/api/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const typeOptions = ref([])
const retentionOptions = ref([])
const securityOptions = ref([])

const query = reactive({
  title: '', archiveNo: '', keywords: '', typeId: null, year: '',
  retentionPeriod: null, securityLevel: null, pageNum: 1, pageSize: 10
})

const retentionMap = {}
const securityMap = {}
const statusMap = { 1: '整理中', 2: '已归档', 3: '已封库' }

function statusTagType(status) {
  return { 1: 'warning', 2: 'success', 3: 'info' }[status] || 'info'
}

async function fetchTypes() {
  try {
    typeOptions.value = await listTypes()
  } catch (e) {
    typeOptions.value = []
  }
}

async function fetchDicts() {
  try {
    const [ret, sec] = await Promise.all([
      request.get('/system/dict/items/retention_period'),
      request.get('/system/dict/items/secret_level')
    ])
    ret.forEach((r) => { retentionMap[r.itemCode] = r.itemName; retentionOptions.value.push(r) })
    sec.forEach((r) => { securityMap[r.itemCode] = r.itemName; securityOptions.value.push(r) })
  } catch (e) {
    // 字典拉取失败不影响检索
  }
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum, pageSize: query.pageSize,
      title: query.title || undefined, archiveNo: query.archiveNo || undefined,
      keywords: query.keywords || undefined, year: query.year || undefined,
      retentionPeriod: query.retentionPeriod || undefined, securityLevel: query.securityLevel || undefined,
      typeId: query.typeId || undefined
    }
    const data = await request.get('/archive/search', { params })
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
  query.title = ''
  query.archiveNo = ''
  query.keywords = ''
  query.typeId = null
  query.year = ''
  query.retentionPeriod = null
  query.securityLevel = null
  handleSearch()
}

onMounted(() => {
  fetchTypes()
  fetchDicts()
  fetchData()
})
</script>

<style scoped>
.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
}
</style>
