<!-- <template>(HTML 역할) : 사용자에게 보여줄 UI 구조를 작성(HTML 태그 기반) -->
<template>
  <card class="card-map">
    <!-- ref: DOM 요소를 Vue에서 직접 접근 가능토록 함 -->
    <!-- ref로 설정한 DOM 요소는 JS에서 this.$refs.____로 접근 가능 -->
    <div class="map" ref="mapContainer"></div> <!-- 이 안에 지도가 들어옴 -->
    <div id="clickLatlng"> <!-- 지도에서 클릭한 좌표를 보여주는 영역 -->
      <!-- computed에서 계산된 값을 변수명으로 사용  -->
      클릭한 위치의 위도는 {{ selectedLat }} 이고, 경도는 {{ selectedLng }} 입니다.
    </div>

    <!-- Vue 내부에서 오버레이로 활용할 엘리먼트 (ref로 접근됨) -->
    <!-- HTML 내에 오버레이의 내용을 숨겨놓고 JS에서 this.$refs.overlayContent를 카카오 맵의 오버레이 콘텐츠로 전달 -->
    <!-- Vue에서 외부 라이브러리를 사용하는 경우에 주로 ref를 이용하여 DOM 객체를 참조하는 방식을 사용 -->
    <div ref="overlayContent" class="wrap" style="display: none" @click.stop>
      <div class="info">
        <div class="title">
          장소 등록
          <div class="close" @click="closeOverlay" title="닫기"></div>
        </div>
        <div class="body">
          <div class="img">
            <img :src="logoImage" width="73" height="70" alt="장소 이미지" />
          </div>
          <div class="desc">
            <div class="ellipsis">선택한 위치에 장소를 등록하시겠습니까?</div>
            <div style="margin-top: 10px; display: flex; gap: 10px;">
              <button class="register-btn" style="padding: 5px 10px; cursor: pointer;" @click="registerPlace">등록</button>
              <button class="cancel-btn" style="padding: 5px 10px; cursor: pointer;" @click="closeOverlay">취소</button>
            </div>
          </div>
        </div>
      </div>
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
import axios from "axios";

export default {
  name: "Maps",
  data() {                    // 이 컴포넌트가 내부적으로 갖고 있는 상태(state) 정의. 값이 바뀌면 반응형 시스템에 따라 화면에 자동 반영
    return {
      map: null,              // Kakao Map 객체
      marker: null,           // 마커 객체
      customOverlay: null,    // 사용자 정의 오버레이 객체
      selectedLatLng: null,   // 클릭한 위치의 좌표 객체
      logoImage: require("@/assets/img/logo-base-image.png"), // 등록 이미지(로고이미지로 임의 설정)
      regMarkerImageSrc: "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png" ,  // 등록된 장소 마커 이미지 소스
      regMarkers: [],         // 지도에 표시할 등록 마커들
      openInfoWindow: null,   // 현재 열려 있는 인포윈도우
      savedMarkers : [],      // 백엔드에서 받아온 저장용 마커들
    };
  },
  computed: {                 // data()를 기반으로 하는 계산된 속성 영역. 함수형태로 구현하지만 사용 시에는 변수 형태.
                              // 화면에 바인딩 가능하며, 계산된 값이 자동으로 반영됨
                              // 예: selectedLat, selectedLng는 selectedLatLng이 바뀌면 자동으로 계산되어 변수명으로 저장
                              // 사용 시 : this.selectedLat(O)  this.selectedLat() (X)
    selectedLat() {
      return this.selectedLatLng?.getLat() ?? '';    // null 병합 연산자: a ?? b -> a가 null도 아니고 undefined도 아니면 a, 그 외의 경우에는 b를 반환
    },
    selectedLng() {
      return this.selectedLatLng?.getLng() ?? '';
    },
  },
  mounted() {     // Vue 컴포넌트가 화면에 완전히 그려졌을 때 실행(Vue의 라이프사이클 훅)
    // Kakao Maps API가 로드되었는지 확인 후 맵 초기화
    if (window.kakao && window.kakao.maps) {
      this.initMap();
    } else {
      window.kakao.maps.load(this.initMap);
    }
    this.fetchMarkers();  // 저장된 마커 불러오기
  },
  methods: {       
    // 지도를 생성하고 초기 마커, 오버레이, 이벤트 리스너를 설정하는 메서드
    initMap() {
      // ref="mapContainer"로 참조한 DOM 요소
      const container = this.$refs.mapContainer;
      const defaultLatLng = new kakao.maps.LatLng(37.5665, 126.978);  // 기본 좌표 설정(서울 시청 근처)
      const options = {
        // 지도의 중심 좌표(기본 좌표로 설정)
        center: defaultLatLng,
        // 줌 레벨(숫자가 적을수록 더 확대됨)
        level: 3,
      };

      // 지도를 생성한 후 this.map에 저장
      this.map = new kakao.maps.Map(container, options);

      // 등록된 장소의 마커 불러오기
      this.fetchMarkers();

      // 마커 생성 및 클릭 이벤트 설정 초기화
      this.initMarker();

      // 오버레이 요소 초기화
      this.initOverlay();

      // 지도 클릭 이벤트 설정
      this.addMapClickListener();
      
      // 현재 위치로 지도 중심 이동
      // geolocation 사용 가능 확인
      if(navigator.geolocation){
        // GeoLocation을 이용해서 접속 위치를 받아옴
        navigator.geolocation.getCurrentPosition(
          position => {
            var currentLat = position.coords.latitude; //위도
            var currentLng = position.coords.longitude; // 경도
            var currentPosition = new kakao.maps.LatLng(currentLat, currentLng); // 현재 위도 경도를 currentPosition 변수에 저장
            this.map.setCenter(currentPosition);        // 지도 중심 이동
            this.marker.setPosition(currentPosition);   // 마커 위치 이동
            this.selectedLatLng = currentPosition;      // 선택된 위치로 등록 초기화
          }, error => {
            // 사용자가 위치 정보를 허용하지 않았을 시 오류 처리
            if(error.code === error.PERMISSION_DENIED) {
              var deniedMessage = "위치 정보가 차단되어 있어 현재 위치를 불러올 수 없습니다.\n"
                                + "브라우저 설정에서 위치 권한을 허용해 주세요.";
              alert(deniedMessage);
              console.log("위치정보 조회 실패:", error);
            }
            // 위치 정보를 가져오지 못하면 기본 위치로 설정
          
            this.map.setCenter(defaultLatLng);
            this.marker.setPosition(defaultLatLng);
            this.selectedLatLng = defaultLatLng;
        }
      );
    } else { // geolocation을 사용할 수 없을 경우에도 기본 위치로 설정
      alert("위치 정보를 불러올 수 없습니다. 기본 좌표로 지도를 로드합니다.");
      this.map.setCenter(defaultLatLng);
      this.marker.setPosition(defaultLatLng);
      this.selectedLatLng = defaultLatLng;
    } 
  },

    // 마커 생성 및 클릭 시 오버레이 표시 기능 메서드
    initMarker() {
      this.marker = new kakao.maps.Marker({
        position: this.map.getCenter(),   // 초기 마커 위치를 지도의 중심좌표로 설정
      });
      this.marker.setMap(this.map);  // 마커를 지도에 표시

      // 마커 클릭 시 커스텀 오버레이 표시
      kakao.maps.event.addListener(this.marker, "click", () => {
        if (this.selectedLatLng) {
          this.showOverlay();   // 선택된 좌표가 있을 경우 오버레이를 보여줌
        } else {
          alert("좌표 정보가 없습니다.");
        }
      });
    },

    // 장소 등록용 커스텀 오버레이 초기화
    initOverlay() {
      this.customOverlay = new kakao.maps.CustomOverlay({
        content: this.$refs.overlayContent, // ref="overlayContent"인 DOM 요소 참조
        map: null, // 처음에는 숨김 상태
        position: null, // 오버레이 위치
        yAnchor: 1, // 마커 위에 위치하도록 설정
      });
    },

    // 지도 클릭 이벤트 리스너 등록
    addMapClickListener() {
      kakao.maps.event.addListener(this.map, "click", (mouseEvent) => {
        // ref="overlayContent"로 참조한 DOM 요소
        const overlayEl = this.$refs.overlayContent;

        // overalayContent가 보여지고 있다면 클릭 이벤트 발생하지 않고 리턴
        if(overlayEl && overlayEl.style.display === "block") return;

        const latLng = mouseEvent.latLng; // 클릭한 위치의 좌표 객체
        this.selectedLatLng = latLng; // 좌표 정보를 Vue 데이터에 저장

        this.map.panTo(latLng); // 클릭한 위치로 지도의 중심을 옮김
                                // 지도영역의 경계 쪽에 오버레이 생성 시 맵 영역 밖의 부분은 짤리는 문제 때문에 설정(유지?유지!)
        this.marker.setPosition(latLng);  // 마커 위치 이동
        this.closeOverlay();  // 기존 오버레이 닫기
        
        // 인포윈도우가 있다면, 닫기
        if (this.openInfoWindow) {   
          this.openInfoWindow.close();
          this.openInfoWindow = null;
        }
      });
    },

    // 오버레이 표시(마커 클릭 시 실행)
    showOverlay() {
      if (!this.selectedLatLng) return; 

      this.customOverlay.setPosition(this.selectedLatLng); // 오버레이 위치 설정
      this.customOverlay.setMap(this.map);  // 지도에 오버레이 표시
      this.$refs.overlayContent.style.display = "block"; // DOM 요소 직접 보이게 설정
    },

    // 오버레이 닫기 (지도 클릭 시 또는 등록 완료 시)
    closeOverlay() {
      this.customOverlay.setMap(null);  // 지도에서 제거
      this.$refs.overlayContent.style.display = "none"; // DOM 요소 숨김
    },

    // axios를 이용한 장소 등록 처리
    async registerPlace() {
      // 선택한 좌표가 없으면 경고 후 리턴
      if (!this.selectedLatLng) {
        alert("좌표 정보가 없습니다.");
        return;
      }

      try {
        // FormData를 생성해 내용 추가
        const formData = new FormData();
        formData.append("latitude", this.selectedLatLng.getLat());
        formData.append("longitude", this.selectedLatLng.getLng());
        // 나중에 이미지나 텍스트 추가 시 여기에 formData.append(...);

        // Spring 서버에 요청을 보냄
        const response = await axios.post(
          "http://localhost:8090/api/places",
          formData,
          {
            headers: {
              // multipart/form-data 형식으로 전송
              // Spring도 이 형식을 받아낼 수 있어야 함(서로)
              "Content-Type": "multipart/form-data",
            },
          }
        );
        alert("장소 등록 완료!");
        this.closeOverlay();
        // 등록된 장소 불러오기
        this.fetchMarkers();
      } catch (error) {
        console.error("등록 실패", error);
        alert("등록에 실패했습니다.");
      }
    },

    // 등록된 장소 데이터 가져오기
    fetchMarkers(){
      axios.get("http://localhost:8090/api/places")
      .then((response) =>{
        this.savedMarkers = response.data;
        this.showSavedMarkers();    // 받아온 데이터로 마커 생성
      }) 
      .catch((err) => {
        console.error("마커 불러오기 실패", err);
      });
    },

    // 받아온 데이터로 Kakao 마커 생성
    showSavedMarkers() {
      this.savedMarkers.forEach((place) => {
        // 등록된 마커 이미지 크기
        const regImageSize = new kakao.maps.Size(24,35);

        // 등록된 마커 이미지 생성
        const regMarkerImage = new kakao.maps.MarkerImage(this.regMarkerImageSrc, regImageSize);

        // 등록된 마커 생성
        const marker = new kakao.maps.Marker({
          map: this.map,
          position: new kakao.maps.LatLng(place.latitude, place.longitude),
          title: place.title,     // 마커의 타이틀
          image: regMarkerImage   // 등록된 마커 이미지
        });
        this.regMarkers.push(marker);

        // 인포윈도우 테스트
        const infoWindow = new kakao.maps.InfoWindow({
          content: `<div style ="padding:5px;">${place.latitude}</div>`,
        });

        kakao.maps.event.addListener(marker, "click", () => {
          //기존 열린 인포윈도우 닫기
          if(this.openInfoWindow){
            this.openInfoWindow.close();
          }
          // 새로운 인포윈도우 열기
          infoWindow.open(this.map, marker);

          // 현재 열린 인포윈도우로 저장
          this.openInfoWindow = infoWindow;
        });
      })
    }
  },
};
</script>

<style>
/* 커스텀 오버레이 스타일 */
.wrap {
  position: absolute;
  pointer-events: auto;
  z-index: 1000;
  left: 0;
  bottom: 40px;
  width: 288px;
  height: 132px;
  margin-left: -144px;
  text-align: left;
  overflow: hidden;
  font-size: 12px;
  font-family: "Malgun Gothic", dotum, "돋움", sans-serif;
  line-height: 1.5;
}
.wrap * {
  padding: 0;
  margin: 0;
}
.wrap .info {
  width: 286px;
  height: 120px;
  border-radius: 5px;
  border-bottom: 2px solid #ccc;
  border-right: 1px solid #ccc;
  overflow: hidden;
  background: #fff;
}
.wrap .info:nth-child(1) {
  border: 0;
  box-shadow: 0px 1px 2px #888;
}
.info .title {
  padding: 5px 0 0 10px;
  height: 30px;
  background: #eee;
  border-bottom: 1px solid #ddd;
  font-size: 18px;
  font-weight: bold;
}
.info .close {
  position: absolute;
  top: 10px;
  right: 10px;
  color: #888;
  width: 17px;
  height: 17px;
  background: url("https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/overlay_close.png");
}
.info .close:hover {
  cursor: pointer;
}
.info .body {
  position: relative;
  overflow: hidden;
}
.info .desc {
  position: relative;
  margin: 13px 0 0 90px;
  height: 75px;
}
.desc .ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.desc .jibun {
  font-size: 11px;
  color: #888;
  margin-top: -2px;
}
.info .img {
  position: absolute;
  top: 6px;
  left: 5px;
  width: 73px;
  height: 71px;
  border: 1px solid #ddd;
  color: #888;
  overflow: hidden;
}
.info:after {
  content: "";
  position: absolute;
  margin-left: -12px;
  left: 50%;
  bottom: 0;
  width: 22px;
  height: 12px;
  background: url("https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/vertex_white.png");
}
.info .link {
  color: #5085bb;
}
</style>