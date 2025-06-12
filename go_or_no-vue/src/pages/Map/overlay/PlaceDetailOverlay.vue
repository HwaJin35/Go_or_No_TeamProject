<template>
  <div class="overlay-container" v-if="visible">
    <div class="overlay-content">
      <button class="close-btn" @click="close" aria-label="닫기">✖</button>

      <div class="left-panel">
        <h2>{{ place.name }}</h2>
        <p>{{ place.description }}</p>
        <img
          v-if="place.uploadFiles"
          :src="place.uploadFiles"
          alt="장소 이미지"
          class="preview-img"
        />

        <!-- 작성자이거나 관리자일 경우에만 노출 -->
        <div v-if="isOwnerOrAdmin" class="action-buttons">
          <button @click="showUpdatePopup = true">수정</button>
          <button @click="showDeletePopup = true">삭제</button>
        </div>
      </div>

      <div class="right-panel">
        <Review :placeId="place.id" @select-review="onSelectReview" />
      </div>

      <!-- 수정 팝업 -->
      <PlaceUpdatePopup
        v-if="showUpdatePopup"
        :place="place"
        @close="showUpdatePopup = false"
        @updated="handleUpdateSuccess"
      />

      <!-- 삭제 팝업 -->
      <PlaceDeleteOverlay
        v-if="showDeletePopup"
        :placeId="place.id"
        @close="showDeletePopup = false"
        @deleted="handleDeleteSuccess"
      />
    </div>
  </div>
</template>

<script>
import Review from "../elements/review/Review.vue";
import PlaceUpdatePopup from "../place/PlaceUpdatePopup.vue";
import PlaceDeleteOverlay from "../place/PlaceDeleteOverlay.vue";

export default {
  name: "PlaceDetailOverlay",
  components: {
    Review,
    PlaceUpdatePopup,
    PlaceDeleteOverlay,
  },
  props: {
    place: Object,
    visible: Boolean,
    currentUserId: Number, // 로그인한 사용자 ID
    isAdmin: Boolean, // 관리자 여부
  },
  data() {
    return {
      selectedReview: null, // 리뷰 선택 상태
      showUpdatePopup: false,
      showDeletePopup: false,
    };
  },
  computed: {
    isOwnerOrAdmin() {
      return this.place.writerId === this.currentUserId || this.isAdmin;
    },
  },
  methods: {
    close() {
      this.$emit("close");
    },
    onSelectReview(review) {
      this.selectedReview = review; // 리뷰 클릭 시 상태 저장
    },
    handleUpdateSuccess(updatedPlace) {
      this.$emit("updated", updatedPlace); // 부모에게 갱신 알림
      this.showUpdatePopup = false;
    },
    handleDeleteSuccess() {
      this.$emit("deleted"); // 장소가 삭제됨을 부모에게 알림
      this.showDeletePopup = false;
    },
  },
//   watch: {
//     place(newPlace) {
//       console.log("부모 컴포넌트의 place 값:", newPlace);
//     },
//   },
//   mounted() {
//     console.log("초기 place 값:", this.place);
//   },
};
</script>

<style scoped>
.overlay-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.overlay-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 90%;
  max-width: 800px;
  min-height: 400px;
  text-align: left;
  display: flex;
  position: relative;
  gap: 20px;
}

.left-panel,
.right-panel {
  flex: 1;
  overflow-y: auto;
}

.preview-img {
  max-width: 100%;
  max-height: 300px;
  object-fit: cover;
  margin-top: 15px;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 20px;
  background: #f0f0f0;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    transform 0.1s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  line-height: 36px; /* 버튼 높이와 동일 */
  text-align: center;
  padding: 0;
}

.close-btn:hover {
  background: #ccc;
}
</style>
