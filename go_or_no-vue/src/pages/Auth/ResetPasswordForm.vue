<template>
  <div class="max-w-md mx-auto mt-16 p-6 border rounded-lg shadow bg-white">
    <h2 class="text-2xl font-semibold text-center mb-6 text-gray-800">비밀번호 재설정</h2>
    <form @submit.prevent="submitNewPassword" class="space-y-4">
      <div>
        <input
          v-model="newPassword"
          type="password"
          placeholder="새 비밀번호 입력"
          class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
        />
      </div>

      <div>
        <input
          v-model="confirmPassword"
          type="password"
          placeholder="새 비밀번호 확인"
          class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
        />
        <p v-if="error" class="text-sm text-red-500 mt-1">{{ error }}</p>
      </div>

      <button
        type="submit"
        class="w-full py-2 px-4 bg-green-600 text-white rounded-md font-semibold hover:bg-green-700 transition"
      >
        비밀번호 변경
      </button>
    </form>
  </div>
</template>

<script>
import axiosInstance from "@/utils/axiosInstance";

export default {
  name: "ResetPasswordForm",
  data() {
    return {
      newPassword: "",
      confirmPassword: "",
      error: "",
    };
  },
  computed: {
    token() {
      return this.$route.query.token || "";
    },
  },
  methods: {
    async submitNewPassword() {
      this.error = "";

      // 비밀번호 유효성 검사
      if (this.newPassword.length < 8) {
        this.error = "비밀번호는 8자 이상이어야 합니다.";
        return;
      }
      if (this.newPassword !== this.confirmPassword) {
        this.error = "비밀번호가 서로 일치하지 않습니다.";
        return;
      }

      try {
        await axiosInstance.post("/api/auth/reset-password/reset", {
          token: this.token,
          newPassword: this.newPassword,
          confirmPassword: this.confirmPassword,
        });

        alert("비밀번호가 성공적으로 변경되었습니다.");
        this.$router.push("/login");

      } catch (err) {
        const code = err.response?.data?.code || "";
        const fallback = err.response?.data?.message || "비밀번호 변경에 실패했습니다.";

        // code 기반으로 매핑 처리
        switch (code) {
          case "PASSWORD_DUPLICATE":
            this.error = "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.";
            break;
          case "EXPIRED_PASSWORD_RESET_TOKEN":
            this.error = "재설정 링크가 만료되었습니다. 다시 요청해 주세요.";
            break;
          case "INVALID_PASSWORD_RESET_TOKEN":
            this.error = "유효하지 않은 비밀번호 재설정 요청입니다.";
            break;
          case "PASSWORD_TOO_SHORT":
            this.error = "비밀번호는 8자 이상이어야 합니다.";
            break;
          default:
            this.error = fallback;
        }
      }
    },
  },
};
</script>