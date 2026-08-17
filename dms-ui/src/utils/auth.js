// Token 读写（存 localStorage，关闭浏览器仍保持登录态）
const TOKEN_KEY = 'dms_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}
