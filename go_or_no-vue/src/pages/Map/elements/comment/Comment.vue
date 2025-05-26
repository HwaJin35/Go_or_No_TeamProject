<template>
    <div class="comment-list">
        <h4 class="title">댓글 목록</h4>
        <!-- 댓글 등록창 -->
        <div class="comment-form">
            <form @submit.prevent="submitComment" class="comment-form-inner">
                <textarea
                v-model="newComment.content"
                placeholder="댓글 작성"
                required
                class="comment-textarea"
                ></textarea>
                <button type="submit" class="submit-button">등록</button>
            </form>
            </div>
        <div v-if="comments.length === 0" class="no-comment">
            등록된 댓글이 없습니다.
        </div>
        <div v-else class="comment-container">
            <div v-for="comment in comments" :key="comment.id" class="comment-card">
                <p class="comment-content">
                    {{ comment.userNickname }}: {{ comment.content }}
                </p>
            </div>
        </div>

    </div>
    </template>

    <script>
    import axiosInstance from "../../../../utils/axiosInstance";

    export default {
    props: ["reviewId"],
    data() {
        return {
        comments: [],
        newComment: { content: "" },
        };
    },
    methods: {
        fetchComments() {
        fetch(`http://localhost:8090/api/comments/review/${this.reviewId}`)
            .then((res) => res.json())
            .then((data) => (this.comments = data));
        },
        async submitComment() {
        try {
            await axiosInstance.post("/api/comments", {
            targetId: this.reviewId,
            targetType: "REVIEW",
            content: this.newComment.content,
            });
            this.newComment.content = "";
            this.fetchComments();
        } catch (error) {
            console.error("댓글 등록 실패: ", error);
        }
        },
    },
    watch: {
        reviewId(newId) {
        if (newId) this.fetchComments();
        },
    },
    mounted() {
        if (this.reviewId) this.fetchComments();
    },
};
</script>

<style scoped>
.comment-list {
    max-width: 600px;
    margin: 0 auto;
}

.title {
    margin-bottom: 12px;
    font-size: 18px;
    font-weight: bold;
}

.no-comment {
    font-style: italic;
    color: #777;
    margin-bottom: 10px;
}

.comment-container {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.comment-card {
    background-color: #f8f9fa;
    border-radius: 8px;
    padding: 12px 16px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.comment-content {
    margin: 0;
    font-size: 14px;
    line-height: 1.5;
}

.comment-form {
    margin-top: 20px;
}

.comment-form-inner {
    position: relative;
    width: 100%;
}

.comment-textarea {
    width: 100%;
    height: 80px;
    padding: 10px 80px 10px 10px; /* 오른쪽 공간 확보 */
    border-radius: 6px;
    border: 1px solid #ccc;
    resize: none;
    font-size: 14px;
    box-sizing: border-box;
}

.submit-button {
    position: absolute;
    top: 50%;
    right: 10px;
    transform: translateY(-50%);
    padding: 8px 16px;
    border: none;
    background-color: #4caf50;
    color: white;
    border-radius: 6px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.2s;
    height: 40px;
    line-height: 1;
}

.submit-button:hover {
    background-color: #45a049;
}
</style>
