<template>
    <div class="review-list">
        <h3>리뷰</h3>
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
            </li>
        </ul>

        <button @click="showReviewPopup = true">리뷰 작성</button>

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
    },
    mounted() {
        console.log('[Review.vue] placeId:', this.placeId);
        this.fetchReviews();
    },
};
</script>

<style scoped>
ul {
    list-style: none;
    padding: 0;
    margin: 0;
}
.review-item {
    cursor: pointer;
    padding: 10px;
    border-bottom: 1px solid #ddd;
    transition: background-color 0.3s ease, box-shadow 0.3s ease;
    border-radius: 4px;
}

.review-item:hover {
    background-color: #4caf50;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
</style>