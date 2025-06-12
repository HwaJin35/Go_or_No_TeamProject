<!-- 대시보드 세부 메뉴 상단 -->
<template>
  <nav class="navbar navbar-expand-lg navbar-light">
    <div class="container-fluid">
      <a class="navbar-brand" href="#">{{ dynamicRouteName }}</a>
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
          <li v-if="isLoggedIn && remainingTime > 0" class="nav-item session-control-group">
            <div class="session-timer-progress-wrapper">
              <div class="timer-display">
                <i class="ti-timer"></i>
                <span class="timer-text">{{ remainingTimeFormatted }}</span>
              </div>
              <div class="progress-bar-container">
                <div
                  class="progress-bar-fill"
                  :style="{
                    width: remainingTimePercent + '%',
                    backgroundColor: getProgressBarColor(remainingTimePercent)
                  }"
                ></div>
              </div>
            </div>
            <button
              class="extend-btn fancy-extend-btn"
              @click="handleExtendSession"
              title="세션 시간 연장"
            >
              <span>{{ '세션 연장' }}</span>
            </button>
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

          <!-- 로그인 한 사용자: 마이페이지 + 로그아웃 버튼 -->
          <template v-else>
            <li class="nav-item">
              <router-link to="/me" class="nav-link">
                <i class="ti-user"></i>
                <p>마이페이지</p>
              </router-link>
            </li>
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
import { isLoggedIn, remainingTime, remainingTimeFormatted, remainingTimePercent, removeToken, extendSessionTime } from '@/utils/loginState';
import { watch } from 'vue';
import axiosInstance from "@/utils/axiosInstance";

export default {
  computed: {
    routeName() {
      const { name } = this.$route;
      return this.capitalizeFirstLetter(name);
    },
    isLoggedIn() {
      return isLoggedIn.value; // 항상 최신 로그인 상태
    },
    remainingTime() {
      return remainingTime.value; 
    },
    remainingTimeFormatted() {
      return remainingTimeFormatted.value; // 남은 시간 가져오기
    },
    remainingTimePercent() {
      return remainingTimePercent.value;
    },
    dynamicRouteName(){
      // console.log('현재 경로:', this.$route.path);
            if (this.$route.path === '/maps') {
        return this.nickname 
          ? `👋 안녕하세요 ${this.nickname}님!`
          : "📌 click on the map to drop a pin";
      }
      return this.routeName;
    }
  },
  mounted() {
    this.fetchUserInfo();
    // 사용자 정보 업데이트 이벤트 리스너 (회원정보 수정 시 사용)
    this.$root.$on('refreshUserInfo', this.fetchUserInfo);

    // 세션 타이머 상태 변화 감지
    this.watchTimer = watch(
      () => remainingTime.value,
      (newTime) => {
        // console.log('세션 시간 변화:', newTime);
        // 타이머가 시작되면 컴포넌트 강제 업데이트
        this.$forceUpdate();
      },
      { immediate : true } // 즉시 실행
    );
  },
  beforeDestroy() {
    this.$root.$off('refreshUserInfo', this.fetchUserInfo);

    if (this.watchTimer) {
      this.watchTimer();
    }
  },
    watch: {
    '$route'() {
      if (!this.userNickname) {
        this.fetchUserInfo();
      }
    }
  },
  data() {
    return {
      activeNotifications: false,
      nickname: null,
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
      const accessToken = localStorage.getItem('accessToken');
      if (!accessToken) {
        this.performLocalLogout();
        return;
      }

      // 로그아웃 요청
      axiosInstance.post('/api/logout', null, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })
      .then(() => {
        this.performLocalLogout();
      })
      .catch((error) => {
        console.error("서버 로그아웃 실패:", error);
        this.performLocalLogout(); // 서버 실패해도 클라이언트 로그아웃은 강제 진행
      });
    },
    performLocalLogout() {
      removeToken();
      this.nickname = null;
      isLoggedIn.value = false;
      this.$root.$emit('refreshUserInfo');
      alert('로그아웃 되었습니다.');
      this.$router.push('/');
    },
    async fetchUserInfo() {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        this.nickname = null;
        return;
      }
      try {
        const response = await axiosInstance.get("/api/users/me");
        
        this.nickname = response.data.nickname;
        // console.log('닉네임: ', this.nickname);
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error.response?.status || error.message);
        this.nickname = null;
      }
    },
    // 시간 연장 핸들러
    async handleExtendSession() {
      try {
        const success = await extendSessionTime();
        if (success) {
          console.log('세션 연장');
        } else {
          console.error('세션 연장 실패');
        }
      } catch (error) {
        console.error('세션 연장 오류:', error);
        alert('세션 연장 중 오류가 발생했습니다.');
      }
    },
    // 프로그레스 바 색상 결정
    getProgressBarColor(percent) {
      if (percent > 50) return '#4caf50'; // 초록색
      if (percent > 25) return '#ff9800'; // 주황색  
      return '#f44336'; // 빨간색
    }
  },
};
</script>
<style scoped>
/* --- 세션 타이머 및 연장 버튼 그룹 스타일 --- */
.session-control-group {
  display: flex; /* flexbox를 사용하여 내부 요소를 가로로 배열 */
  align-items: center; /* 세로 중앙 정렬 */
  gap: 10px; /* 세션 타이머/프로그레스와 버튼 사이 간격 */
  margin-right: 10px; /* 다른 메뉴 아이템과의 간격 */
}

/* 세션 타이머와 프로그레스 바 래퍼 */
.session-timer-progress-wrapper {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 8px 12px;
  min-width: 180px; /* 적절한 최소 너비 설정 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 부드러운 그림자 추가 */
  display: flex;
  flex-direction: column; /* 아이템들을 세로로 정렬 */
  gap: 4px; /* 타이머와 프로그레스 바 사이 간격 */
}

.timer-display {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333; /* 텍스트 색상 명확하게 지정 */
}

.timer-display i {
  font-size: 16px; /* 아이콘 크기 조정 */
  color: #555;
}

.timer-text {
  font-size: 14px; /* 텍스트 크기 조정 */
  font-weight: 600; /* 좀 더 두껍게 */
  color: #333;
  flex: 1;
}

/* 프로그레스 바 스타일 */
.progress-bar-container {
  width: 100%;
  height: 6px; /* 높이 약간 증가 */
  background-color: rgba(0, 0, 0, 0.1); /* 어두운 배경으로 대비 */
  border-radius: 3px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  transition: width 0.5s linear, background-color 0.3s ease;
  border-radius: 3px;
}

/* --- 연장 버튼 스타일 --- */
.extend-btn {
  border: none;
  padding: 8px 12px; /* 패딩 조정 */
  font-size: 13px; /* 글자 크기 조정 */
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: transform 0.3s ease, filter 0.5s ease;
  font-weight: bold;
  background: linear-gradient(to right, #007bff, #0154ad); /* 그라데이션 배경 */
  color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  border-radius: 10px; /* 둥근 모양 */
  padding: 8px 18px; /* 패딩 조정 */
}

.extend-btn:hover {
  filter: brightness(0.8); /* 밝기를 80%로 */
  box-shadow: 0 6px 12px rgba(0, 123, 255, 0.4); /* 호버 시 그림자 강화 */
  transform: translateY(-1px); /* 약간 위로 움직이는 효과 */
}
</style>