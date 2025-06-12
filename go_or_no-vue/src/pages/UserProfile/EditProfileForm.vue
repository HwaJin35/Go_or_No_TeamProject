<template>
  <card class="card" title="Edit Profile">
    <div>
      <form @submit.prevent>
        <div class="row">
          <div class="col-md-5">
            <fg-input
              type="text"
              label="Nickname"
              placeholder="Nickname"
              v-model="localUser.nickname"
            />
            <small
              v-if="nicknameMessage"
              :style="{ color: nicknameExists ? 'red' : 'green' }"
            >
              {{ nicknameMessage }}
            </small>
          </div>
          <div class="col-md-7">
            <fg-input
              type="email"
              label="Email"
              placeholder="Email"
              v-model="localUser.email"
              :disabled="true"
            />
          </div>
        </div>

        <!-- 비밀번호 변경 페이지로 리다이렉트 -->
        <div class="text-center mt-3">
          <p-button type="default" round @click.native="goToPasswordChange">
            비밀번호 변경하기
          </p-button>
        </div>

        <!-- 프로필 사진 변경 -->
        <div class="row">
          <div class="col-md-12">
            <div class="form-group">
              <label>프로필 사진 변경</label>
              <input type="file" @change="handleFileChange" accept="image/*" />
              <div v-if="previewImage" class="preview-container">
                <img :src="previewImage" alt="미리보기" class="preview-image" />
              </div>
            </div>
          </div>
        </div>

        <div class="row">
          <div class="col-md-12">
            <div class="form-group">
              <label>About Me</label>
              <quill-editor
                v-model="localUser.aboutMe"
                :options="editorOptions"
              />
            </div>
          </div>
        </div>

        <div class="text-center">
          <p-button type="info" round @click.native.prevent="updateProfile">
            Update Profile
          </p-button>
        </div>
      </form>
    </div>
  </card>
</template>

<script>
import axiosInstance from "@/utils/axiosInstance";
import { quillEditor } from "vue-quill-editor";
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
import { debounce } from "@/utils/debounce";

export default {
  components: { quillEditor },
  props: {
    user: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      localUser: {
        nickname: "",
        email: "",
        aboutMe: "",
      },
      profileImageFile: null,
      previewImage: null,
      editorOptions: {
        placeholder: "300자 이내로 소개글을 입력하세요!",
        theme: "snow",
      },
      nicknameMessage: "",
      nicknameExists: false,
    };
  },
  watch: {
    user: {
      immediate: true,
      handler(newInfo) {
        this.localUser.nickname = newInfo.nickname;
        this.localUser.email = newInfo.email;
        this.localUser.aboutMe = newInfo.aboutMe || "";
      },
    },
    "localUser.aboutMe"(newVal) {
      // Quill은 HTML 태그 포함되어 길이가 늘어남. 실제 텍스트만 추출해서 검사
      const tempDiv = document.createElement("div");
      tempDiv.innerHTML = newVal || "";
      const plainText = tempDiv.innerText || tempDiv.textContent || "";

      if (plainText.length > 300) {
        this.$toasted.show("소개글은 300자까지만 작성할 수 있습니다.", {
          type: "error",
          duration: 1000, // 3초 뒤 자동 사라짐
          position: "top-center", // 위치 설정 가능 (top-right, bottom-right 등)
        });
        // 3000자까지만 자른 다음 다시 HTML 변환
        const truncatedText = plainText.slice(0, 300);
        this.localUser.aboutMe = `<p>${truncatedText}</p>`;
      }
    },
    "localUser.nickname"(newVal) {
      this.nicknameError = "";
      if (newVal && newVal !== this.user.nickname) {
        this.debounceCheckNicknameEdit(newVal);
      }
    },
    user: {
      immediate: true,
      handler(newInfo) {
        this.localUser.nickname = newInfo.nickname;
        this.localUser.email = newInfo.email;
        this.localUser.aboutMe = newInfo.aboutMe || "";
      },
    },
  },
  created() {
    this.debounceCheckNicknameEdit = debounce(this.checkNicknameEdit, 400);
  },
  methods: {
    handleFileChange(event) {
      const file = event.target.files[0];
      // console.log("[LOG] 선택한 파일:", file);
      this.profileImageFile = file;
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.previewImage = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    async checkNicknameEdit(nickname) {
      if (!nickname || nickname.trim().length < 3) {
        this.nicknameExists = true;
        this.nicknameMessage = "닉네임은 최소 3자 이상이어야 합니다.";
        return;
      }
      try {
        const res = await axiosInstance.get("/api/users/check-nickname", {
          params: { nickname },
        });
        if (res.data.exists && nickname !== this.user.nickname) {
          this.nicknameExists = true;
          this.nicknameMessage = "이미 사용 중인 닉네임입니다.";
        } else {
          this.nicknameExists = false;
          this.nicknameMessage = "✅ 사용 가능한 닉네임입니다.";
        }
      } catch (e) {
        this.nicknameError = "닉네임 확인 중 오류 발생";
      }
    },
    async updateProfile() {
      try {
        const formData = new FormData();
        formData.append("nickname", this.localUser.nickname);
        formData.append("aboutMe", this.localUser.aboutMe); // 추가

        if (this.profileImageFile) {
          formData.append("uploadFiles", this.profileImageFile);
        }

        const response = await axiosInstance.put("/api/users/me", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        if(response.ok) {
          // 성공 시 TopNavbar의 사용자 정보 새로고침
          this.$root.$emit('refreshUserInfo');
        }

        if (this.nicknameExists) {
          alert("다른 닉네임을 입력해주세요.");
          return;
        }

        alert("프로필이 성공적으로 수정되었습니다.");
        this.$emit("profile-updated");

        this.profileImageFile = null;
        this.previewImage = null;

        const fileInput = this.$el.querySelector('input[type="file"]');
        if (fileInput) fileInput.value = "";
      } catch (error) {
        const code = error.response?.data?.code;

        if (code === "NICKNAME_DUPLICATE") {
          alert("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요.");
        } else {
          console.error("프로필 수정 실패", error);
          alert(error.response?.data?.message || "프로필 수정에 실패했습니다.");
        }
      }
    },
    goToPasswordChange() {
      this.$router.push("/change-password");
    },
  },
};
</script>

<style scoped>
.preview-container {
  margin-top: 10px;
  text-align: center;
}
.preview-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 10px;
  border: 1px solid #ccc;
}
</style>