<template>
  <card class="card-user">
    <div slot="image">
      <img src="@/assets/img/background.jpg" alt="..." />
    </div>

    <div>
      <div class="author">
        <img
          class="avatar border-white"
          :src="user.profileImageUrl || require('@/assets/img/faces/face-2.jpg')"
          alt="프로필 사진"
        />
        <h4 class="title">
          {{ localUser.nickname }}
          <br />
          <a href="#">
            <small>{{ localUser.email }}</small>
          </a>
        </h4>
      </div>
      <div class="description text-center" v-html="localUser.aboutMe || '자기소개가 없습니다.'">
      </div>
    </div>
    <hr />
    <div class="text-center">
      <div class="row">
        <div
          v-for="(info, index) in details"
          :key="index"
          :class="getClasses(index)"
        >
          <h5>
            {{ info.title }}
            <br />
            <small>{{ info.subTitle }}</small>
          </h5>
        </div>
      </div>
    </div>
  </card>
</template>
<script>
export default {
  props: {
    user: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      details: [
        {
          title: "조회수",
          subTitle: "방문수",
        },
        {
          title: "찜한 장소",
          subTitle: "5",
        },
        {
          title: "메시지 수",
          subTitle: "10",
        },
      ],
    };
  },
  watch: {
    user: {
      immediate: true,
      handler(newUser) {
        if (newUser) {
          this.localUser = { ...newUser }; // props 복제
        }
      },
    },
  },
  methods: {
    getClasses(index) {
      var remainder = index % 3;
      if (remainder === 0) {
        return "col-lg-3 offset-lg-1";
      } else if (remainder === 2) {
        return "col-lg-4";
      } else {
        return "col-lg-3";
      }
    },
  },
};
</script>
<style scoped>
.avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 50%;
}
</style>