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
        <li class="nav-item">
          <router-link to="/stats" class="nav-link">
              <i class="ti-user"></i>
              <p>마이페이지</p>
            </router-link>
        </li>
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
export default {
  components: {
    TopNavbar,
    ContentFooter,
    DashboardContent,
    MobileMenu,
  },
  methods: {
    toggleSidebar() {
      if (this.$sidebar.showSidebar) {
        this.$sidebar.displaySidebar(false);
      }
    },
  },
};
</script>
