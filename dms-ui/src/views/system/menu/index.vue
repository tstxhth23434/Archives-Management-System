<template>
  <div class="menu-page">
    <el-card shadow="never">
      <div class="search-bar">
        <el-button v-if="userStore.hasPerm('system:menu:add')" type="primary" @click="openAdd(null)">
          <el-icon><Plus /></el-icon> 新增菜单
        </el-button>
        <div style="flex: 1"></div>
        <el-button @click="fetchData">刷新</el-button>
      </div>

      <!-- 树形表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children' }"
        default-expand-all
        style="margin-top: 16px"
      >
        <el-table-column label="菜单名称" min-width="220">
          <template #default="{ row }">
            <el-icon style="margin-right: 4px"><component :is="row.icon || 'Menu'" /></el-icon>
            {{ row.menuTitle }}
          </template>
        </el-table-column>
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.menuType)" size="small">{{ typeMap[row.menuType] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="150" show-overflow-tooltip />
        <el-table-column prop="component" label="组件" min-width="180" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限码" min-width="180" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('system:menu:add') && row.menuType !== 3" link type="primary" @click="openAdd(row)">新增子级</el-button>
            <el-button v-if="userStore.hasPerm('system:menu:edit')" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('system:menu:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="父级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'menuTitle', children: 'children' }"
            node-key="id"
            check-strictly
            :render-after-expand="false"
            placeholder="留空为顶级目录"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="菜单名称（如 用户管理）" />
        </el-form-item>
        <el-form-item label="显示标题" prop="menuTitle">
          <el-input v-model="form.menuTitle" placeholder="侧边栏显示标题（如 用户管理）" />
        </el-form-item>
        <el-form-item label="类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="form.menuType !== 3">
          <el-form-item label="图标" prop="icon">
            <el-input v-model="form.icon" placeholder="Element Plus 图标名，如 User（可选）" />
          </el-form-item>
          <el-form-item label="路由路径" prop="path">
            <el-input v-model="form.path" placeholder="如 /system/user（可选）" />
          </el-form-item>
          <el-form-item label="组件路径" prop="component">
            <el-input v-model="form.component" placeholder="如 system/user/index（可选）" />
          </el-form-item>
        </template>
        <el-form-item v-else label="权限码" prop="perms">
          <el-input v-model="form.perms" placeholder="如 system:user:add（按钮必填）" />
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
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '@/api/menu'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const parentOptions = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const typeMap = { 1: '目录', 2: '菜单', 3: '按钮' }
function typeTagType(t) {
  return { 1: 'warning', 2: 'success', 3: 'info' }[t] || 'info'
}

const defaultForm = { id: null, parentId: null, menuName: '', menuTitle: '', menuType: 1, icon: '', path: '', component: '', perms: '', sort: 0, status: 1 }
const form = reactive({ ...defaultForm })

const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuTitle: [{ required: true, message: '请输入显示标题', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

async function fetchData() {
  loading.value = true
  try {
    tableData.value = await getMenuTree()
  } finally {
    loading.value = false
  }
}

function openAdd(parentRow) {
  isEdit.value = false
  Object.assign(form, defaultForm)
  if (parentRow) {
    form.parentId = parentRow.id
    form.menuType = parentRow.menuType === 1 ? 2 : 3
  }
  parentOptions.value = tableData.value
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, parentId: row.parentId, menuName: row.menuName, menuTitle: row.menuTitle,
    menuType: row.menuType, icon: row.icon, path: row.path, component: row.component,
    perms: row.perms, sort: row.sort, status: row.status
  })
  // 父级下拉过滤自身+全部后代,避免把菜单挂到子孙下形成环(buildTree 递归会丢失整支)
  parentOptions.value = filterOutSelfAndDescendants(tableData.value, row.id)
  dialogVisible.value = true
}

/**
 * 从树中剔除指定节点及其全部后代
 */
function filterOutSelfAndDescendants(nodes, excludeId) {
  const result = []
  nodes.forEach((n) => {
    if (n.id === excludeId) return
    const children = n.children ? filterOutSelfAndDescendants(n.children, excludeId) : []
    result.push({ ...n, children })
  })
  return result
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
        await updateMenu(form)
        ElMessage.success('修改成功')
      } else {
        await addMenu(form)
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
  await ElMessageBox.confirm(`确认删除菜单「${row.menuTitle}」？`, '提示', { type: 'warning' })
  await deleteMenu(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>
