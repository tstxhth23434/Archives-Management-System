<template>
  <div class="overview-page">
    <el-card shadow="never" class="tree-card">
      <div class="tree-title">
        <el-icon><FolderOpened /></el-icon>
        <span>档案目录</span>
      </div>
      <el-tree
        ref="treeRef"
        :data="treeData"
        node-key="nodeKey"
        :props="{ label: 'label', children: 'children' }"
        highlight-current
        default-expand-all
        @node-click="handleNodeClick"
      >
        <template #default="{ data }">
          <span class="tree-node">
            <el-icon v-if="data.type === 'fonds'"><OfficeBuilding /></el-icon>
            <el-icon v-else-if="data.type === 'type'"><Files /></el-icon>
            <el-icon v-else><Calendar /></el-icon>
            <span>{{ data.label }}</span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <el-card shadow="never" class="list-card">
      <div class="search-bar">
        <el-input v-model="query.title" placeholder="案卷题名" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div style="flex: 1"></div>
        <el-tag v-if="currentPath" type="info">{{ currentPath }}</el-tag>
      </div>

      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="volumeNo" label="案卷号" min-width="170" />
        <el-table-column prop="title" label="案卷题名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="year" label="年度" width="80" />
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
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
      </el-table>

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
import { FolderOpened, OfficeBuilding, Files, Calendar } from '@element-plus/icons-vue'
import { getArchiveTree, pageVolumes } from '@/api/archive'
import request from '@/api/request'

const loading = ref(false)
const treeData = ref([])
const tableData = ref([])
const total = ref(0)
const currentPath = ref('')

const query = reactive({ fondsId: null, typeId: null, year: null, title: '', pageNum: 1, pageSize: 10 })

const retentionMap = {}
const securityMap = {}
const statusMap = { 1: '整理中', 2: '已归档', 3: '已封库' }

function statusTagType(status) {
  return { 1: 'warning', 2: 'success', 3: 'info' }[status] || 'info'
}

async function fetchDict() {
  try {
    const [ret, sec] = await Promise.all([
      request.get('/system/dict/items/retention_period'),
      request.get('/system/dict/items/secret_level')
    ])
    ret.forEach((r) => { retentionMap[r.itemCode] = r.itemName })
    sec.forEach((r) => { securityMap[r.itemCode] = r.itemName })
  } catch (e) {
    // 字典拉取失败不影响列表
  }
}

async function fetchTree() {
  try {
    treeData.value = await getArchiveTree()
  } catch (e) {
    treeData.value = []
  }
}

function handleNodeClick(data, node) {
  // 按节点层级设置过滤条件：fonds→fondsId、type→fondsId+typeId、year→fondsId+typeId+year
  // 通过 el-tree 的 node.parent 链取祖先 id，避免"点某门类下某年度返回所有门类该年度数据"
  query.fondsId = null
  query.typeId = null
  query.year = null
  if (data.type === 'fonds') {
    query.fondsId = data.id
    currentPath.value = data.label
  } else if (data.type === 'type') {
    query.typeId = data.id
    query.fondsId = node.parent?.data?.id ?? null
    currentPath.value = data.label
  } else if (data.type === 'year') {
    query.year = data.id
    query.typeId = node.parent?.data?.id ?? null
    query.fondsId = node.parent?.parent?.data?.id ?? null
    currentPath.value = data.label
  }
  handleSearch()
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: query.pageNum, pageSize: query.pageSize, title: query.title || undefined }
    if (query.fondsId) params.fondsId = query.fondsId
    if (query.typeId) params.typeId = query.typeId
    if (query.year) params.year = query.year
    const data = await pageVolumes(params)
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
  query.fondsId = null
  query.typeId = null
  query.year = null
  query.title = ''
  currentPath.value = ''
  handleSearch()
}

onMounted(() => {
  fetchDict()
  fetchTree()
  fetchData()
})
</script>

<style scoped>
.overview-page {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.tree-card {
  width: 300px;
  flex-shrink: 0;
}
.list-card {
  flex: 1;
}
.tree-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: bold;
  margin-bottom: 8px;
}
.tree-node {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
