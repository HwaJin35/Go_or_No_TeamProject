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
            </div>

            <div class="right-panel">
                <Review :placeId="place.id" @select-review="onSelectReview"/>
            </div>
        </div>
    </div>
</template>

<script>
import Review from '../elements/review/Review.vue';

export default {
    name: 'PlaceDetailOverlay',
    components: {
        Review,
    },
    props: {
        place: Object,
        visible: Boolean
    },
    data() {
        return {
            selectedReview: null, // 리뷰 선택 상태
        };
    },
    methods: {
        close() {
            this.$emit('close');
        },
        onSelectReview(review) {
            this.selectedReview = review; // 리뷰 클릭 시 상태 저장
        }
    }
};
</script>

<style scoped>
.overlay-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.4);
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

.left-panel, .right-panel {
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
    transition: background-color 0.2s ease, transform 0.1s ease;
    box-shadow: 0 1px 3px rgba(0,0,0,0.2);
    line-height: 36px; /* 버튼 높이와 동일 */
    text-align: center;
    padding: 0;
}

.close-btn:hover {
    background: #ccc;
}
</style>