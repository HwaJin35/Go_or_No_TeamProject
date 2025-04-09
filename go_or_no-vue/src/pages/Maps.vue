<template>
  <card class="card-map">
    <div class="map">
      <div id="map"></div>
    </div>
    <div id="clickLatlng"></div>
  </card>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      selectedLatLng: null,
    };
  },
  mounted() {
    if (window.kakao && window.kakao.maps) {
      this.initMap();
    } else {
      window.kakao.maps.load(this.initMap);
    }
  },
  methods: {
    initMap() {
      var container = document.getElementById("map");
      var options = {
        center: new window.kakao.maps.LatLng(37.5665, 126.978),
        level: 3,
      };

      var map = new window.kakao.maps.Map(container, options);

      var marker = new kakao.maps.Marker({
        position: map.getCenter(),
      });

      marker.setMap(map);

      const imageSrc = require("@/assets/img/logo-base-image.png");

      const vm = this;

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

      var overlay = new kakao.maps.CustomOverlay({
        content: content,
        map: null,
        position: marker.getPosition(),
      });

      kakao.maps.event.addListener(map, "click", function (mouseEvent) {
        if (overlay.getMap()) return;

        var latLng = mouseEvent.latLng;

        marker.setPosition(latLng);
        overlay.setPosition(latLng);
        vm.selectedLatLng = latLng;

        var resultDiv = document.getElementById("clickLatlng");
        resultDiv.innerHTML =
          "클릭한 위치의 위도는 " +
          latLng.getLat() +
          " 이고, 경도는 " +
          latLng.getLng() +
          " 입니다.";
      });

      kakao.maps.event.addListener(marker, "click", function () {
        overlay.setMap(map);
      });

      content.querySelector(".close").onclick = function () {
        overlay.setMap(null);
      };

      // axios를 이용한 장소 등록 처리
      content.querySelector(".register-btn").onclick = async function () {
        if (!vm.selectedLatLng) {
          alert("좌표 정보가 없습니다.");
          return;
        }

        try {
          const formData = new FormData();
          formData.append("latitude", vm.selectedLatLng.getLat());
          formData.append("longitude", vm.selectedLatLng.getLng());
          // 나중에 이미지나 텍스트 추가 시 여기에 formData.append(...);

          const response = await axios.post(
            "http://localhost:8090/api/places",
            formData,
            {
              headers: {
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
