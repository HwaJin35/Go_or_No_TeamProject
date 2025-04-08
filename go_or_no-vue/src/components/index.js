import FormGroupInput from "./Inputs/formGroupInput.vue";

import DropDown from "./Dropdown.vue";
import PaperTable from "./PaperTable.vue";
import Button from "./Button";

import Card from "./Cards/Card.vue";
import ChartCard from "./Cards/ChartCard.vue";
import StatsCard from "./Cards/StatsCard.vue";

import SidebarPlugin from "./SidebarPlugin/index";

// 컴포넌트 모음 객체(components) 생성
let components = {
  FormGroupInput,
  Card,
  ChartCard,
  StatsCard,
  PaperTable,
  DropDown,
  SidebarPlugin,
};

// 기본 export -> components 객체를 통째로 내보냄
// import 예시 : import components from "./components";
export default components;

// 개별 export -> 각 컴포넌트의 이름을 지정해서 내보냄, 특정 컴포넌트만 불러올 수 있음
// import 예시 : import { FormGroupInput } from "./components";
export {
  FormGroupInput,
  Card,
  ChartCard,
  StatsCard,
  PaperTable,
  DropDown,
  Button,
  SidebarPlugin,
};
