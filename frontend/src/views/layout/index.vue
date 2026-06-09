<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="28"><TrendCharts /></el-icon>
        <span>策略管理系统</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        router
        class="menu"
        background-color="#1a237e"
        text-color="#b0b8d4"
        active-text-color="#fff"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item :index="route.path" v-if="!route.meta?.hidden">
            <el-icon><component :is="route.meta?.icon" /></el-icon>
            <span>{{ route.meta?.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  
  <el-dialog
    v-model="passwordDialogVisible"
    title="修改密码"
    width="400px"
  >
    <el-form
      ref="passwordFormRef"
      :model="passwordForm"
      :rules="passwordRules"
      label-width="100px"
    >
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleChangePassword" :loading="changing">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, ArrowDown, TrendCharts } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { changePassword } from '@/api/user'
import Breadcrumb from './components/Breadcrumb.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menuRoutes = computed(() => {
  const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')
  return layoutRoute?.children?.filter(r => !r.meta?.admin || userStore.isAdmin) || []
})

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      passwordDialogVisible.value = true
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/login')
        ElMessage.success('已退出登录')
      })
      break
  }
}

const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const changing = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleChangePassword = async () => {
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changing.value = true
      try {
        await changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        passwordDialogVisible.value = false
        userStore.logout()
        router.push('/login')
      } finally {
        changing.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  
  .sidebar {
    background: #1a237e;
    
    .logo {
      height: 64px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      
      .el-icon {
        margin-right: 12px;
      }
    }
    
    .menu {
      border-right: none;
      
      :deep(.el-menu-item) {
        height: 50px;
        line-height: 50px;
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.05) !important;
        }
        
        &.is-active {
          background-color: #3949ab !important;
        }
        
        .el-icon {
          margin-right: 12px;
        }
      }
    }
  }
  
  .header {
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
    
    .header-right {
      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 4px 8px;
        border-radius: 4px;
        transition: background 0.3s;
        
        &:hover {
          background: #f5f5f5;
        }
        
        .username {
          margin: 0 8px;
          font-size: 14px;
          color: #333;
        }
      }
    }
  }
  
  .main {
    background: #f0f2f5;
    padding: 20px;
    overflow-y: auto;
  }
}
</style>
