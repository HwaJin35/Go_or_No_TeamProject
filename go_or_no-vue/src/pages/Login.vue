<template>
  <div class="login-container">
    <h2>로그인</h2>
    <form @submit.prevent="login">
      <div>
        <input v-model="email" type="email" placeholder="이메일 입력" required />
      </div>
      <div>
        <input v-model="password" type="password" placeholder="비밀번호 입력" required />
      </div>
      <button type="submit">로그인</button>
    </form>
  </div>
</template>
<script>
import axiosInstance from '@/utils/axiosInstance';
import { isLoggedIn } from '@/utils/loginState';

export default {
  name: 'LoginForm',
  data() {
    return {
      email: '',
      password: ''
    }
  },
  methods: {
    async login() {
      try {
        const response = await axiosInstance.post('/api/login', {
          email: this.email,
          password: this.password
        })

        const accessToken = response.data.accessToken;
        // AccessToken을 localStorage에 저장
        localStorage.setItem('accessToken', accessToken);
        // 로그인 성공하면 isLoggedIn = true
        isLoggedIn.value = true;
        // 토큰 확인
        console.log('accessToken: ',localStorage.getItem('accessToken'));

        // 로그인 성공 후 페이지 이동 (router 사용)
        this.$router.push('/') // 메인페이지로 이동
      } catch (error) {
        console.error('로그인 실패:', error)
        alert('로그인에 실패했습니다. 이메일 또는 비밀번호를 확인하세요.')
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.login-container h2 {
  text-align: center;
  margin-bottom: 20px;
}

.login-container form div {
  margin-bottom: 15px;
}

.login-container input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

.login-container button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.login-container button:hover {
  background-color: #45a049;
}
</style>