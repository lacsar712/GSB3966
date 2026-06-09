import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.roleCode === 'ADMIN')
  const isManager = computed(() => userInfo.value?.roleCode === 'MANAGER' || userInfo.value?.roleCode === 'ADMIN')
  const isTrader = computed(() => userInfo.value?.roleCode === 'TRADER')

  async function loginAction(loginForm) {
    const res = await login(loginForm)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    userInfo.value = {
      userId: res.data.userId,
      username: res.data.username,
      realName: res.data.realName,
      roleCode: res.data.roleCode,
      roleName: res.data.roleName
    }
    return res.data
  }

  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
    return res.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isManager,
    isTrader,
    loginAction,
    fetchUserInfo,
    logout
  }
})
