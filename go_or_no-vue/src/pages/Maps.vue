<template>
  <card class="card-map">
    <div class="map">
      <div id="map"></div>
    </div>
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
        const container = document.getElementById("map");
        const options = {
          center: new window.kakao.maps.LatLng(37.5665, 126.9780),
        level: 3,
      };

      const map = new window.kakao.maps.Map(container, options);

      //마커 추가 예시
      const markerPosition = new window.kakao.maps.LatLng(37.5665, 126.9780);
      const marker = new window.kakao.maps.Marker({
        position: markerPosition,
      });

      marker.setMap(map);
    },
  },
};
</script>
<style></style>
