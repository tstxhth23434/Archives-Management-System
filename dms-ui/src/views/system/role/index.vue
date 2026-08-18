<template>
  <div class="role-page">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="query.roleName" placeholder="角色名称" clearable style="width: 180px" @keyup.enter="handleSearch" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div style="flex: 1"></div>
        <el-button v-if="userStore.hasPerm('system:role:add')" type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon> 新增角色
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="roleName" label="角色名称" min-width="130" />
        <el-table-column prop="roleCode" label="角色编码" min-width="130" />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button v-if="userStore.hasPerm('system:role:edit')" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="userStore.hasPerm('system:role:edit') && userStore.hasPerm('system:menu:query')" link type="success" @click="openAssign(row)">分配菜单</el-button>
            <el-button v-if="userStore.hasPerm('system:role:delete')" link type="danger" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="如 档案管理员" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="如 archive_admin（唯一，字母数字下划线）" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="角色描述（可选）" />
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

    <!-- 分配菜单弹窗 -->
    <el-dialog v-model="assignVisible" :title="`分配菜单：${assignRoleName}`" width="420px" destroy-on-close>
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        node-key="id"
        :props="{ label: 'menuTitle', children: 'children' }"
        show-checkbox
        default-expand-all
      />
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignSaving" @click="handleAssign">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { pageRoles, addRole, updateRole, deleteRole, getRoleMenus, assignRoleMenus } from '@/api/role'
import request from '@/api/request'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const assignSaving = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const assignVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const menuTreeRef = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const assignRoleId = ref(null)
const assignRoleName = ref('')

const query = reactive({ roleName: '', status: null, pageNum: 1, pageSize: 10 })

const defaultForm = { id: null, roleName: '', roleCode: '', description: '', sort: 0, status: 1 }
const form = reactive({ ...defaultForm })

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '字母开头，仅字母数字下划线', trigger: 'blur' }
  ]
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: query.pageNum, pageSize: query.pageSize, roleName: query.roleName || undefined, status: query.status ?? undefined }
    const data = await pageRoles(params)
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
  query.roleName = ''
  query.status = null
  handleSearch()
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, defaultForm)
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, roleName: row.roleName, roleCode: row.roleCode, description: row.description, sort: row.sort, status: row.status })
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
        await updateRole(form)
        ElMessage.success('修改成功')
      } else {
        await addRole(form)
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
  await ElMessageBox.confirm(`确认删除角色「${row.roleName}」？`, '提示', { type: 'warning' })
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

async function openAssign(row) {
  assignRoleId.value = row.id
  assignRoleName.value = row.roleName
  // 拉取全量菜单树 + 该角色已分配菜单
  try {
    const [tree, ids] = await Promise.all([
      request.get('/system/menu/tree'),
      getRoleMenus(row.id)
    ])
    menuTree.value = tree
    checkedMenuIds.value = collectLeafIds(tree, ids)
    assignVisible.value = true
    // destroy-on-close 保证每次打开重新挂载;nextTick 后精确回显叶子节点勾选(父级半选由子级推导)
    await nextTick()
    menuTreeRef.value?.setCheckedKeys(checkedMenuIds.value)
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

/**
 * 从已分配菜单 id 中过滤出树里的叶子节点
 * 父目录半选状态不应直接 setCheckedKeys(会联动勾选全部子级),只回显叶子,父级自动半选
 */
function collectLeafIds(tree, ids) {
  const idSet = new Set(ids || [])
  const leafIds = []
  const walk = (nodes) => {
    nodes.forEach((n) => {
      if (!n.children || n.children.length === 0) {
        if (idSet.has(n.id)) leafIds.push(n.id)
      } else {
        walk(n.children)
      }
    })
  }
  walk(tree || [])
  return leafIds
}

async function handleAssign() {
  const checked = menuTreeRef.value.getCheckedKeys()
  const halfChecked = menuTreeRef.value.getHalfCheckedKeys()
  // 半选(父级部分勾选)的目录也要传给后端,否则子菜单勾选但父目录未全选时父级会丢失
  const menuIds = [...new Set([...checked, ...halfChecked])]
  assignSaving.value = true
  try {
    await assignRoleMenus(assignRoleId.value, menuIds)
    ElMessage.success('分配成功')
    assignVisible.value = false
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  } finally {
    assignSaving.value = false
  }
}

onMounted(fetchData)
</script>
