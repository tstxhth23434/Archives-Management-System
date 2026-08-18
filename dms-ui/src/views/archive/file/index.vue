<template>
  <div class="file-page">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-select v-model="query.fondsId" placeholder="全宗" clearable filterable style="width: 160px" @change="onFondsChange">
          <el-option v-for="f in fondsOptions" :key="f.id" :label="f.fondsName" :value="f.id" />
        </el-select>
        <el-select v-model="query.typeId" placeholder="门类" clearable filterable style="width: 150px" @change="onTypeChange">
          <el-option v-for="t in typeOptions" :key="t.id" :label="t.typeName" :value="t.id" />
        </el-select>
        <el-select v-model="query.volumeId" placeholder="案卷" clearable filterable style="width: 180px" @change="handleSearch">
          <el-option v-for="v in volumeOptions" :key="v.id" :label="v.title" :value="v.id" />
        </el-select>
        <el-input v-model="query.year" placeholder="年度" clearable style="width: 90px" @keyup.enter="handleSearch" />
        <el-input v-model="query.title" placeholder="题名" clearable style="width: 150px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div style="flex: 1"></div>
        <el-button v-if="userStore.hasPerm('archive:file:add')" type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon> 新增文件
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="archiveNo" label="档号" min-width="180" />
        <el-table-column prop="title" label="题名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="责任者" min-width="100" />
        <el-table-column prop="docDate" label="文件日期" width="110" />
        <el-table-column prop="year" label="年度" width="70" />
        <el-table-column prop="pages" label="页数" width="70" />
        <el-table-column label="保管期限" min-width="100">
          <template #default="{ row }">{{ retentionMap[row.retentionPeriod] || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusMap[row.status] || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('archive:file:edit')" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('archive:file:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文件' : '新增文件（档号自动生成）'" width="560px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所属全宗" prop="fondsId">
          <el-select v-model="form.fondsId" placeholder="请选择全宗" filterable :disabled="isEdit" style="width: 100%" @change="onFormFondsChange">
            <el-option v-for="f in fondsOptions" :key="f.id" :label="f.fondsName" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属门类" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择门类" filterable :disabled="isEdit" style="width: 100%" @change="onFormTypeChange">
            <el-option v-for="t in formTypeOptions" :key="t.id" :label="t.typeName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属案卷" prop="volumeId">
          <el-select v-model="form.volumeId" placeholder="可选（未组卷留空）" clearable filterable style="width: 100%">
            <el-option v-for="v in formVolumeOptions" :key="v.id" :label="v.title" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题名" prop="title">
          <el-input v-model="form.title" placeholder="档案题名" />
        </el-form-item>
        <el-form-item label="责任者" prop="author">
          <el-input v-model="form.author" placeholder="文件形成单位/个人" />
        </el-form-item>
        <el-form-item label="文件日期" prop="docDate">
          <el-date-picker v-model="form.docDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="年度" prop="year">
          <el-input-number v-model="form.year" :min="1900" :max="2100" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="保管期限" prop="retentionPeriod">
          <el-select v-model="form.retentionPeriod" placeholder="请选择" clearable style="width: 100%">
            <el-option v-for="r in retentionOptions" :key="r.itemCode" :label="r.itemName" :value="r.itemCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="密级" prop="securityLevel">
          <el-select v-model="form.securityLevel" placeholder="请选择" clearable style="width: 100%">
            <el-option v-for="s in securityOptions" :key="s.itemCode" :label="s.itemName" :value="s.itemCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词" prop="keywords">
          <el-input v-model="form.keywords" placeholder="逗号分隔，如 教学改革,通知" />
        </el-form-item>
        <el-form-item label="页数" prop="pages">
          <el-input-number v-model="form.pages" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="摘要/备注（可选）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="整理中" :value="1" />
            <el-option label="已归档" :value="2" />
            <el-option label="已封库" :value="3" />
          </el-select>
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
import { pageFiles, addFile, updateFile, deleteFile, listFonds, listTypes, pageVolumes } from '@/api/archive'
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
const typeOptions = ref([])
const volumeOptions = ref([])
const formTypeOptions = ref([])
const formVolumeOptions = ref([])
const retentionOptions = ref([])
const securityOptions = ref([])

const query = reactive({ fondsId: null, typeId: null, volumeId: null, year: '', title: '', pageNum: 1, pageSize: 10 })

const defaultForm = { id: null, fondsId: null, typeId: null, volumeId: null, title: '', author: '', docDate: null, year: null, retentionPeriod: null, securityLevel: null, keywords: '', pages: 0, summary: '', status: 1 }
const form = reactive({ ...defaultForm })

const rules = {
  fondsId: [{ required: true, message: '请选择全宗', trigger: 'change' }],
  typeId: [{ required: true, message: '请选择门类', trigger: 'change' }],
  title: [{ required: true, message: '请输入题名', trigger: 'blur' }],
  year: [{ required: true, message: '请输入年度', trigger: 'blur' }]
}

const retentionMap = {}
const securityMap = {}
const statusMap = { 1: '整理中', 2: '已归档', 3: '已封库' }

function statusTagType(status) {
  return { 1: 'warning', 2: 'success', 3: 'info' }[status] || 'info'
}

async function fetchFonds() {
  fondsOptions.value = await listFonds()
}

async function fetchTypes() {
  try {
    typeOptions.value = query.fondsId ? await listTypes(query.fondsId) : await listTypes()
  } catch (e) {
    typeOptions.value = []
  }
}

async function fetchVolumes() {
  if (!query.typeId) { volumeOptions.value = []; return }
  try {
    const data = await pageVolumes({ typeId: query.typeId, pageNum: 1, pageSize: 50 })
    volumeOptions.value = data.records
  } catch (e) {
    volumeOptions.value = []
  }
}

async function fetchDict() {
  try {
    const [ret, sec] = await Promise.all([
      request.get('/system/dict/items/retention_period'),
      request.get('/system/dict/items/secret_level')
    ])
    ret.forEach((r) => { retentionMap[r.itemCode] = r.itemName; retentionOptions.value.push(r) })
    sec.forEach((r) => { securityMap[r.itemCode] = r.itemName; securityOptions.value.push(r) })
  } catch (e) {
    // 字典拉取失败不影响列表
  }
}

function onFondsChange() {
  query.typeId = null
  query.volumeId = null
  volumeOptions.value = []
  fetchTypes()
  handleSearch()
}

function onTypeChange() {
  query.volumeId = null
  fetchVolumes()
  handleSearch()
}

async function onFormFondsChange(fondsId) {
  form.typeId = null
  form.volumeId = null
  formTypeOptions.value = []
  formVolumeOptions.value = []
  if (!fondsId) return
  try {
    formTypeOptions.value = await listTypes(fondsId)
  } catch (e) {
    formTypeOptions.value = []
  }
}

async function onFormTypeChange(typeId) {
  form.volumeId = null
  formVolumeOptions.value = []
  if (!typeId) return
  try {
    const data = await pageVolumes({ typeId, pageNum: 1, pageSize: 50 })
    formVolumeOptions.value = data.records
  } catch (e) {
    formVolumeOptions.value = []
  }
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: query.pageNum, pageSize: query.pageSize, title: query.title || undefined, year: query.year || undefined }
    if (query.fondsId) params.fondsId = query.fondsId
    if (query.typeId) params.typeId = query.typeId
    if (query.volumeId) params.volumeId = query.volumeId
    const data = await pageFiles(params)
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
  query.volumeId = null
  query.year = ''
  query.title = ''
  volumeOptions.value = []
  fetchTypes()
  handleSearch()
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, defaultForm)
  formTypeOptions.value = []
  formVolumeOptions.value = []
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, fondsId: row.fondsId, typeId: row.typeId, volumeId: row.volumeId, title: row.title, author: row.author, docDate: row.docDate, year: row.year, retentionPeriod: row.retentionPeriod, securityLevel: row.securityLevel, keywords: row.keywords, pages: row.pages, summary: row.summary, status: row.status })
  await onFormFondsChange(row.fondsId)
  if (row.typeId) await onFormTypeChange(row.typeId)
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
        await updateFile(form)
        ElMessage.success('修改成功')
      } else {
        await addFile(form)
        ElMessage.success('新增成功，档号已自动生成')
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
  await ElMessageBox.confirm(`确认删除文件「${row.title}」？`, '提示', { type: 'warning' })
  await deleteFile(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => {
  fetchFonds()
  fetchTypes()
  fetchDict()
  fetchData()
})
</script>
