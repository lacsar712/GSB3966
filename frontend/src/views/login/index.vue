<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>交易策略管理系统</h1>
        <p>Trading Strategy Management System</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.loginAction(loginForm)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a237e 0%, #3949ab 50%, #5c6bc0 100%);
  
  .login-box {
    width: 420px;
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    
    .login-header {
      text-align: center;
      margin-bottom: 32px;
      
      h1 {
        font-size: 28px;
        color: #1a237e;
        margin-bottom: 8px;
      }
      
      p {
        font-size: 14px;
        color: #666;
      }
    }
    
    .login-form {
      .login-btn {
        width: 100%;
        font-size: 16px;
        font-weight: 500;
      }
    }
    
    .login-tips {
      margin-top: 24px;
      padding-top: 20px;
      border-top: 1px solid #e8e8e8;
      
      p {
        font-size: 12px;
        color: #999;
        line-height: 1.8;
        margin: 0;
      }
      
      p:first-child {
        color: #666;
        font-weight: 500;
        margin-bottom: 8px;
      }
    }
  }
}
</style>
