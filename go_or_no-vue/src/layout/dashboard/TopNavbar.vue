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
          <!-- 세션 타이머 -->
          <li v-if="isLoggedIn" class="nav-item session-timer-item">
            <a href="#" class="nav-link disabled">
              <i class="ti-timer"></i>
              <p> 세션 종료까지 {{ remainingTimeFormatted }}</p>
              <div class="progress-bar-container">
                <div class="progress-bar-fill" :style="{ width: remainingTimePercent + '%' }"></div>
              </div>
            </a>
          </li>
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
import { isLoggedIn, remainingTime, remainingTimeFormatted, remainingTimePercent, removeToken } from '@/utils/loginState';
import { watch } from 'vue';

export default {
  computed: {
    routeName() {
      const { name } = this.$route;
      return this.capitalizeFirstLetter(name);
    },
    isLoggedIn() {
      return isLoggedIn.value; // 항상 최신 로그인 상태
    },
    remainingTimeFormatted() {
      return remainingTimeFormatted.value; // 남은 시간 가져오기
    },
    remainingTimePercent() {
      return remainingTimePercent.value;
    },
  },
  mounted() {
    watch(remainingTime, (newTime) => {
      if(newTime === 0 ) {
        alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
        this.$router.push('/');
      }
    });
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
      removeToken();
      alert('로그아웃되었습니다.');
      this.$router.push('/');
    },
  },
};
</script>
<style scoped>
.session-timer {
  margin: 5px 0;
}

.timer-text {
  font-size: 14px;
  margin-bottom: 4px;
  text-align: center;
}

.progress-bar-container {
  width: 100%;
  height: 6px;
  margin-top: 8px;
  background-color: #eee;
  border-radius: 5px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background-color: #4caf50; /* 초록색 */
  transition: width 0.5s linear;
}
</style>
