<template>
    <div class="overlay">
        <div class="overlay-content">
            <button class="close-btn" @click="$emit('close')" aria-label="닫기">닫기</button>

            <h2>{{ review.title }}</h2>
            <p class="author">작성자: {{ review.userNickname }}</p>
            <p>{{ review.content }}</p>

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
    top: 10px;
    right: 12px;
    background: transparent;
    border: none;
    font-size: 20px;
    cursor: pointer;
}

.author {
    font-size: 0.9rem;
    color: #666;
    margin-bottom: 16px;
}

.comment-section {
    margin-top: 24px;
}

.comment-wrapper {
    max-height: 300px; /* 원하는 높이로 조절 */
    overflow-y: auto;
    padding-right: 8px; /* 스크롤바 공간 확보용 */
}
</style>