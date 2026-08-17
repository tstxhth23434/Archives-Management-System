import { defineStore } from 'pinia'

// 菜单状态：存储后端返回的菜单树，供主布局侧边栏渲染
export const useMenuStore = defineStore('menu', {
  state: () => ({
    menus: []
  }),
  actions: {
    setMenus(menus) {
      this.menus = menus || []
    }
  }
})
