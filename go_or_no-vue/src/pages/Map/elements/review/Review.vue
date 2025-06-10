<template>
    <div class="review-list">
        <h3>리뷰</h3>
        <!-- 리뷰 목록 영역 -->
        <div class="review-scroll-box">
            <div v-if="reviews.length === 0">등록된 리뷰가 없습니다.</div>
            <ul>
                <li 
                    v-for="review in reviews" 
                    :key="review.id" 
                    @click="selectReview(review)"
                    class="review-item"
                >
                    <h4>{{ review.title }}</h4>
                    <p>{{ review.content }}</p>
                    <p>{{ formatDate(review.createdAt) }}</p>
                </li>
            </ul>
        </div>

        <!-- 리뷰 작성버튼 -->
        <button class="write-review-btn" @click="showReviewPopup = true">✍️ 리뷰 작성하기</button>

        <!-- 리뷰 상세 컴포넌트 -->
        <ReviewDetailOverlay 
            v-if="selectedReview"
            :review="selectedReview"
            @close="selectedReview = null"
        />

        <!-- 리뷰 작성 컴포넌트 -->
        <RegisterReviewPopup
            v-if="showReviewPopup"
            :placeId="placeId"
            @close="showReviewPopup = false"
            @submitted="fetchReviews"
        />
    </div>
</template>

<script>
import ReviewDetailOverlay from './ReviewDetailOverlay.vue';
import RegisterReviewPopup from './RegisterReviewPopup.vue';

export default {
    props: ['placeId'],
    components: {
        ReviewDetailOverlay,
        RegisterReviewPopup,
    },
    data() {
        return {
            selectedReview: null,
            reviews: [],
            showReviewPopup: false,
        };
    },
    methods: {
        fetchReviews() {
            fetch(`http://localhost:8090/api/reviews/place/${this.placeId}`)
            .then(res => {
                if (!res.ok) {
                console.error('Response not OK', res.status);
                }
                return res.json();
            })
            .then(data => {
                console.log(data);
                this.reviews = data;
            })
            .catch(err => {
                console.error('Fetch error', err);
            });
        },
        selectReview(review) {
            this.selectedReview = review;
        },
        formatDate(date) {
            const d = new Date(date);
            return d.toLocaleString();
        },
    },
    mounted() {
        console.log('[Review.vue] placeId:', this.placeId);
        this.fetchReviews();
    },
};
</script>

<style scoped>
.review-scroll-box {
    max-height: 300px;
    overflow-y: auto;
    margin-bottom: 16px;
    border: 1px solid #ddd;
    padding: 8px;
    border-radius: 4px;
}

ul {
    list-style: none;
    padding: 0;
    margin: 0;
}
.review-item {
    cursor: pointer;
    padding: 10px;
    margin: 10px;
    border-bottom: 3px solid #ddd;
    transition: background-color 0.3s ease, box-shadow 0.3s ease;
    border-radius: 4px;
}

.review-item h4,
.review-item p {
    margin: 4px 0; /* 위아래 4px */
    line-height: 1.4;
    font-size: 14px;
}
.review-item h4 {
    font-weight: bold;
    font-size: 20px;
    color: #333;
}
.review-item p {
    margin: 4px 0;
    line-height: 1.4;
    font-size: 14px;
    white-space: nowrap;       /* 줄바꿈 없이 한 줄로 */
    overflow: hidden;          /* 넘친 내용 숨김 */
    text-overflow: ellipsis;   /* ... 으로 표시 */
}

/* 리뷰 목록 호버 색상 */
.review-item:hover {
    background-color: rgba(0, 123, 255, 0.5); /* 파란색을 10%만 입힘 */
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 리뷰 작성하기 버튼 */
.write-review-btn {
    background: linear-gradient(to right, #007bff, #0154ad);
    color: white;
    padding: 12px 20px;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    transition: transform 0.3s ease, filter 0.5s ease;
}

.write-review-btn:hover {
    transform: translateY(-2px);
    filter: brightness(0.8); /* 밝기를 80%로 */
}
</style>