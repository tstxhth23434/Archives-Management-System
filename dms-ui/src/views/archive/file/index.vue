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
        <el-button v-if="userStore.hasPerm('archive:file:add')" type="success" @click="openImport">
          <el-icon><Upload /></el-icon> 批量导入
        </el-button>
        <el-button v-if="userStore.hasPerm('archive:file:add')" type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon> 新增文件
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="archiveNo" label="档号" min-width="180" />
        <el-table-column label="所属案卷" min-width="150">
          <template #default="{ row }">{{ volumeMap[row.volumeId] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="title" label="题名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="责任者" min-width="100" />
        <el-table-column prop="docDate" label="文件日期" width="110" />
        <el-table-column prop="year" label="年度" width="70" />
        <el-table-column prop="pages" label="页数" width="70" />
        <el-table-column label="保管期限" min-width="100">
          <template #default="{ row }">{{ retentionMap[row.retentionPeriod] || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="150">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusMap[row.status] || '-' }}</el-tag>
            <el-button
              v-if="row.status === 1 && userStore.hasPerm('archive:file:edit')"
              link type="success" size="small"
              @click="handleFlow(row, 2)"
            >归档</el-button>
            <el-button
              v-if="row.status === 2 && userStore.hasPerm('archive:file:edit')"
              link type="warning" size="small"
              @click="handleFlow(row, 3)"
            >封库</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('archive:file:query')" link type="success" @click="openElectronics(row)">原文</el-button>
            <el-button v-if="userStore.hasPerm('archive:file:query')" link type="info" @click="openLifecycle(row)">履历</el-button>
            <el-button v-if="row.status !== 3 && userStore.hasPerm('archive:file:edit')" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status !== 3 && userStore.hasPerm('archive:file:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 生命周期履历弹窗（D15 时间轴） -->
    <el-dialog v-model="lifecycleVisible" :title="`生命周期履历：${lifecycleFileTitle}`" width="560px" destroy-on-close>
      <el-timeline v-if="lifecycleList.length">
        <el-timeline-item
          v-for="item in lifecycleList"
          :key="item.id"
          :timestamp="item.createTime"
          :type="item.action === 'SEAL' ? 'danger' : 'success'"
        >
          <b>{{ item.actionName }}</b>
          <div style="color: #909399; font-size: 12px">{{ item.detail }}</div>
          <div style="color: #909399; font-size: 12px">操作人：{{ item.operatorName || '-' }}</div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无履历记录" />
    </el-dialog>

    <!-- Excel 批量导入弹窗（D13） -->
    <el-dialog v-model="importVisible" title="批量导入档案" width="560px" destroy-on-close>
      <el-alert type="info" :closable="false" style="margin-bottom: 12px"
        title="模板列：全宗代码、门类代码、题名、责任者、文件日期、年度、保管期限、密级、关键词、页数、摘要（题名/年度/全宗/门类必填，档号自动生成）" />
      <el-upload
        :show-file-list="false"
        :http-request="handleImport"
        accept=".xlsx,.xls"
        :disabled="importing"
      >
        <el-button type="primary" :loading="importing">选择 Excel 并导入</el-button>
      </el-upload>
      <div v-if="importResult" style="margin-top: 16px">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="总行数">{{ importResult.total }}</el-descriptions-item>
          <el-descriptions-item label="成功">
            <span style="color: #67c23a">{{ importResult.success }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="失败">
            <span style="color: #f56c6c">{{ importResult.fail }}</span>
          </el-descriptions-item>
        </el-descriptions>
        <el-table v-if="importResult.errors && importResult.errors.length" :data="importResult.errors" size="small" style="margin-top: 12px" max-height="240">
          <el-table-column prop="row" label="行号" width="70" />
          <el-table-column prop="message" label="错误原因" />
        </el-table>
      </div>
    </el-dialog>

    <!-- 电子原文预览弹窗（D13） -->
    <el-dialog v-model="previewVisible" :title="`预览：${previewName}`" width="720px" destroy-on-close>
      <div style="display: flex; justify-content: center; max-height: 70vh; overflow: auto">
        <img v-if="previewIsImage" :src="previewUrl" style="max-width: 100%" alt="预览" />
        <iframe v-else :src="previewUrl" style="width: 100%; height: 65vh; border: none" />
      </div>
    </el-dialog>

    <!-- 电子原文管理弹窗（D12） -->
    <el-dialog v-model="elecVisible" :title="`电子原文：${currentFile?.title}`" width="700px" destroy-on-close>
      <div class="search-bar" style="margin-bottom: 12px">
        <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          :disabled="!userStore.hasPerm('archive:file:edit')"
        >
          <el-button v-if="userStore.hasPerm('archive:file:edit')" type="primary" :loading="uploading">
            <el-icon><Upload /></el-icon> 上传原文
          </el-button>
        </el-upload>
        <el-tag style="margin-left: 8px" type="info">支持 pdf/doc/docx/xls/xlsx/ppt/pptx/txt/图片等,最大 50MB</el-tag>
      </div>
      <el-table :data="elecList" v-loading="elecLoading" size="small" empty-text="暂无电子原文">
        <el-table-column prop="fileName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column label="大小" width="90">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="fileSuffix" label="类型" width="70" />
        <el-table-column prop="uploadTime" label="上传时间" min-width="160" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handlePreview(row)">预览</el-button>
            <el-button link type="primary" size="small" @click="handleDownload(row)">下载</el-button>
            <el-button v-if="userStore.hasPerm('archive:file:delete')" link type="danger" size="small" @click="handleElecDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

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
import { Plus, Upload } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { pageFiles, addFile, updateFile, deleteFile, listFonds, listTypes, pageVolumes, uploadElectronic, listElectronics, downloadElectronic, deleteElectronic, previewElectronic, importExcelFile, changeFileStatus, getFileLifecycle } from '@/api/archive'
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

// 案卷 id → 题名 映射（所属案卷列展示）——用 ref 保证响应式
const volumeMap = ref({})

async function loadVolumeMap() {
  try {
    const map = {}
    let page = 1
    const size = 100
    // 循环翻页拉全量案卷，避免 >100 个案卷时映射缺失
    while (true) {
      const data = await pageVolumes({ pageNum: page, pageSize: size })
      data.records.forEach((v) => { map[v.id] = v.title })
      if (data.records.length < size || data.total <= page * size) break
      page++
    }
    volumeMap.value = map
  } catch (e) {
    // 案卷映射加载失败不影响列表
  }
}

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

// 电子原文管理（D12）
const elecVisible = ref(false)
const elecLoading = ref(false)
const uploading = ref(false)
const currentFile = ref(null)
const elecList = ref([])

async function openElectronics(row) {
  currentFile.value = row
  elecVisible.value = true
  elecLoading.value = true
  try {
    elecList.value = await listElectronics(row.id)
  } catch (e) {
    elecList.value = []
  } finally {
    elecLoading.value = false
  }
}

async function handleUpload(options) {
  uploading.value = true
  try {
    await uploadElectronic(currentFile.value.id, options.file)
    ElMessage.success('上传成功')
    elecList.value = await listElectronics(currentFile.value.id)
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  } finally {
    uploading.value = false
  }
}

function formatSize(bytes) {
  if (!bytes && bytes !== 0) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

async function handleDownload(row) {
  try {
    await downloadElectronic(row.id, row.fileName)
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

async function handleElecDelete(row) {
  await ElMessageBox.confirm(`确认删除原文「${row.fileName}」？`, '提示', { type: 'warning' })
  await deleteElectronic(row.id)
  ElMessage.success('删除成功')
  elecList.value = await listElectronics(currentFile.value.id)
}

// Excel 批量导入（D13）
const importVisible = ref(false)
const importing = ref(false)
const importResult = ref(null)

function openImport() {
  importResult.value = null
  importVisible.value = true
}

async function handleImport(options) {
  importing.value = true
  try {
    importResult.value = await importExcelFile(options.file)
    ElMessage.success(`导入完成：成功 ${importResult.value.success} 条，失败 ${importResult.value.fail} 条`)
    fetchData()
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  } finally {
    importing.value = false
  }
}

// 原文预览（D13）
const previewVisible = ref(false)
const previewName = ref('')
const previewUrl = ref('')
const previewIsImage = ref(false)
const IMAGE_SUFFIX = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']

async function handlePreview(row) {
  try {
    const blob = await previewElectronic(row.id)
    const url = URL.createObjectURL(blob)
    previewName.value = row.fileName
    const suffix = (row.fileSuffix || '').toLowerCase()
    previewIsImage.value = IMAGE_SUFFIX.includes(suffix)
    previewUrl.value = url
    previewVisible.value = true
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

// 状态流转 + 生命周期履历（D15）
const lifecycleVisible = ref(false)
const lifecycleFileTitle = ref('')
const lifecycleList = ref([])

async function handleFlow(row, toStatus) {
  const actionName = toStatus === 2 ? '归档' : '封库'
  await ElMessageBox.confirm(`确认将文件「${row.title}」${actionName}？${toStatus === 3 ? '（封库后档案只读，不可再编辑/删除）' : ''}`, '提示', { type: 'warning' })
  await changeFileStatus(row.id, toStatus)
  ElMessage.success(`${actionName}成功`)
  fetchData()
}

async function openLifecycle(row) {
  lifecycleFileTitle.value = row.title
  lifecycleVisible.value = true
  try {
    lifecycleList.value = await getFileLifecycle(row.id)
  } catch (e) {
    lifecycleList.value = []
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

onMounted(async () => {
  fetchFonds()
  fetchTypes()
  fetchDict()
  // 先加载案卷映射再查列表，避免首屏所属案卷列显示为 -
  await loadVolumeMap()
  fetchData()
})
</script>
