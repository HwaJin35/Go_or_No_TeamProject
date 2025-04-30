<template>
  <div class="signup-container">
    <h2>회원가입</h2>
    <form @submit.prevent="registerUser">

      <!-- 이메일 입력 & 중복확인 -->
      <div>
        <label>이메일</label>
        <div style="display: flex; gap: 8px">
          <input type="email" v-model="email" required placeholder="예: user@example.com" />
          <button type="button" @click="checkEmail" :disabled="checking || emailChecked">중복 확인</button>
        </div>

        <p v-if="emailCheckMessage" :style="{ color: emailExists ? 'red' : 'green', marginTop: '4px' }">
          {{ emailCheckMessage }}
        </p>
        
        <span v-if="checking">
          <img src="@/assets/img/loading-spinner.gif" alt="로딩 중" width="30" height="30" />
        </span>
      </div>

      <!-- 인증코드 발송 및 인증확인 -->
      <div v-if="emailChecked && !emailExists">
        <div style="display: flex; gap: 8px; margin-top: 16px">
          <button type="button" @click="sendCode" :disabled="loading || emailVerified">{{ sendCodeText }}</button>
        </div>

        <div v-if="codeSent" style="margin-top: 8px">
          <div style="display: flex; gap: 8px">
            <input v-model="code" placeholder="인증 코드를 입력하세요" />
            <button type="button" @click="verifyCode" :disabled="loading || emailVerified">인증 확인</button>
          </div>
          <p style="color: red" v-if="verifyError">{{ verifyError }}</p>
        </div>
        <p v-if="emailVerified" style="color: green; margin-top: 8px">✅ 이메일 인증 완료</p>
        <span v-if="loading">
          <img src="@/assets/img/loading-spinner.gif" alt="로딩 중" width="30" height="30" />
        </span>
      </div>

      <!-- 회원가입 폼 -->
      <div style="margin-top: 20px">
        <label>비밀번호</label>
        <input type="password" v-model="password" required placeholder="비밀번호를 입력하세요" />
      </div>

      <div>
        <label>닉네임</label>
        <input type="text" v-model="nickname" required placeholder="닉네임을 입력하세요" />
      </div>

      <div>
        <label>프로필 이미지 (선택)</label>
        <input type="file" @change="handleFileChange" accept="image/*" />
      </div>

      <div v-if="previewImage" class="preview-container">
        <img :src="previewImage" alt="프로필 미리보기" class="preview-image" />
      </div>

      <p v-if="message" style="margin-top: 10px">{{ message }}</p>

      <button type="submit" :disabled="!emailVerified">회원가입</button>
    </form>
  </div>
</template>

<script>
import { registerUser, sendAuthCode, verifyAuthCode, checkEmailDuplicate } from "./signupApi";

export default {
  name: "Signup",
  data() {
    return {
      email: "",
      password: "",
      nickname: "",
      profileImageFile: null,
      previewImage: null,  // 미리보기용 Base64 URL

      // 이메일 중복체크용
      emailChecked: false,
      emailExists: false,
      emailCheckMessage: "",
      checking: false, // 로딩표시
      
      // 인증 관련 상태
      code: "",
      emailVerified: false,
      codeSent: false,    // 인증코드 발송 여부
      emailSent: false,
      verifyError: "",
      loading: false,
      sendCodeText: '인증 요청',
      message: "",
    };
  },
  watch: {
    email(newEmail, oldEmail) {
    if ((this.emailVerified || this.emailChecked) && newEmail !== oldEmail) {
      this.emailChecked = false;
      this.emailExists = false;
      this.emailCheckMessage = '';
      this.code = '';
      this.emailVerified = false;
      this.codeSent = false;
      this.emailSent = false;
      this.verifyError = '';
      this.message = '';
      this.loading = false;
      this.sendCodeText = '인증 요청';

      // ✅ vue-toasted 전역 토스트 사용
      this.$toasted.show("✉️ 이메일을 수정하셨네요! 인증절차를 다시 해주세요.", {
        duration: 3000,
        position: "top-center",
        theme: "primary"
      });
    }
  }
},
  methods: {
    // 이메일 중복확인
    async checkEmail() {
      if (!this.email) {
        this.emailCheckMessage = "이메일을 입력하세요.";
        return;
      }
      if (!this.isValidEmail(this.email)) {
      this.emailCheckMessage = "유효한 이메일 형식이 아닙니다.";
      return;
      }
      this.checking = true;
      try {
        const response = await checkEmailDuplicate(this.email);
        if (response.data.exists) {
          this.emailExists = true;
          this.emailCheckMessage = "이미 사용 중인 이메일입니다.";
        } else {
          this.emailExists = false;
          this.emailCheckMessage = "사용 가능한 이메일입니다.";
          this.emailChecked = true;
        }
      } catch (error) {
        console.error(error);
        this.emailCheckMessage = "이메일 확인에 실패했습니다.";
      } finally {
        this.checking = false;
      }
    },

    // 인증코드 메일 발송
    async sendCode() {
      if (!this.isValidEmail(this.email)) {
      this.message = "유효한 이메일 형식이 아닙니다.";
      this.loading = false;
      return;
      }
      this.loading = true;
      try {
        await sendAuthCode(this.email);
        this.codeSent = true;
        this.emailSent = true;
        this.sendCodeText = "인증 재요청";
        this.message = "인증 코드가 이메일로 전송되었습니다.";
      } catch (e) {
        this.message = "인증 코드 전송 실패: " + (e.response?.data || e.message);
      } finally {
        this.loading = false;
      }
    },

    // 인증 코드 검증
    async verifyCode() {
      if (!this.code) {
        this.verifyError = "인증 코드를 입력하세요.";
        return;
      }
      this.loading = true;
      try {
        await verifyAuthCode(this.email, this.code);
        this.emailVerified = true;
        this.verifyError = "";
        this.message = "이메일 인증이 완료되었습니다.";
      } catch (e) {
        this.verifyError = "인증코드가 일치하지 않습니다. ";
        console.error((e.response?.data || e.message));
      } finally {
        this.loading = false;
      }
    },

    // 회원 가입 요청
    async registerUser() {
      if (!this.emailVerified) {
        this.message = "이메일 인증을 먼저 완료하세요.";
        return;
      }

      if (this.password.length < 8) {
      this.message = "비밀번호는 최소 8자 이상이어야 합니다.";
      return;
      }

      try {
        const formData = new FormData();
        formData.append("email", this.email);
        formData.append("password", this.password);
        formData.append("nickname", this.nickname);
        formData.append("authProvider", "LOCAL");

        // 이미지가 있다면 추가
        if (this.profileImageFile) {
          formData.append("files", this.profileImageFile);
        }

        const signupData = await registerUser(formData);

        console.log("회원가입 데이터", signupData);
        alert("회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.");

        // 회원가입 성공후 원하는 곳으로 이동
        this.$router.push("/login"); // 로그인 페이지로 이동

      } catch (error) {
        console.error((error.response?.data || error.message));
        alert("회원 가입에 실패했습니다.");
      }
    },

    // 이메일 유효성 검사
    isValidEmail(email){
      const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return re.test(email);
    },

    // 파일 업로드
    handleFileChange(event) {
      const file = event.target.files[0];
      this.profileImageFile = file;

      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.previewImage = e.target.result; // base64 URL 저장
        };
        reader.readAsDataURL(file); // 파일을 Base64 URL로 변환
      } else {
        this.previewImage = null; // 파일 없으면 초기화
      }
    },
  }
};
</script>
<style scoped>
.signup-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.signup-container h2 {
  text-align: center;
  margin-bottom: 20px;
}

.signup-container form div {
  margin-bottom: 15px;
}

.signup-container label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.signup-container input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

.signup-container button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.signup-container button:hover {
  background-color: #45a049;
}

.preview-container {
  margin-top: 20px;
  text-align: center;
}

.preview-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  border: 1px solid #ccc;
}
</style>