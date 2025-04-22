
// 등록 마커 외에 선택된 마커를 초기화하는 유틸
import { resetSelectedMarker } from "./renderMarkers";

/**  JSDoc
 * 사용자 클릭 좌표에 등록 마커(clickMarker)를 생성하고 지도에 표시.
 * - 맵 클릭 시 해당 위치로 이동
 * - 등록 오버레이가 열려 있다면 이동 방지
 * - 선택된 마커 초기화
 * 
 * @param {kakao.maps.Map} map - Kakao Map 객체
 * @param {Function} getPosition - 클릭 시 좌표를 반환하는 함수 (mouseEvent.latLng)
 * @param {Function} onMove - 마커 위치 이동 시 실행할 콜백
 * @param {Function} onClick - 마커 자체를 클릭했을 때 실행할 콜백 (오버레이 열기 등)
 * @param {Function} isOverlayVisible - 등록 오버레이가 열려있는지 확인하는 함수
 * @returns {kakao.maps.Marker} - 클릭 마커 인스턴스 반환
 */

export function createClickMarker(
  map,
  getPosition,
  onMove = () => {},
  onClick = () => {},
  isOverlayVisible = () => false,
) {
  // 클릭 마커 생성(초기 위치는 지도 중심)
  const marker = new kakao.maps.Marker({
    position: map.getCenter(),
  });
  marker.setMap(map);   // 최초엔 보이지 않지만 클릭 시 위치 이동

  // 지도 클릭 시 마커 이동
  kakao.maps.event.addListener(map, "click", (mouseEvent) => {
    if (isOverlayVisible()) return;   // 오버레이 열려 있으면 무시
    
    const latLng = getPosition(mouseEvent); // 클릭 위치 좌표
    marker.setMap(map);                     // 클릭 시 마커 표시
    marker.setPosition(latLng);             // 마커 위치 이동
    onMove(latLng);                         // 등록 좌표 갱신

    resetSelectedMarker(); 
  });

  // 마커 클릭 시 오버레이 표시 등 콜백 호출
  kakao.maps.event.addListener(marker, "click", () => {
    onClick(); // 마커 클릭 시 오버레이 표시
  });

  return marker;
}