<!-- 대시보드 세부 메뉴 상단 -->
<template>
  <nav class="navbar navbar-expand-lg navbar-light">
    <div class="container-fluid">
      <a class="navbar-brand" href="#">{{ routeName }}</a>
      <button
        class="navbar-toggler navbar-burger"
        type="button"
        @click="toggleSidebar"
        :aria-expanded="$sidebar.showSidebar"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-bar"></span>
        <span class="navbar-toggler-bar"></span>
        <span class="navbar-toggler-bar"></span>
      </button>
      <!-- Navbar 우측 메뉴 -->
      <div class="collapse navbar-collapse">
        <ul class="navbar-nav ml-auto">
          <!-- Stats -->
          <li class="nav-item">
            <router-link to="/stats" class="nav-link">
              <i class="ti-user"></i>
              <p>마이페이지</p>
            </router-link>
          </li>
          <!-- 드롭다운 -->
          <!-- 개수 동적 코딩 -->
          <drop-down
            class="nav-item"
            title="알림"
            title-classes="nav-link"
            icon="ti-bell"
          >
            <a class="dropdown-item" href="#">Notification 1</a>
            <a class="dropdown-item" href="#">Notification 2</a>
            <a class="dropdown-item" href="#">Notification 3</a>
            <a class="dropdown-item" href="#">Notification 4</a>
            <a class="dropdown-item" href="#">Another notification</a>
          </drop-down>
          <!-- UI 색상 변경 -->
          <li class="nav-item">
            <a href="#" class="nav-link">
              <i class="ti-eye"></i>
              <p>보기 모드 변경</p>
            </a>
          </li>
          <!-- 로그인 안 한 사용자: 로그인 버튼 + 회원가입 버튼 -->
          <template v-if="!isLoggedIn">
            <li class="nav-item">
              <router-link to="/login" class="nav-link">
                <i class="ti-lock"></i>
                <p>로그인</p>
              </router-link>
            </li>
            <li class="nav-item signup">
              <router-link to="/signup" class="nav-link">
                <i class="ti-pencil-alt"></i>
                <p>회원가입</p>
              </router-link>
            </li>
          </template>

          <!-- 로그인 한 사용자: 로그아웃 버튼 -->
          <template v-else>
            <li class="nav-item">
              <a href="#" class="nav-link" @click.prevent="logout">
                <i class="ti-unlock"></i>
                <p>로그아웃</p>
              </a>
            </li>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>
<script>
import { isLoggedIn } from '../../utils/loginState';

export default {
  computed: {
    routeName() {
      const { name } = this.$route;
      return this.capitalizeFirstLetter(name);
    },
    isLoggedIn() {
      return isLoggedIn.value; // 항상 최신 로그인 상태
    },
  },
  data() {
    return {
      activeNotifications: false,
    };
  },
  methods: {
    capitalizeFirstLetter(string) {
      return string.charAt(0).toUpperCase() + string.slice(1);
    },
    toggleNotificationDropDown() {
      this.activeNotifications = !this.activeNotifications;
    },
    closeDropDown() {
      this.activeNotifications = false;
    },
    toggleSidebar() {
      this.$sidebar.displaySidebar(!this.$sidebar.showSidebar);
    },
    hideSidebar() {
      this.$sidebar.displaySidebar(false);
    },
    logout() {
      localStorage.removeItem('accessToken');
      isLoggedIn.value = false; // 전역 상태 false로 변경
      alert('로그아웃되었습니다.');
      this.$router.push('/');
    },
  },
};
</script>
<style></style>
