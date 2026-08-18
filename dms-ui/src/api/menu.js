import request from './request'

// 查询菜单树（管理端）
export function getMenuTree() {
  return request.get('/system/menu/tree')
}

// 新增菜单
export function addMenu(data) {
  return request.post('/system/menu', data)
}

// 编辑菜单
export function updateMenu(data) {
  return request.put('/system/menu', data)
}

// 删除菜单
export function deleteMenu(id) {
  return request.delete(`/system/menu/${id}`)
}
