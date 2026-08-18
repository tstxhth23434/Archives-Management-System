<template>
  <div class="type-page">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-select v-model="query.fondsId" placeholder="所属全宗" clearable filterable style="width: 200px" @change="handleSearch">
          <el-option v-for="f in fondsOptions" :key="f.id" :label="f.fondsName" :value="f.id" />
        </el-select>
        <el-input
          v-model="query.typeName"
          placeholder="门类名称"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div style="flex: 1"></div>
        <el-button v-if="userStore.hasPerm('archive:type:add')" type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon> 新增门类
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
        <el-table-column label="所属全宗" min-width="120">
          <template #default="{ row }">{{ fondsMap[row.fondsId] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="typeCode" label="门类代码" min-width="100" />
        <el-table-column prop="typeName" label="门类名称" min-width="130" />
        <el-table-column label="默认保管期限" min-width="110">
          <template #default="{ row }">{{ retentionMap[row.retentionPeriod] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('archive:type:edit')" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('archive:type:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑门类' : '新增门类'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属全宗" prop="fondsId">
          <el-select v-model="form.fondsId" placeholder="请选择全宗" filterable style="width: 100%">
            <el-option v-for="f in fondsOptions" :key="f.id" :label="f.fondsName" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="门类代码" prop="typeCode">
          <el-input v-model="form.typeCode" placeholder="如 WS" />
        </el-form-item>
        <el-form-item label="门类名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="如 文书档案" />
        </el-form-item>
        <el-form-item label="默认保管期限" prop="retentionPeriod">
          <el-select v-model="form.retentionPeriod" placeholder="请选择" clearable style="width: 100%">
            <el-option v-for="r in retentionOptions" :key="r.itemCode" :label="r.itemName" :value="r.itemCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { pageTypes, addType, updateType, deleteType, listFonds } from '@/api/archive'
import request from '@/api/request'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const fondsOptions = ref([])
const retentionOptions = ref([])

const query = reactive({ fondsId: null, typeName: '', pageNum: 1, pageSize: 10 })

const defaultForm = { id: null, fondsId: null, typeCode: '', typeName: '', retentionPeriod: null, sort: 0, status: 1 }
const form = reactive({ ...defaultForm })

const rules = {
  fondsId: [{ required: true, message: '请选择所属全宗', trigger: 'change' }],
  typeCode: [{ required: true, message: '请输入门类代码', trigger: 'blur' }],
  typeName: [{ required: true, message: '请输入门类名称', trigger: 'blur' }]
}

const fondsMap = {}
const retentionMap = {}

async function fetchFonds() {
  try {
    const list = await listFonds()
    fondsOptions.value = list
    Object.keys(fondsMap).forEach((k) => delete fondsMap[k])
    list.forEach((f) => { fondsMap[f.id] = f.fondsName })
  } catch (e) {
    // 无 archive:fonds:query 权限时全宗下拉为空，不影响列表加载（错误提示已由拦截器弹出）
    fondsOptions.value = []
  }
}

async function fetchRetention() {
  try {
    const list = await request.get('/system/dict/items/retention_period')
    retentionOptions.value = list
    Object.keys(retentionMap).forEach((k) => delete retentionMap[k])
    list.forEach((r) => { retentionMap[r.itemCode] = r.itemName })
  } catch (e) {
    retentionOptions.value = []
  }
}

async function fetchData() {
  loading.value = true
  try {
    const data = await pageTypes(query)
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
  query.typeName = ''
  handleSearch()
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, defaultForm)
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, fondsId: row.fondsId, typeCode: row.typeCode, typeName: row.typeName, retentionPeriod: row.retentionPeriod, sort: row.sort, status: row.status })
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateType(form)
        ElMessage.success('修改成功')
      } else {
        await addType(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (e) {
      // 错误提示已由拦截器统一弹出
    } finally {
      saving.value = false
    }
  })
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除门类「${row.typeName}」？`, '提示', { type: 'warning' })
  await deleteType(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => {
  fetchFonds()
  fetchRetention()
  fetchData()
})
</script>
