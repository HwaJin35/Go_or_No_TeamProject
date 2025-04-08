import Sidebar from "./SideBar.vue";
import SidebarLink from "./SidebarLink";

const SidebarStore = {
  showSidebar: false,
  sidebarLinks: [],
  displaySidebar(value) {
    this.showSidebar = value;
  },
};

const SidebarPlugin = {
  install(Vue) {
    let app = new Vue({  // vue 인스턴스 생성
      data: {           // 인스턴스에 SidebarStore를 data로 연결 -> 반응형 시스템으로 사용 가능해짐
        sidebarStore: SidebarStore,
      },
    });

    // sidebarStore 전역 등록
    Vue.prototype.$sidebar = app.sidebarStore;
    // Sidebar, SidebarLink 전역 컴포넌트 등록
    Vue.component("side-bar", Sidebar);
    Vue.component("sidebar-link", SidebarLink);
  },
};

export default SidebarPlugin;
