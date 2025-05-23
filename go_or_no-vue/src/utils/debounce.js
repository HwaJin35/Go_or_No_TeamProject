/**
 * debounce 함수 
 * 짧은 시간 동안 반복해서 호출되는 함수 실행을 지연시켜
 * 마지막 호출만 실행되도록 하는 유틸 함수
 * 
 * - 사용 예시 (Vue 컴포넌트에서)
 * import { debounce } from "@/utils/debounce";
 * 
 * created() {
 *  this.debounceSearch = debounce(this.searchFunction, 300);
 * },
 * methods: {
 *  searchFunction() {
 *  // 함수 구현
 *  }
 * }
 * 
 * <input @input="debounceSearch" />
 * -> 사용자가 빠르게 입력할 때, 마지막 입력 후 300ms가 지나야 searchFunction이 실행됨
 * 
 * @param {Function} func - 지연시켜 실행할 함수
 * @param {number} delay - 대기 시간(ms), 기본값은 300ms로 설정 
 * @returns {Funtion} - debounce가 적용된 새로운 함수
 */

export function debounce(func, delay=300) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => {
      func.apply(this, args);
    }, delay);
  }
}