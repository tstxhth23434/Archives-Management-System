import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'

// 创建 axios 实例，基地址 /api（由 vite 代理转发到后端 8080）
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动带上 token
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理后端 Result 返回体 { code, message, data }
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // Blob 响应（文件下载）直接返回，不按 Result 判断
    if (res instanceof Blob) {
      return res
    }
    // 成功：直接返回 data 业务数据
    if (res.code === 200) {
      return res.data
    }
    // 未登录：清 token，跳登录页
    if (res.code === 401) {
      removeToken()
      ElMessage.error(res.message || '未登录或登录已过期')
      location.href = '/login'
      return Promise.reject(new Error(res.message || '未登录'))
    }
    // 其他异常（业务错误、无权限等）
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    ElMessage.error(error.message || '网络异常，请稍后重试')
    return Promise.reject(error)
  }
)

export default request
