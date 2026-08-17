import request from './request'

// 分页查询用户
export function pageUsers(params) {
  return request.get('/system/user/page', { params })
}

// 新增用户
export function addUser(data) {
  return request.post('/system/user', data)
}

// 编辑用户
export function updateUser(data) {
  return request.put('/system/user', data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/system/user/${id}`)
}

// 启用/禁用用户
export function changeUserStatus(id, status) {
  return request.put(`/system/user/${id}/status/${status}`)
}

// 重置用户密码
export function resetPassword(id) {
  return request.put(`/system/user/${id}/reset-password`)
}
