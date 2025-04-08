//vue-clickwawy 라이브러리에서 사용자의 클릭이 컴포넌트 밖으로 나갔을 때 동작하게 하는 디렉티브 임포트
import { directive as vClickOutside } from "vue-clickaway";

/**
 * You can register global directives here and use them as a plugin in your main Vue instance
 */

const GlobalDirectives = {
  // Vue.directive("click-outside", 디렉티브함수) : 전역 디렉티브 등록
  // 템플릿에서 v-click-outside로 바로 사용 가능
  install(Vue) {
    Vue.directive("click-outside", vClickOutside);
  },
};

export default GlobalDirectives;
