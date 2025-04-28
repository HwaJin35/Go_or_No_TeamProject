<template>
  <div class="signup-container">
    <h2>회원가입</h2>
    <form @submit.prevent="registerUser">
      <div>
        <label>이메일</label>
        <input type="email" v-model="email" required placeholder="예: user@example.com" />
      </div>
      <div>
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
      <button type="submit">회원가입</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Signup",
  data() {
    return {
      email: "",
      password: "",
      nickname: "",
      profileImageFile: null,
      previewImage: null,  // 미리보기용 Base64 URL
    };
  },
  methods: {
    async registerUser() {
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

        const response = await axios.post(
          "http://localhost:8090/api/users/signup",
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );

        console.log("회원가입 데이터", response.data);
        alert("회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.");

        // 회원가입 성공후 원하는 곳으로 이동
        this.$router.push("/login"); // 로그인 페이지로 이동
      } catch (error) {
        console.error(error);
        alert("회원 가입에 실패했습니다.");
      }
    },
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
  },
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