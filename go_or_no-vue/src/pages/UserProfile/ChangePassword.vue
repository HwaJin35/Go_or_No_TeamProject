<template>
  <div class="change-password-container">
    <h2>비밀번호 변경</h2>
    <form @submit.prevent="changePassword">
      <div>
        <input
          v-model="form.currentPassword"
          type="password"
          placeholder="현재 비밀번호 입력"
          required
        />
        <p v-if="errors.currentPassword" class="error-message">
          {{ errors.currentPassword }}
        </p>
      </div>
      <div>
        <input
          v-model="form.newPassword"
          type="password"
          placeholder="새 비밀번호 입력"
          required
        />
      </div>
      <p v-if="errors.newPassword" class="error-message">
        {{ errors.newPassword }}
      </p>
      <div>
        <input
          v-model="form.confirmPassword"
          type="password"
          placeholder="새 비밀번호 확인"
          required
        />
        <p v-if="errors.confirmPassword" class="error-message">
          {{ errors.confirmPassword }}
        </p>
      </div>
      <button type="submit" :disabled="!isFormValid">비밀번호 변경</button>
    </form>
  </div>
</template>

<script>
import axiosInstance from "@/utils/axiosInstance";

export default {
  name: "ChangePasswordPage",
  data() {
    return {
      form: {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      errors: {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
    };
  },
  computed: {
    // 모든 필드가 입력되어야만 버튼 활성화
    isFormValid() {
      const { currentPassword, newPassword, confirmPassword } = this.form;
      return currentPassword && newPassword && confirmPassword;
    },
  },
  methods: {
    async changePassword() {
      // 메시지 초기화
      this.errors = {
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      };

      const { currentPassword, newPassword, confirmPassword } = this.form;

      if (newPassword.length < 8) {
        this.errors.newPassword = "비밀번호는 최소 8자 이상이어야 합니다.";
        return;
      }

      if (newPassword !== confirmPassword) {
        this.errors.confirmPassword = "새 비밀번호가 일치하지 않습니다.";
        return;
      }

      if (currentPassword === newPassword) {
        this.errors.newPassword =
          "새 비밀번호는 현재 비밀번호와 달라야 합니다.";
        return;
      }

      try {
        await axiosInstance.put("/api/users/me/password", {
          currentPassword,
          newPassword,
          confirmPassword,
        });

        alert("비밀번호가 성공적으로 변경되었습니다.");
        this.form.currentPassword = "";
        this.form.newPassword = "";
        this.form.confirmPassword = "";
        this.$router.push("/me");
      } catch (error) {
        const code = error.response?.data?.code;

        switch (code) {
          case "PASSWORD_MISMATCH":
            this.errors.currentPassword = "현재 비밀번호가 일치하지 않습니다.";
            break;
          case "PASSWORD_CONFIRM_MISMATCH":
            this.errors.confirmPassword = "새 비밀번호가 일치하지 않습니다.";
            break;
          case "PASSWORD_DUPLICATE":
            this.errors.newPassword =
              "새 비밀번호는 현재 비밀번호와 달라야 합니다.";
            break;
          case "PASSWORD_TOO_SHORT":
            this.errors.newPassword = "비밀번호는 최소 8자 이상이어야 합니다.";
            break;
          default:
            alert(
              error.response?.data?.message || "비밀번호 변경에 실패했습니다."
            );
        }
      }
    },
  },
};
</script>

<style scoped>
.change-password-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.change-password-container h2 {
  text-align: center;
  margin-bottom: 20px;
}

.change-password-container form div {
  margin-bottom: 15px;
}

.change-password-container input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

.change-password-container button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.change-password-container button:disabled {
  background-color: #aaa;
  cursor: not-allowed;
}

.error-message {
  color: red;
  font-size: 13px;
  margin-top: 5px;
}
</style>