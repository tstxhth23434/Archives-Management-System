import request from './request'

// ===== 全宗管理 =====
export function pageFonds(params) {
  return request.get('/archive/fonds/page', { params })
}

export function listFonds() {
  return request.get('/archive/fonds/list')
}

export function addFonds(data) {
  return request.post('/archive/fonds', data)
}

export function updateFonds(data) {
  return request.put('/archive/fonds', data)
}

export function deleteFonds(id) {
  return request.delete(`/archive/fonds/${id}`)
}

// ===== 门类管理 =====
export function pageTypes(params) {
  return request.get('/archive/type/page', { params })
}

export function listTypes(fondsId) {
  return request.get('/archive/type/list', { params: { fondsId } })
}

export function addType(data) {
  return request.post('/archive/type', data)
}

export function updateType(data) {
  return request.put('/archive/type', data)
}

export function deleteType(id) {
  return request.delete(`/archive/type/${id}`)
}

// ===== 档案树 + 案卷查询（D9） =====
export function getArchiveTree() {
  return request.get('/archive/tree')
}

export function pageVolumes(params) {
  return request.get('/archive/volume/page', { params })
}
