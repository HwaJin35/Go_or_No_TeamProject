/**
 * 사용자 현재 위치 마커를 지도에 표시하고, 마커 클릭 시 오버레이를 표시하도록 설정
 * 
 * 사용자의 현재 위치를 최초에 받아올 때만 사용되며,
 * 이후 클릭을 통한 장소 선택에는 사용되지 않음
 * 
 * @param {kakao.maps.Map} map - Kakao Map 인스턴스
 * @param {Function} onClick - 마커 클릭 시 호출되는 콜백 함수 (보통 오버레이 표시)
 * @returns {kakao.maps.Marker} - 생성된 사용자 위치 마커
 */

export function createUserMarker(map, onClick) {
  // 사용자 현재 위치 마커 생성
  const imageSrc = "/img/userMarker.png";         // 사용자 위치 마커 이미지 경로
  const imageSize = new kakao.maps.Size(20, 20);  // 마커 크기
  const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize); 

  const marker = new kakao.maps.Marker({
    position: map.getCenter(),
    image: markerImage,
  });

  marker.setMap(map);           // 지도에 마커 표시
  marker.isUserMarker = true; // 사용자 마커임을 구분

  // 사용자 마커 클릭 시 등록 오버레이 표시
  kakao.maps.event.addListener(marker, "click", () => {
    if (typeof onClick === "function") {
      onClick();
    }
  });

  return marker;
}
