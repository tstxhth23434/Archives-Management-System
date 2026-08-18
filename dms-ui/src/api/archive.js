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

// ===== 案卷 CRUD（D10） =====
export function addVolume(data) {
  return request.post('/archive/volume', data)
}

export function updateVolume(data) {
  return request.put('/archive/volume', data)
}

export function deleteVolume(id) {
  return request.delete(`/archive/volume/${id}`)
}

// ===== 文件著录（D10） =====
export function pageFiles(params) {
  return request.get('/archive/file/page', { params })
}

export function addFile(data) {
  return request.post('/archive/file', data)
}

export function updateFile(data) {
  return request.put('/archive/file', data)
}

export function deleteFile(id) {
  return request.delete(`/archive/file/${id}`)
}
