<template>
  <!-- 외부에서 유입된 props들이 내부의 요소로 활용 -->
  <div
    class="sidebar"
    :data-background-color="backgroundColor"
    :data-active-color="activeColor"
  >
    <!--
            Tip 1: you can change the color of the sidebar's background using: data-background-color="white | black | darkblue"
            Tip 2: you can change the color of the active button using the data-active-color="primary | info | success | warning | danger"
        -->
    <!-- -->
    <div class="sidebar-wrapper" id="style-3">
      <div class="logo">
        <a href="/" class="simple-text">
          <div class="logo-img">
            <img src="@/assets/img/logo-base-image.png" alt="" />
          </div>
          {{ title }}
        </a>
      </div>
      <slot> </slot>
      <ul class="nav">
        <!--By default vue-router adds an active class to each route link. This way the links are colored when clicked-->
        <slot name="links">
          <sidebar-link
            v-for="(link, index) in sidebarLinks"
            :key="index"
            :to="link.path"
            :name="link.name"
            :icon="link.icon"
          >
          </sidebar-link>
        </slot>
      </ul>
      <moving-arrow :move-y="arrowMovePx"> </moving-arrow>
    </div>
  </div>
</template>
<script>
import MovingArrow from "./MovingArrow.vue";
import SidebarLink from "./SidebarLink";
export default {
  //외부 데이터 유입
  props: {
    title: {
      type: String,
      default: "여기 가도 될까?",
    },
    backgroundColor: {
      type: String,
      default: "black",
      validator: (value) => {
        let acceptedValues = ["white", "black", "darkblue"];
        return acceptedValues.indexOf(value) !== -1;
      },
    },
    activeColor: {
      type: String,
      default: "success",
      validator: (value) => {
        let acceptedValues = [
          "primary",
          "info",
          "success",
          "warning",
          "danger",
        ];
        return acceptedValues.indexOf(value) !== -1;
      },
    },
    sidebarLinks: {
      type: Array,
      default: () => [],
    },
    autoClose: {
      type: Boolean,
      default: true,
    },
  },
  // 하위 컴포넌트에 전달할 값
  // 하위 컴포넌트에서 inject로 사용 가능
  provide() {
    return {
      autoClose: this.autoClose,
      addLink: this.addLink,
      removeLink: this.removeLink,
    };
  },
  // 자식 컴포넌트 등록
  components: {
    MovingArrow,
    SidebarLink,
  },
  computed: {
    /**
     * Styles to animate the arrow near the current active sidebar link
     * @returns {{transform: string}}
     */
    // 화살표의 Y축 위치 계산 (사이드메뉴 단위 높이 * 인덱스)
    arrowMovePx() {
      return this.linkHeight * this.activeLinkIndex;
    },
  },
  //컴포넌트 내부 상태
  data() {
    return {
      linkHeight: 65, // 사이드메뉴 하나의 높이
      activeLinkIndex: 0, // 현재 활성화 된 링크의 인덱스
      windowWidth: 0,
      isWindows: false,
      hasAutoHeight: false,
      links: [],
    };
  },
  //컴포넌트의 기능 함수
  methods: {
    // 모든 SidebarLink 중 isActive()가 true인 것을 찾아서 그에 해당하는 index를 acitveLinkIndex에 넣음
    findActiveLink() {
      this.links.forEach((link, index) => {
        if (link.isActive()) {
          this.activeLinkIndex = index;
        }
      });
    },
    //slot에 들어온 링크 컴포넌트를 link 배열에 추가
    addLink(link) {
      const index = this.$slots.links.indexOf(link.$vnode);
      this.links.splice(index, 0, link);
    },
    removeLink(link) {
      const index = this.links.indexOf(link);
      if (index > -1) {
        this.links.splice(index, 1);
      }
    },
  },
  // 마운트 후 실행될 로직
  // Vue 라우터의 $route 객체를 감시하여 라우트가 바뀔 때마다 활성메뉴를 찾아서 업데이트
  mounted() {
    this.$watch("$route", this.findActiveLink, {
      immediate: true,  // 마운드 직후에도 한 번 실행
    });
  },
};
</script>
<style></style>