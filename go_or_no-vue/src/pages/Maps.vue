<template>
  <card class="card-map">
    <!-- ref: DOM 요소를 Vue에서 직접 접근 가능토록 함 -->
    <!-- ref로 설정한 DOM 요소는 JS에서 this.$refs.____로 접근 가능 -->
    <!-- 지도를 표시할 영역 -->
    <div class="map" ref="mapContainer"></div>
    <!-- 현재 위치로 이동 버튼 -->
    <button class="btn-current-location" @click="goToCurrentLocation">
      📍 현재 위치로 이동
    </button>
    <!-- 현재 선택된 위/경도 표시 -->
    <div id="clickLatlng">
      <!-- computed에서 계산된 값을 변수명으로 사용  -->
      클릭한 위치의 위도는 {{ selectedLat }} 이고, 경도는 {{ selectedLng }} 입니다.
    </div>
  </card>
</template>
  
  <!-- <script>(Javascript 역할): 데이터를 관리하거나, 로직(이벤트, API 요청 등)을 처리   -->
  <!--                      Vue 컴포넌트 인스턴스(this) 바인딩                            -->
  <!-- Vue는 컴포넌트를 생성할 때 각 객체(methods, data, computed 등)를 모두 읽고         -->
  <!-- 그 안의 요소들을 모두, 한꺼번에, 실행 순서 관계 없이 Vue 인스턴스(this)에 바인딩함 -->
  <!-- methods, data, computed에서 정의된 속성이나 메서드들에 대하여 this로 접근 가능     -->
  <!-- ref로 지정한 DOM 객체 참조도 this로 바인딩 가능                                    -->
  
<script>
import { createMap, setCurrentLocation } from "./Map/utils/map";
import { createUserMarker } from "./Map/marker/createUserMarker";
import { overlayController } from "./Map/overlay/overlayController";
import { getAllPlaces } from "./Map/place/getAllPlaces";
import { registerPlace } from "./Map/place/registerPlace";
import { renderMarkers } from "./Map/marker/renderMarkers";
import { createClickMarker } from "./Map/marker/createClickMarker";

export default {
  name: "Maps",
  data() {
    // 이 컴포넌트가 내부적으로 갖고 있는 상태(state) 정의.
    return {
      map: null, // Kakao Map 인스턴스
      userMarker: null, // 사용자 현재 위치 마커
      clickMarker: null, // 클릭으로 생성된 장소 등록 마커
      selectedLatLng: null, // 사용자가 선택한 좌표
      logoImage: require("@/assets/img/logo-base-image.png"), // 등록 이미지(로고이미지로 임의 설정)
      regMarkers: [], // 등록된 장소 마커 목록
      openInfoWindow: null, // 현재 열려 있는 인포윈도우
      savedMarkers: [], // 백엔드에서 받아온 장소 데이터
      registerOverlay: null, // 장소 등록용 오버레이 컨트롤러 인스턴스
    };
  },
  computed: {
    // data()를 기반으로 하는 계산된 속성 영역. 함수형태로 구현하지만 사용 시에는 변수 형태.
    // 화면에 바인딩 가능하며, 계산된 값이 자동으로 반영됨
    // 예: selectedLat, selectedLng는 selectedLatLng이 바뀌면 자동으로 계산되어 변수명으로 저장
    // 사용 시 : this.selectedLat(O)  this.selectedLat() (X)
    selectedLat() {
      return this.selectedLatLng?.getLat() ?? ""; // null 병합 연산자: a ?? b -> a가 null도 아니고 undefined도 아니면 a, 그 외의 경우에는 b를 반환
    },
    selectedLng() {
      return this.selectedLatLng?.getLng() ?? "";
    },
  },
  mounted() {
    // Vue 컴포넌트가 화면에 완전히 그려졌을 때 실행(Vue의 라이프사이클 훅)

    //초기 지도 생성
    const container = this.$refs.mapContainer;
    this.map = createMap(container);

    // 장소 목록 불러오기 및 렌더링
    this.fetchAllPlaces(); 

    // 사용자 현재 위치 마커 생성
    this.userMarker = createUserMarker(this.map, () => {
      if (this.selectedLatLng) {
        this.showRegisterOverlay();
      } else {
        alert("좌표 정보가 없습니다.");
      }
    });

    // 현재 위치 가져오기(선택된 위치는 초기화하지 않음)
    setCurrentLocation(this.map, this.userMarker, () => {});

    // 클릭 마커 생성
    this.clickMarker = createClickMarker(
      this.map,
      (mouseEvent) => mouseEvent.latLng,
      (latLng) => {
        this.selectedLatLng = latLng;
        this.map.panTo(latLng);
        this.registerOverlay.close();

        // 클릭 마커가 생기면 사용자 위치 마커는 숨김
        if (this.userMarker) {
          this.userMarker.setMap(null);
        }
        // 인포윈도우가 있다면, 닫기
        if (this.openInfoWindow) {
          this.openInfoWindow.close();
          this.openInfoWindow = null;
        }

        // 모든 등록 마커를 기본 이미지로 초기화
        if (this.regMarkers.length) {
          this.regMarkers.forEach((marker) => {
            marker.setImage(marker.normalImage);
          });
        }
      },
      () => {
        this.showRegisterOverlay();
      },
      () => this.registerOverlay.isVisible()
    );
    
    // 오버레이 컨트롤러 생성
    this.registerOverlay = overlayController("register");
  },
  methods: {
    // 장소 등록용 오버레이 표시
    showRegisterOverlay() {
      this.registerOverlay.show(
        this.map,
        this.selectedLatLng,
        this.logoImage,
        this.handleRegisterPlace,
        null
      );
    },

    // 장소 등록 API 호출
    async handleRegisterPlace() {
      // 선택한 좌표가 없으면 경고 후 리턴
      if (!this.selectedLatLng) {
        alert("좌표 정보가 없습니다.");
        return;
      }

      try {
        const success = await registerPlace(
          this.selectedLatLng.getLat(),
          this.selectedLatLng.getLng()
        );

        if (success) {
          alert("장소 등록 완료!");
          this.fetchAllPlaces();
        }
      } catch (error) {
        alert("등록에 실패했습니다.");
      }
    },

    // 장소 목록 API 호출
    async fetchAllPlaces() {
      try {
        this.savedMarkers = await getAllPlaces();
        this.renderSavedMarkers();
      } catch (error) {
        alert("장소 데이터를 불러오지 못했습니다.");
        console.error(error);
      }
    },

    // 등록된 장소 마커 렌더링
    renderSavedMarkers() {
      if (this.regMarkers.length) {
        // 기존 마커 제거 (지도에서 제거)
          this.regMarkers.forEach((m) => m.setMap(null));
          this.regMarkers = [];
        }

      const { regMarkers } = renderMarkers(
        this.map,
        this.savedMarkers,
        (marker, place) => {
          const infoWindow = new kakao.maps.InfoWindow({
            content: `<div style ="padding:5px;">${place.latitude}</div>`,
          });
          if (this.openInfoWindow) {
            this.openInfoWindow.close();
          }
          infoWindow.open(this.map, marker);
          this.openInfoWindow = infoWindow;
        }
      );

      this.regMarkers = regMarkers;
    },

    // 현재 위치로 이동 버튼 함수
    goToCurrentLocation() {
      setCurrentLocation(this.map, this.clickMarker, (latLng) => {
        this.selectedLatLng = latLng;
        
        // 현재 위치 마커 표시, 클릭 마커는 숨김
        this.userMarker.setMap(this.map);
        this.clickMarker?.setMap(null);

        // 기타 상태 초기화
        if (this.registerOverlay?.isVisible()) {
          this.registerOverlay.close();
        }
        if (this.openInfoWindow) {
          this.openInfoWindow.close();
          this.openInfoWindow = null;
        }
        if (this.regMarkers.length) {
          this.regMarkers.forEach((m) => m.setImage(m.normalImage));
        }
        window.selectedMarker = null;
      });
    },
  },
};
</script>
  
<style scoped>
.btn-current-location {
  bottom: 12px;
  right: 12px;
  padding: 8px 12px;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}
.btn-current-location:hover {
  background-color: #f2f2f2;
}
</style>