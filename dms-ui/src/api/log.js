import request from './request'

// 分页查询操作日志
export function pageLogs(params) {
  return request.get('/system/log/page', { params })
}
