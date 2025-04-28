<template>
  <card class="card" title="Edit Profile">
    <div>
      <form @submit.prevent>
        <div class="row">
          <div class="col-md-5">
            <fg-input
              type="text"
              label="Username"
              placeholder="Username"
              v-model="localUser.username"
            />
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
import Quill from "quill";

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
        username: "",
        email: "",
        aboutMe: "",
      },
      profileImageFile: null,
      previewImage: null,
      editorOptions: {
        placeholder: "300자 이내로 소개글을 입력하세요!",
        theme: "snow",
      },
    };
  },
  watch: {
    user: {
      immediate: true,
      handler(newInfo) {
        this.localUser.username = newInfo.nickname;
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
  },
  methods: {
    handleFileChange(event) {
      const file = event.target.files[0];
      this.profileImageFile = file;
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.previewImage = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    async updateProfile() {
      try {
        const formData = new FormData();
        formData.append("nickname", this.localUser.username);
        formData.append("aboutMe", this.localUser.aboutMe); // 추가

        if (this.profileImageFile) {
          formData.append("files", this.profileImageFile);
        }

        await axiosInstance.put(`/api/users/${this.user.id}`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        alert("프로필이 성공적으로 수정되었습니다.");
        this.$emit("profile-updated");

        this.profileImageFile = null;
        this.previewImage = null;

        const fileInput = this.$el.querySelector('input[type="file"]');
        if (fileInput) fileInput.value = "";
      } catch (error) {
        console.error("프로필 수정 실패", error);
        alert("프로필 수정에 실패했습니다.");
      }
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