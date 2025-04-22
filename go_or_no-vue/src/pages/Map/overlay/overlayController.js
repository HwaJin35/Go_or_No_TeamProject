// 오버레이 인스턴스 설정
import { createOverlayContent } from "./overlayContentFactory";

/**
 * 오버레이 타입에 따라 해당 오버레이 인스턴스를 생성 및 관리.
 * 팩토리 기반의 오버레이 컨트롤러 함수.
 *
 * @param {string} type - 오버레이 타입 ("register", "detail" 등)
 * @returns {object} show, close 등 오버레이 제어 메서드 포함 컨트롤러 객체
 *
 * 사용 예시:
 * const registerOverlay = overlayController("register")
 * registerOverlay.show(map, position, logoImage, onRegister, onCancel)
 */

// Maps.vue에서 사용 시 아래와 같이 사용
// this.registerOverlay = overlayController("register")
// this.detailOverlay = overlayController("detail")
export function overlayController(type = "register") {
  let overlay = null;     // 현재 오버레이 인스턴스
  let isVisible = false;  // 오버레이 표시 상태

  /**
   * 오버레이 인스턴스 생성 함수
   * - 팩토리에서 콘텐츠 생성
   * - 등록/취소 버튼 이벤트 등록
   * 오버레이 타입에 따라 기본 props 외 추가 데이터 전달할 수 있도록 전개연산자 사용
   * @param {object} args - 오버레이 생성에 필요한 데이터 + extra
   */

  function create({ position, logoImage, onRegister, onCancel, ...rest }) {
    const overlayContent = createOverlayContent(type, {
      logoImage,
      onRegister: () => {
        onRegister();
        close(); //  등록 후 자동으로 닫기
      },
      onCancel: () => {
        onCancel?.(); // Optional : onCancel이 존재할 경우에만 실행
        close();
      },
      ...rest,    // 추가 데이터 전달 가능
    });

    overlay = new kakao.maps.CustomOverlay({
      map: null,
      position,
      content: overlayContent,
      yAnchor: 1,
    });

    return overlay;
  }

    /**
   * 오버레이 표시 함수
   * - 이미 열려 있으면 닫고 새로 열기
   * - create() 내부에서 CustomOverlay 생성
   */

  function show(map, position, logoImage, onRegister, onCancel, extra = {}) {
    if (overlay) {
      close(); // 이미 열려 있으면 닫고 새로운 오버레이 표시
    }
    overlay = create({ position, logoImage, onRegister, onCancel, ...extra });
    overlay.setMap(map);
    overlay.setPosition(position);
    isVisible = true;
  }

  /**
   * 오버레이 닫기 함수
   * - 지도에서 제거
   * - 상태 리셋
   */

  function close() {
    if (overlay) {
      overlay.setMap(null);
      overlay = null;
    }
    isVisible = false;
  }


  return {
    show,
    close,
    getOverlay: () => overlay,
    isVisible: () => isVisible,
  };
}
