import { registerOverlayContent } from "./content/registerOverlayContent";
// 추후 오버레이 추가 시 import

/**
 * 오버레이 타입에 따라 해당 오버레이 콘텐츠를 반환.
 * 팩토리 패턴을 적용하여 확장 가능한 구조.
 *
 * @param {string} type - 오버레이 유형 ("register", "detail" 등)
 * @param {object} options - 해당 콘텐츠 생성에 필요한 옵션
 * @returns {HTMLElement|null} 생성된 오버레이 콘텐츠 DOM 요소
 */

export function createOverlayContent(type, props) {
  switch(type) {
    // 장소 등록용 오버레이 콘텐츠 반환
    case "register" :
      return registerOverlayContent(props);

    // 추후 오버레이 추가 생성 시 case문 추가
    // case "detail":
    //   return detailOverlayContent(options);
    
    default:
      console.log("지원하지 안흔 오버레이 타입: ", type);
      return null;
  }
}