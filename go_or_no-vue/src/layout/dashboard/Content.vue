<template>
  <div class="['content', isFullMap? 'full-map-layout' : '']">
    <div class="container-fluid">
      <!-- 페이지가 변경될 때 페이드 효과 적용 -->
      <transition name="fade" mode="out-in">
        <!-- your content here -->
        <!-- 현재 선택된 라우트에 따라 해당 페이지 컴포넌트 동적 로드 -->
        <router-view></router-view>
      </transition>
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    isFullMap() {
      return this.$route.meta.fullMap === true;
    }
  }
};
</script>
<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.1s;
}

.fade-enter,
  .fade-leave-to
  /* .fade-leave-active in <2.1.8 */ {
  opacity: 0;
}
.full-map-layout {
  padding: 0 !important;
  margin: 0 !important;
  height: 100vh;
}
</style>
