<template>
  <div class="row">
    <div class="col-xl-4 col-lg-5 col-md-6">
      <user-card :user="user" :key="user.id" />
      <members-card> </members-card>
    </div>
    <div class="col-xl-8 col-lg-7 col-md-6">
      <edit-profile-form :user="user" @profile-updated="fetchMyProfile" />
    </div>
  </div>
</template>
<script>
import EditProfileForm from "./UserProfile/EditProfileForm.vue";
import UserCard from "./UserProfile/UserCard.vue";
import MembersCard from "./UserProfile/MembersCard.vue";
import axiosInstance from "@/utils/axiosInstance";

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
  },
};
</script>
<style></style>
