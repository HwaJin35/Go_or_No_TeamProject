<template>
  <card class="card-map">
    <div class="map">
      <div id="map"></div>
    </div>
    <div id="clickLatlng"></div>
  </card>
</template>
<script>
  export default{
    mounted(){
      if(window.kakao && window.kakao.maps){
        this.initMap();
      } else {
        // 로딩이 느릴 경우를 대비 ready 콜백
        window.kakao.maps.load(this.initMap);
      }
    },
    methods: {
      initMap(){
        var container = document.getElementById("map");
        var options = {
          center: new window.kakao.maps.LatLng(37.5665, 126.9780),  // 지도의 중심 좌표
        level: 3,   // 지도의 확대 레벨
      };

      // 지도 생성
      var map = new window.kakao.maps.Map(container, options);
      
      // 클릭한 위치에 마커 표출
      var marker = new kakao.maps.Marker({
        // 지도 중심 좌표에 마커를 생성
        position: map.getCenter()
      });
      // 마커를 지도 위에 표시
      marker.setMap(map);

      // 지도에 클릭 이벤트 등록
      kakao.maps.event.addListener(map, 'click', function(mouseEvent){
        if(overlay.getMap()) return;
        // 클릭한 위도, 경도 정보
        var latLng = mouseEvent.latLng;

        //마커 위치를 클릭한 위치로 옮김
        marker.setPosition(latLng);

        // 오버레이 위치도 마커 위치로 변경
        overlay.setPosition(latLng);

        var message = '클릭한 위치의 위도는' + latLng.getLat() + ' 이고, ';
        message += '경도는 ' + latLng.getLng() + ' 입니다.';

        var resultDiv = document.getElementById('clickLatlng');
        resultDiv.innerHTML = message;
      })

      // 커스텀 오버레이에 표시할 컨텐츠
      const imageSrc = require("@/assets/img/logo-base-image.png");
      var content = `
      <div class="wrap">
        <div class="info">
          <div class="title">
            장소 등록
          <div class="close" onclick="closeOverlay()" title="닫기"></div>
        </div>
        <div class="body">
          <div class="img">
            <img src="${imageSrc}" width="73" height="70" alt="장소 이미지">
        </div>
        <div class="desc">
          <div class="ellipsis">선택한 위치에 장소를 등록하시겠습니까?</div>
          <div style="margin-top: 10px; display: flex; gap: 10px;">
            <button onclick="registerPlace()" style="padding: 5px 10px; cursor: pointer;">등록</button>
            <button onclick="closeOverlay()" style="padding: 5px 10px; cursor: pointer;">취소</button>
          </div>
        </div>
      </div>
      `;

      // 마커 위에 커스텀 오버레이 표시
      var overlay = new kakao.maps.CustomOverlay({
        content: content,
        map: null,
        position: marker.getPosition()
      });

      // 마커를 클릭했을 때 커스텀 오버레이 표시
      kakao.maps.event.addListener(marker, 'click', function(){
        overlay.setMap(map);
      });

      // 커스텀 오버레이 닫기 호출 함수
      window.closeOverlay = function(){
        overlay.setMap(null);
      }
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
  font-family: 'Malgun Gothic', dotum, '돋움', sans-serif;
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
  background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/overlay_close.png');
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
  content: '';
  position: absolute;
  margin-left: -12px;
  left: 50%;
  bottom: 0;
  width: 22px;
  height: 12px;
  background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/vertex_white.png');
}
.info .link {
  color: #5085BB;
}
</style>
