<template>
  <card class="card-map">
    <div class="map">
      <div id="map"></div> <!-- 이 안에 지도가 들어옴 -->
    </div>
    <div id="clickLatlng"></div> <!-- 지도에서 클릭한 좌표를 보여주는 텍스트 영역 -->
  </card>
</template>

<script>
import axios from "axios";

export default {
  data() { // 이 컴포넌트가 내부적으로 갖고 있는 상태(state) 정의
    return {
      selectedLatLng: null, // 클릭한 위치 정보를 저장함
    };
  },
  mounted() { // Vue 컴포넌트가 화면에 나타날 때 실행됨(Vue 라이프사이클 훅)
    if (window.kakao && window.kakao.maps) {
      this.initMap();
    } else {
      window.kakao.maps.load(this.initMap);
    }
  },
  methods: {
    initMap() {
      // 지도가 들어갈 DOM(Document Object Model)
      var container = document.getElementById("map");
      var options = {
        // 지도의 중심 좌표(서울시청 근처)
        center: new window.kakao.maps.LatLng(37.5665, 126.978),
        // 줌 레벨(숫자가 작을수록 더 확대됨)
        level: 3,
      };

      // 카카오 지도를 생성해서 map 변수에 저장
      var map = new window.kakao.maps.Map(container, options);

      // 마커 객체 생성
      var marker = new kakao.maps.Marker({
        position: map.getCenter(),
      });

      // 만든 마커를 지도에 표시
      marker.setMap(map);

      // 커스텀 오버레이에 사용할 이미지 경로 불러오기
      const imageSrc = require("@/assets/img/logo-base-image.png");

      // Vue 컴포넌트 내부의 데이터나 메서드에 안전하게 접근하기 위해 선언
      // this: 원래는 Vue 인스턴스를 가리키지만, 일반 함수 안에서는 그렇지 않음
      // 현재 Vue 인스턴스를 vm이라는 변수에 저장해둠
      const vm = this;

      // DOM 객체인 content를 만들고, 안에 오버레이로 쓸 HTML 작성
      var content = document.createElement("div");
      content.innerHTML = `
      <div class="wrap">
        <div class="info">
          <div class="title">
            장소 등록
            <div class="close" title="닫기"></div>
          </div>
          <div class="body">
            <div class="img">
              <img src="${imageSrc}" width="73" height="70" alt="장소 이미지">
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
      </div>
      `;

      // content를 이용해 커스텀 오버레이 생성
      // map: null이므로, 클릭 이벤트 없으면 우선 표시 안 됨
      var overlay = new kakao.maps.CustomOverlay({
        content: content,
        map: null,
        position: marker.getPosition(),
      });

      // 지도 클릭 이벤트
      kakao.maps.event.addListener(map, "click", function (mouseEvent) {
        if (overlay.getMap()) return;
        
        // 클릭한 위치(mouseEevent.latLng)를 가져와 마커 위치, 오버레이 위치를 설정
        var latLng = mouseEvent.latLng;
        marker.setPosition(latLng);
        overlay.setPosition(latLng);

        // 클릭한 좌표를 저장
        vm.selectedLatLng = latLng;

        // 클릭한 위치의 위도/경도를 #clickLatlng 영역에 텍스트로 표시
        var resultDiv = document.getElementById("clickLatlng");
        resultDiv.innerHTML =
          "클릭한 위치의 위도는 " +
          latLng.getLat() +
          " 이고, 경도는 " +
          latLng.getLng() +
          " 입니다.";
      });

      // 마커 클릭 이벤트
      // 마커 클릭 시, 커스텀 오버레이가 표시되도록 설정
      kakao.maps.event.addListener(marker, "click", function () {
        overlay.setMap(map);
      });

      // 닫기 버튼 누르면 오버레이 닫음
      content.querySelector(".close").onclick = function () {
        overlay.setMap(null);
      };

      // axios를 이용한 장소 등록 처리
      content.querySelector(".register-btn").onclick = async function () {
        // 선택한 좌표가 업으면 경고 후 리턴
        if (!vm.selectedLatLng) {
          alert("좌표 정보가 없습니다.");
          return;
        }

        try {
          // FormData를 생성해 내용 추가
          const formData = new FormData();
          formData.append("latitude", vm.selectedLatLng.getLat());
          formData.append("longitude", vm.selectedLatLng.getLng());
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
            },
          );

          alert("장소 등록 완료!");
          overlay.setMap(null);
        } catch (error) {
          console.error("등록 실패", error);
          alert("등록에 실패했습니다.");
        }
      };

      // 취소 버튼 클릭 이벤트
      content.querySelector(".cancel-btn").onclick = function () {
        overlay.setMap(null);
      };
    },
  },
};
</script>

<style>
/* 커스텀 오버레이 스타일 */
.wrap {
  position: absolute;
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
