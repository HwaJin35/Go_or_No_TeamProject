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
      <div class="login-btn-group">
        <button class="half-btn login-btn" type="submit">로그인</button>
        <button button class="half-btn home-btn" @click="$router.push('/')">홈으로</button>
      </div>
      <div class="text-right mt-4">
      <router-link
        to="/reset-password"
        class="text-sm text-green-600 hover:underline"
      >
        비밀번호를 잊으셨나요?
      </router-link>
    </div>
    </form>
  </div>
</template>
<script>
import axiosInstance from '@/utils/axiosInstance';
import { isLoggedIn } from '@/utils/loginState';
import { saveToken } from '../../utils/loginState';

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
        // 토큰 저장 및 타이머 시작
        saveToken(accessToken);
        // TopNavBar에 로그인 성공 알리기
        this.$root.$emit('loginSuccess');
        // AccessToken을 localStorage에 저장
        localStorage.setItem('accessToken', accessToken);
        
        // 로그인 성공하면 isLoggedIn = true
        isLoggedIn.value = true;
        // 토큰 확인
        // console.log('accessToken: ',localStorage.getItem('accessToken'));

        // 로그인 성공 후 페이지 이동 (router 사용)
        alert('로그인되었습니다.\n메인 페이지로 이동합니다.');
        this.$router.push('/') // 메인페이지로 이동
        } catch (error) { 
          console.error('로그인 실패:', error);
          const code = error.response?.data?.error;
          const message = error.response?.data?.message;
          console.log(code);
          console.log(message);

          if(code === 'ACCOUNT_DISABLED') {
            alert('이메일 인증이 필요합니다.');
          } else if(code === 'BAD_CREDENTIALS') {
            alert('비밀번호가 올바르지 않습니다.');
          } else if(code === 'AUTHENTICATION_USER_NOT_FOUND') {
            alert('존재하지 않는 사용자입니다.');
          } else {
            alert('로그인에 실패했습니다. 이메일 또는 비밀번호를 확인하세요.')
        }
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

.login-btn-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.login-btn {
  background-color: #4caf50;
}

.login-btn:hover {
  background-color: #45a049;
}

.home-btn {
  background-color: #1976d2;
}

.home-btn:hover {
  background-color: #1565c0;
}

.half-btn {
  width: 50%; 
  padding: 10px;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  text-align: center;
}

/* .login-container button {
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
} */
</style>