/**
 * 장소 등록용 오버레이 콘텐츠를 생성
 * - innerHTML로 작성한 마커 오버레이 UI 반환
 * - 외부에서 콜백을 주입받아 버튼 이벤트에 연결함
 *
 * @param {Object} options - 오버레이 생성 시 전달되는 옵션
 * @param {string} options.logoImage - 오버레이에 표시할 이미지 URL
 * @param {Function} options.onRegister - 등록 버튼 클릭 시 실행할 함수
 * @param {Function} options.onCancel - 취소 또는 닫기 버튼 클릭 시 실행할 함수
 * @returns {HTMLElement} Kakao CustomOverlay에 삽입될 DOM 요소
 */

export function registerOverlayContent({ logoImage, onRegister, onCancel }) {
  // 오버레이 컨테이너 div 생성
  const container = document.createElement("div");
  container.className = "kakao-overlay-wrap";

  // 오버레이 내부 HTML 구성 (카카오맵에서는 React/Vue Template을 사용할 수 없기 때문에 직접 작성)
  container.innerHTML = `
    <div class="info">
      <div class="title">
        장소 등록
        <div class="close" title="닫기"></div>
      </div>
      <div class="body">
        <div class="img">
          <img src="${logoImage}" width="73" height="70" alt="장소 이미지" />
        </div>
        <div class="desc">
          <div class="ellipsis">선택한 위치에 장소를 등록하시겠습니까?</div>
          <div style="margin-top: 10px; display: flex; gap: 10px;">
            <button class="register-btn" style="padding: 5px 10px; cursor: pointer;">등록</button>
            <button class="cancel-btn" style="padding: 5px 10px; cursor: pointer;">취소</button>
          </div>
        </div>
      </div>
    </div>
  `;

  // 이벤트 연결: 등록/ 취소/ 닫기 버튼
  container.querySelector(".register-btn").addEventListener("click", onRegister);
  container.querySelector(".cancel-btn").addEventListener("click", onCancel);
  container.querySelector(".close").addEventListener("click", onCancel);

  return container;
}
