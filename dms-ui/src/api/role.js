import request from './request'

// 查询全部启用角色（下拉框用）
export function listRoles() {
  return request.get('/system/role/list')
}
