<template>
  <div class="row">
    <div class="col-xl-4 col-lg-5 col-md-6">
      <user-card :user="user" :key="user.id" />
      <members-card> </members-card>
    </div>
    <div class="col-xl-8 col-lg-7 col-md-6">
      <edit-profile-form :user="user" @profile-updated="fetchMyProfile" />
    </div>
    <!-- 회원 탈퇴 버튼 -->
    <div class="text-center mt-4">
      <p-button type="danger" round @click.native="confirmDeleteAccount">
        회원 탈퇴하기
      </p-button>
    </div>
  </div>
</template>
<script>
import EditProfileForm from "@/pages/UserProfile/EditProfileForm.vue";
import UserCard from "@/pages/UserProfile/UserCard.vue";
import MembersCard from "@/pages/UserProfile/MembersCard.vue";
import axiosInstance from "@/utils/axiosInstance";
import { isLoggedIn, removeToken } from "@/utils/loginState";

export default {
  components: {
    EditProfileForm,
    UserCard,
    MembersCard,
  },
  data() {
    return {
      user: {
        id: null,
        nickname: "",
        email: "",
        profileImageUrl: "",
        aboutMe: "",
      },
    };
  },
  mounted() {
    this.fetchMyProfile();
  },
  methods: {
    async fetchMyProfile() {
      try {
        const response = await axiosInstance.get("/api/users/me");
        const userData = response.data;

        // user 객체를 통째로 새로 할당
        this.user = {
          id: userData.id,
          nickname: userData.nickname,
          email: userData.email,
          profileImageUrl: userData.profileImageFile,
          aboutMe: userData.aboutMe || "",
        };
      } catch (error) {
        console.error("유저 정보 가져오기 실패", error);
        this.$router.push("/login");
      }
    },
    async confirmDeleteAccount() {
      if( confirm("정말 탈퇴하시겠습니까?\n 탈퇴 시 작성한 데이터들이 모두 삭제되며, 복구할 수 없습니다.") ) {
        try {
          await axiosInstance.delete(`/api/users/${Number(this.user.id)}`);
          alert("회원 탈퇴가 완료되었습니다.");
          removeToken();  // 토큰 삭제
          isLoggedIn.value = false; // 로그인 상태 false로 변경
          this.$router.push("/");  // 메인페이지 이동
        } catch (error) {
          console.error("회원 탈퇴 실패", error);
          alert("회원 탈퇴에 실패하였습니다. 관리자에게 문의하세요.");
        }
      }
    }
  },
};
</script>
<style></style>
