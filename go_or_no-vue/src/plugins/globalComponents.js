// 컴포넌트 임포트
import { FormGroupInput, Card, DropDown, Button } from "../components/index";
/**
 * You can register global components here and use them as a plugin in your main Vue instance
 */

const GlobalComponents = {
  // 커스텀 플러그인 컴포넌트 전역 등록
  // Vue.component("태그이름", 컴포넌트) 형식
  // 어느 컴포넌트에서든 <태그이름> 으로 해당 컴포넌트 사용이 가능해짐
  install(Vue) {
    Vue.component("fg-input", FormGroupInput);
    Vue.component("drop-down", DropDown);
    Vue.component("card", Card);
    Vue.component("p-button", Button);
  },
};

export default GlobalComponents;
