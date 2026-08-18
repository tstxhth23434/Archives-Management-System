import request from './request'

// 查询全部启用角色（下拉框用）
export function listRoles() {
  return request.get('/system/role/list')
}

// 分页查询角色
export function pageRoles(params) {
  return request.get('/system/role/page', { params })
}

// 新增角色
export function addRole(data) {
  return request.post('/system/role', data)
}

// 编辑角色
export function updateRole(data) {
  return request.put('/system/role', data)
}

// 删除角色
export function deleteRole(id) {
  return request.delete(`/system/role/${id}`)
}

// 查询角色已分配的菜单ID列表
export function getRoleMenus(id) {
  return request.get(`/system/role/${id}/menus`)
}

// 给角色分配菜单
export function assignRoleMenus(id, menuIds) {
  return request.post(`/system/role/${id}/menus`, menuIds)
}
