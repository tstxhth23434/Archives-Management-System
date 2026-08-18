<template>
  <div class="dict-page">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="query.dictName" placeholder="字典名称" clearable style="width: 180px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div style="flex: 1"></div>
        <el-button v-if="userStore.hasPerm('system:dict:add')" type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon> 新增字典
        </el-button>
      </div>

      <!-- 字典类型表格 -->
      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="dictName" label="字典名称" min-width="150" />
        <el-table-column prop="dictCode" label="字典编码" min-width="180" />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('system:dict:query')" link type="success" @click="openItems(row)">字典项</el-button>
            <el-button v-if="userStore.hasPerm('system:dict:edit')" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('system:dict:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 字典类型新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字典' : '新增字典'" width="460px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="如 保管期限" />
        </el-form-item>
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="form.dictCode" placeholder="如 retention_period（唯一）" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="描述（可选）" />
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

    <!-- 字典项管理弹窗 -->
    <el-dialog v-model="itemsVisible" :title="`字典项：${currentDictName}`" width="680px" destroy-on-close>
      <div class="search-bar" style="margin-bottom: 12px">
        <el-button v-if="userStore.hasPerm('system:dict:add')" type="primary" size="small" @click="openItemAdd">
          <el-icon><Plus /></el-icon> 新增字典项
        </el-button>
      </div>
      <el-table :data="itemList" v-loading="itemsLoading" size="small">
        <el-table-column prop="itemCode" label="编码" min-width="120" />
        <el-table-column prop="itemName" label="名称" min-width="120" />
        <el-table-column prop="itemValue" label="值" min-width="100" />
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('system:dict:edit')" link type="primary" size="small" @click="openItemEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('system:dict:delete')" link type="danger" size="small" @click="handleItemDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 字典项新增/编辑弹窗（内嵌） -->
      <el-dialog v-model="itemDialogVisible" :title="itemIsEdit ? '编辑字典项' : '新增字典项'" width="420px" append-to-body destroy-on-close>
        <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="80px">
          <el-form-item label="编码" prop="itemCode">
            <el-input v-model="itemForm.itemCode" placeholder="如 permanent" />
          </el-form-item>
          <el-form-item label="名称" prop="itemName">
            <el-input v-model="itemForm.itemName" placeholder="如 永久" />
          </el-form-item>
          <el-form-item label="值" prop="itemValue">
            <el-input v-model="itemForm.itemValue" placeholder="如 permanent" />
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="itemForm.sort" :min="0" :max="999" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-switch v-model="itemForm.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="itemDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="itemSaving" @click="handleItemSubmit">确定</el-button>
        </template>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { pageDicts, addDict, updateDict, deleteDict, listDictItems, addDictItem, updateDictItem, deleteDictItem } from '@/api/dict'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const itemsLoading = ref(false)
const itemSaving = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const itemsVisible = ref(false)
const itemDialogVisible = ref(false)
const isEdit = ref(false)
const itemIsEdit = ref(false)
const formRef = ref(null)
const itemFormRef = ref(null)
const itemList = ref([])
const currentDictCode = ref('')
const currentDictName = ref('')

const query = reactive({ dictName: '', pageNum: 1, pageSize: 10 })

const defaultForm = { id: null, dictName: '', dictCode: '', description: '', status: 1 }
const form = reactive({ ...defaultForm })

const defaultItemForm = { id: null, itemCode: '', itemName: '', itemValue: '', sort: 0, status: 1 }
const itemForm = reactive({ ...defaultItemForm })

const rules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictCode: [
    { required: true, message: '请输入字典编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '字母开头，仅字母数字下划线', trigger: 'blur' }
  ]
}

const itemRules = {
  itemCode: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  itemName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入值', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const data = await pageDicts({ pageNum: query.pageNum, pageSize: query.pageSize, dictName: query.dictName || undefined })
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
  query.dictName = ''
  handleSearch()
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, defaultForm)
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, dictName: row.dictName, dictCode: row.dictCode, description: row.description, status: row.status })
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
        await updateDict(form)
        ElMessage.success('修改成功')
      } else {
        await addDict(form)
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
  await ElMessageBox.confirm(`确认删除字典「${row.dictName}」？`, '提示', { type: 'warning' })
  await deleteDict(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

async function openItems(row) {
  currentDictCode.value = row.dictCode
  currentDictName.value = row.dictName
  itemsVisible.value = true
  await fetchItems()
}

async function fetchItems() {
  itemsLoading.value = true
  try {
    itemList.value = await listDictItems(currentDictCode.value)
  } catch (e) {
    itemList.value = []
  } finally {
    itemsLoading.value = false
  }
}

function openItemAdd() {
  itemIsEdit.value = false
  Object.assign(itemForm, defaultItemForm)
  itemDialogVisible.value = true
}

function openItemEdit(row) {
  itemIsEdit.value = true
  Object.assign(itemForm, { id: row.id, itemCode: row.itemCode, itemName: row.itemName, itemValue: row.itemValue, sort: row.sort, status: row.status })
  itemDialogVisible.value = true
}

async function handleItemSubmit() {
  await itemFormRef.value.validate(async (valid) => {
    if (!valid) return
    itemSaving.value = true
    try {
      if (itemIsEdit.value) {
        await updateDictItem(itemForm)
        ElMessage.success('修改成功')
      } else {
        await addDictItem({ ...itemForm, dictId: itemList.value.length ? itemList.value[0].dictId : undefined })
        ElMessage.success('新增成功')
      }
      itemDialogVisible.value = false
      fetchItems()
    } catch (e) {
      // 错误提示已由拦截器统一弹出
    } finally {
      itemSaving.value = false
    }
  })
}

async function handleItemDelete(row) {
  await ElMessageBox.confirm(`确认删除字典项「${row.itemName}」？`, '提示', { type: 'warning' })
  await deleteDictItem(row.id)
  ElMessage.success('删除成功')
  fetchItems()
}

onMounted(fetchData)
</script>
