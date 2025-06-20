<template>
  <div class="wrapper">
    <!-- 사이드바 메뉴 -->
    <side-bar>
      <template slot="links">
        <sidebar-link to="/maps" name="Map" icon="ti-map-alt" />
        <sidebar-link to="/my-place" name="나의 장소" icon="ti-pin-alt" />
        <sidebar-link to="/notice" name="공지사항" icon="ti-receipt" />
        <sidebar-link to="/community" name="지역별 커뮤니티" icon="ti-view-grid" />
        <sidebar-link
        to="/popularity"
        name="인기순 정렬"
        icon="ti-exchange-vertical"
        />
        <sidebar-link to="/favorites" name="즐겨찾기(수정 및 네비 연동)" icon="ti-star" />
        <sidebar-link to="/message" name="실시간 메시지" icon="ti-themify-favicon-alt" />
        <sidebar-link to="/dashboard" name="관리자 페이지" icon="ti-dashboard" />
        <!-- <sidebar-link to="/stats" name="User Profile(네비로 이동)" icon="ti-user" /> -->
        <sidebar-link to="/typography" name="Typography(삭제 예정)" icon="ti-text" />
        <sidebar-link to="/icons" name="Icons(삭제 예정)" icon="ti-pencil-alt2" />
        <sidebar-link to="/notifications" name="Notifications(삭제 예정)" icon="ti-bell" />
      </template>
      <!-- 모바일 환경 사이드바 메뉴 -->
      <mobile-menu>
        <!-- 알림 개수 동적 코딩 -->
        <drop-down
          class="nav-item"
          title="알림"
          title-classes="nav-link d-flex align-items-center gap-1"
          icon="ti-bell"
        >
          <a class="dropdown-item">Notification 1</a>
          <a class="dropdown-item">Notification 2</a>
          <a class="dropdown-item">Notification 3</a>
          <a class="dropdown-item">Notification 4</a>
          <a class="dropdown-item">Another notification</a>
        </drop-down>
        <li class="nav-item">
          <a class="nav-link">
            <i class="ti-eye"></i>
            <p>보기 모드 변경</p>
          </a>
        </li>
        <!-- 로그인 안 한 경우: 로그인, 회원가입 버튼 -->
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

        <!-- 로그인 한 경우: 마이페이지, 로그아웃 버튼 -->
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

        <li class="divider"></li>
      </mobile-menu>
    </side-bar>
    <!-- 대시보드 메인 화면 레이아웃 -->
    <div class="main-panel">
      <top-navbar></top-navbar>

      <dashboard-content @click.native="toggleSidebar"> </dashboard-content>

      <content-footer></content-footer>
    </div>
  </div>
</template>
<style lang="scss"></style>
<script>
import TopNavbar from "./TopNavbar.vue";
import ContentFooter from "./ContentFooter.vue";
import DashboardContent from "./Content.vue";
import MobileMenu from "./MobileMenu";
import { isLoggedIn, removeToken } from "../../utils/loginState";
import axiosInstance from "../../utils/axiosInstance";
export default {
  components: {
    TopNavbar,
    ContentFooter,
    DashboardContent,
    MobileMenu,
  },
  computed: {
    isLoggedIn() {
      return isLoggedIn.value; // 전역 상태 참조
    }
  },
  methods: {
    toggleSidebar() {
      if (this.$sidebar.showSidebar) {
        this.$sidebar.displaySidebar(false);
      }
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
    }
  },
};
</script>
