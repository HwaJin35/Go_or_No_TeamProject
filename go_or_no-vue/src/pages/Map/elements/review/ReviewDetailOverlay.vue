<template>
    <div class="overlay">
        <div class="overlay-content">
            <button class="close-btn" @click="$emit('close')" aria-label="닫기">✖</button>

            <!-- 리뷰 내용 영역 -->
            <div class="review-box">
                <h2>{{ review.title }}</h2>
                <div class="meta-info">
                    <p class="author">작성자: {{ review.userNickname }}</p>
                    <p>{{ formatDate(review.createdAt) }}</p>
                </div>
                <div class="review-content-box">
                    <h4 class="content-title">리뷰 내용</h4>
                    <p class="content-body">{{ review.content }}</p>
                </div>
            </div>

            <!-- 댓글 영역 -->
            <div class="comment-section">
                <div class="comment-wrapper">
                    <Comment :reviewId="review.id" />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Comment from '../comment/Comment.vue';

export default {
    props: {
        review: {
            type: Object,
            required: true,
        },
    },
    components: {
        Comment,
    },
    methods: {
        formatDate(date) {
            const d = new Date(date);
            return d.toLocaleString();
        },
    },
};
</script>

<style scoped>
.overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.overlay-content {
    background-color: white;
    padding: 20px;
    width: 500px;
    max-height: 90vh;
    overflow-y: auto;
    border-radius: 8px;
    position: relative;
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

/* 리뷰 박스 */
.review-box {
    border-bottom: 5px solid #ddd;
    padding-bottom: 16px;
    margin-bottom: 24px;
}

.meta-info {
    display: flex;
    justify-content: space-between;
    font-size: 0.9rem;
    color: #666;
    margin-bottom: 16px;
    border-bottom: 1px solid #ddd;
}

.meta-info .author {
    text-align: left;
}

.meta-info .date {
    text-align: right;
}

.author {
    font-size: 0.9rem;
    color: #666;
}


.review-content-box {
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #e0e0e0;
}

.content-title {
    font-weight: bold;
    margin-bottom: 8px;
    color: #333;
}

.content-body {
    white-space: pre-wrap; /* 줄바꿈 유지 */
    color: #444;
    line-height: 1.6;
}

/* 댓글 섹션 */
.comment-section {
    margin-top: 24px;
}

.comment-wrapper {
    max-height: 300px; /* 원하는 높이로 조절 */
    overflow-y: auto;
}
</style>