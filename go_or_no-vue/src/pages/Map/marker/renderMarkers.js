// 현재 선택된 마커 전역 상태(클릭 이미지 유지용)
let selectedMarker = null;

/**
 * 외부에서 선택 마커를 초기화할 수 있도록 export
 * → 지도 클릭 시나 현재 위치 이동 버튼에서 사용
 */

// 선택 마커의 클릭 이미지를 기본 이미지로 변경
export function resetSelectedMarker() {
  if(selectedMarker) {
    selectedMarker.setImage(selectedMarker.normalImage);  
    selectedMarker.currentState = "normal";
    selectedMarker = null;
  }
}

/**
 * 등록된 장소 리스트를 기반으로 마커를 생성해 지도에 표시.
 * (스프라이트 이미지 기반으로 기본/오버/클릭 상태를 구현)
 * 
 * @param {kakao.maps.Map} map - Kakao Map 인스턴스
 * @param {Array} places - 서버에서 받아온 장소 배열
 * @param {Function} onMarkerClick - 마커 클릭 시 콜백(상세보기, 오버레이 등)
 * @returns {Object} - { regMarkers } 생성된 등록 마커 배열
 */

export function renderMarkers(
  map,
  places,
  onMarkerClick = () => {},
) {
  const regMarkers = [];

  // 여러 마커 제어하기 관련 설정(기본, 클릭, 오버)
  const SPRITE_MARKER_URL = "/img/regMarker_sprites.png";// 스프라이트 마커 이미지 URL
  const spriteImageSize = new kakao.maps.Size(65, 120); // 전체 스프라이트 이미지의 크기

  // 기본 마커
  const normalSize = new kakao.maps.Size(50, 50); // 마커의 크기
  const normalOffset = new kakao.maps.Point(32, 45); // 마커의 기준좌표
  const normalOrigin = new kakao.maps.Point(0, 0);    // 기본 마커
  
  // 마우스 오버 마커
  const overSize = new kakao.maps.Size(50, 50); 
  const overOffset = new kakao.maps.Point(32, 50); 
  const overOrigin = new kakao.maps.Point(0, 0);   
  
  // 클릭 마커
  const clickSize = new kakao.maps.Size(50, 50);   
  const clickOffset = new kakao.maps.Point(32, 45);   
  const clickOrigin = new kakao.maps.Point(0, 65);
  
  // 장소별 마커 생성
  places.forEach( place => {

    // 각각의 마커 상태별 이미지 생성
    const normalImage = new kakao.maps.MarkerImage(
      SPRITE_MARKER_URL,
      normalSize,
      {
        offset: normalOffset,
        spriteOrigin: normalOrigin,
        spriteSize: spriteImageSize,
      },
    );

    const overImage = new kakao.maps.MarkerImage(
      SPRITE_MARKER_URL,
      overSize,
      {
        offset: overOffset,
        spriteOrigin: overOrigin,
        spriteSize: spriteImageSize,
      },
    );

    const clickImage = new kakao.maps.MarkerImage(
      SPRITE_MARKER_URL,
      clickSize,
      {
        offset: clickOffset,
        spriteOrigin: clickOrigin,
        spriteSize: spriteImageSize,
      },
    );

    // 마커 객체 생성 및 지도에 추가
    const marker = new kakao.maps.Marker({
      map,
      position: new kakao.maps.LatLng(place.latitude, place.longitude),
      title: place.title,
      image: normalImage,
    });

    // 현재 마커 상태 저장
    marker.normalImage = normalImage;
    marker.currentState = "normal";  // normal || click

    // 마우스 오버 이벤트 : 클릭 상태가 아니라면 오버 이미지 표시
    kakao.maps.event.addListener(marker, "mouseover", () => {
      if (marker.currentState !== "click") {
        marker.setImage(overImage);
      }
    });

    // 마우스 아웃 이벤트 : 클릭 상태가 아니라면 기본 이미지로 변환
    kakao.maps.event.addListener(marker, "mouseout", () => {
      // 현재 선택된 마커가 아니면 기본 이미지로 변경
      if (marker.currentState !== "click") {
        marker.setImage(normalImage);
      }
    });

    // 마우스 클릭 이벤트 : 이전 선택 마커 초기화 + 본인 클릭 이미지로 변경
    kakao.maps.event.addListener(marker, "click", () => {
      // 이전 마커가 있고, 지금 누른 마커와 다를때만 초기화
      if (selectedMarker && selectedMarker !== marker) {
        selectedMarker.setImage(selectedMarker.normalImage);
        selectedMarker.currentState = "normal";
      }
      marker.setImage(clickImage);
      marker.currentState = "click";
      selectedMarker = marker;
      
      onMarkerClick(marker, place);
    });

    regMarkers.push(marker);
  });
  return { regMarkers };
}