<template>
  <div class="popup-overlay">
    <div class="popup-content">
      <h2>정말 삭제하시겠어요?</h2>
      <p>삭제한 장소는 복구할 수 없습니다.</p>
      <div class="button-group">
        <button @click="confirmDelete">삭제</button>
        <button @click="$emit('close')">취소</button>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInstance from "../../../utils/axiosInstance";

export default {
  name: "PlaceDeleteOverlay",
  props: {
    placeId: Number,
  },
  methods: {
    async confirmDelete() {
      try {
        await axiosInstance.delete(`/api/places/${this.placeId}`);
        this.$emit("deleted"); // 삭제 완료 이벤트 발생
      } catch (error) {
        console.error("장소 삭제 실패 in confirmDelete", error);
        alert("삭제 중 오류가 발생했습니다.");
      }
    },
  },
};
</script>
