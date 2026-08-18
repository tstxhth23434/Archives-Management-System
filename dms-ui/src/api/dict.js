import request from './request'

// ===== 字典类型 =====
export function pageDicts(params) {
  return request.get('/system/dict/page', { params })
}

export function addDict(data) {
  return request.post('/system/dict', data)
}

export function updateDict(data) {
  return request.put('/system/dict', data)
}

export function deleteDict(id) {
  return request.delete(`/system/dict/${id}`)
}

// ===== 字典项 =====
export function listDictItems(dictCode) {
  return request.get(`/system/dict/items/${dictCode}`)
}

export function addDictItem(data) {
  return request.post('/system/dict/item', data)
}

export function updateDictItem(data) {
  return request.put('/system/dict/item', data)
}

export function deleteDictItem(id) {
  return request.delete(`/system/dict/item/${id}`)
}
