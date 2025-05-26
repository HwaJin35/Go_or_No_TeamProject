<template>
  <div class="max-w-md mx-auto mt-16 p-6 border rounded-lg shadow bg-white">
    <h2 class="text-2xl font-semibold text-center mb-6 text-gray-800">비밀번호 찾기</h2>
    <form @submit.prevent="requestReset" class="space-y-4">
      <div>
        <input
          v-model="email"
          type="email"
          placeholder="가입된 이메일 주소 입력"
          required
          class="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
        />
        <p v-if="error" class="text-sm text-red-500 mt-1">{{ error }}</p>
      </div>

      <button
        type="submit"
        :disabled="loading || !email"
        class="w-full py-2 px-4 bg-green-600 text-white rounded-md font-semibold hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition flex justify-center items-center gap-2"
      >
        <img
          v-if="loading"
          src="@/assets/img/loading-spinner.gif"
          alt="로딩 중"
          class="w-3 h-3"
        />
        <span>{{ loading ? "메일 전송 중..." : "재설정 메일 보내기" }}</span>
      </button>

      <!-- 완료 메시지 -->
      <p
        v-if="success"
        class="text-sm text-green-600 font-medium text-center mt-2"
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
        this.email = "";
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