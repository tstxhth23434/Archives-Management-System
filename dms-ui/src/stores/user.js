import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login as loginApi, logout as logoutApi, getInfo, getMenus } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: {},
    perms: []
  }),
  getters: {
    // 是否有某按钮权限
    hasPerm: (state) => (perm) => {
      return state.perms.includes(perm)
    }
  },
  actions: {
    // 登录：调用接口，保存 token 和用户信息
    async login(form) {
      const data = await loginApi(form)
      this.token = data.token
      this.userInfo = data.userInfo || {}
      setToken(data.token)
      return data
    },
    // 拉取当前用户信息
    async fetchUserInfo() {
      this.userInfo = await getInfo()
      return this.userInfo
    },
    // 拉取菜单树 + 权限码（返回菜单树，perms 存 store）
    async fetchMenus() {
      const data = await getMenus()
      this.perms = data.perms || []
      return data.menus || []
    },
    // 登出
    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // 登出接口失败也继续清理本地状态
      } finally {
        this.reset()
      }
    },
    // 清空本地登录态
    reset() {
      this.token = ''
      this.userInfo = {}
      this.perms = []
      removeToken()
    }
  }
})
