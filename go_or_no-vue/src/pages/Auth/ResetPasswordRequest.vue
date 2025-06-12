<template>
  <div class="reset-request-container">
    <h2>비밀번호 찾기</h2>
    <form @submit.prevent="requestReset" class="reset-request-form">
      <div>
        <input
          v-model="email"
          type="email"
          placeholder="가입된 이메일 주소 입력"
          required
        />
        <p v-if="error" class="error-message">{{ error }}</p>
      </div>

      <button
        type="submit"
        :disabled="loading || !email"
      >
        <img
          v-if="loading"
          src="@/assets/img/loading-spinner.gif"
          alt="로딩 중"
          width="30"
          height="30"
        />
        <span>{{ loading ? "메일 전송 중..." : "재설정 메일 보내기" }}</span>
      </button>

      <!-- 완료 메시지 -->
      <p
        v-if="success"
        class="success-message"
      >
        ✅ 비밀번호 재설정 링크가 이메일로 전송되었습니다.
      </p>
    </form>
  </div>
</template>

<script>
import axiosInstance from "@/utils/axiosInstance";

export default {
  name: "ResetPasswordRequest",
  data() {
    return {
      email: "",
      loading: false,
      error: "",
      success: false,
    };
  },
  methods: {
    async requestReset() {
      this.error = "";
      this.success = false;
      this.loading = true;

      try {
        await axiosInstance.post("/api/auth/reset-password/send", {
          email: this.email,
        });
        this.success = true;
        // this.email = "";
      } catch (err) {
        const code = err.response?.data?.code;
        switch (code) {
          case "USER_NOT_FOUND":
            this.error = "가입되지 않은 이메일 주소입니다.";
            break;
          case "EMAIL_NOT_VERIFIED":
            this.error = "이메일 인증이 완료되지 않았습니다.";
            break;
          default:
            this.error =
              err.response?.data?.message || "이메일 전송에 실패했습니다.";
        }
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
<style scoped>
.reset-request-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.reset-request-container h2 {
  text-align: center;
  margin-bottom: 20px;
}

.reset-request-container form div {
  margin-bottom: 15px;
}

.reset-request-container input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

.reset-request-container button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.reset-request-container button:disabled {
  background-color: #aaa;
  cursor: not-allowed;
}

.reset-request-container button:hover {
  background-color: #45a049;
}

.success-message {
  color: #2e7d32;
  font-size: 13px;
  font-weight: 500;
  margin-top: 0.5rem;
  text-align: center;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 4px;
}
</style>