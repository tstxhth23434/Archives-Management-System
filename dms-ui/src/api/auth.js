import request from './request'

// 登录
export function login(data) {
  return request.post('/auth/login', data)
}

// 登出
export function logout() {
  return request.post('/auth/logout')
}

// 获取当前登录用户信息
export function getInfo() {
  return request.get('/auth/info')
}

// 获取当前用户菜单树 + 权限码
export function getMenus() {
  return request.get('/auth/menus')
}

// 修改密码
export function changePassword(data) {
  return request.post('/auth/change-password', data)
}
