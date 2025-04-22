// 지도 초기 위치 설정을 위한 기본 좌표(서울 시청 기준)
export const defaultLatLng = new kakao.maps.LatLng(37.5665, 126.978);

// 카카오 맵을 초기화하는 함수
export function createMap(container, level = 3) {
  return new kakao.maps.Map(container, {
    center: defaultLatLng,
    level,
  });
}

// 현재 위치를 받아 지도 및 마커를 이동시키는 함수
export function setCurrentLocation(map, marker, onLocationUpdate) {
  if (!navigator.geolocation) {
    // geolocation을 사용할 수 없을 경우에도 기본 위치로 설정
    alert("위치 정보를 불러올 수 없습니다. 기본 좌표로 지도를 로드합니다.");
    map.panTo(defaultLatLng);
    marker.setPosition(defaultLatLng);
    if (onLocationUpdate) onLocationUpdate(defaultLatLng);
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      // console.log("위치:", position);
      var currentLat = position.coords.latitude; //위도
      var currentLng = position.coords.longitude; // 경도
      var currentPosition = new kakao.maps.LatLng(currentLat, currentLng); // 현재 위도 경도를 currentPosition 변수에 저장
      
      map.panTo(currentPosition); // 지도 중심 이동
      marker.setPosition(currentPosition); // 마커 위치 이동
      if (onLocationUpdate) {
        onLocationUpdate(currentPosition); // 선택된 위치로 등록 초기화
      }
    },
    (error) => {
      console.error("위치 정보 오류:", error.code, error.message);

      // 사용자가 위치 정보를 허용하지 않았을 시 오류 처리
      if (error.code === error.PERMISSION_DENIED) {
        var deniedMessage =
          "위치 정보가 차단되어 있어 현재 위치를 불러올 수 없습니다.\n" +
          "브라우저 설정에서 위치 권한을 허용해 주세요.";
        alert(deniedMessage);
        console.log("위치정보 조회 실패:", error);
      }
      // 위치 정보를 가져오지 못하면 기본 위치로 설정
      map.setCenter(defaultLatLng);
      marker.setPosition(defaultLatLng);
      if (onLocationUpdate) onLocationUpdate(defaultLatLng);
    },
  );
}