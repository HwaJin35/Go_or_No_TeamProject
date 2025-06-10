<template>
    <div class="comment-item">
        <div class="comment-header">
            <p class="comment-content">
                {{ comment.userNickname }}: {{ comment.content }}
            </p>
            <p class="comment-date">{{ formatDate(comment.createdAt) }}</p>
        </div>

        <!-- 답글 목록 버튼 -->
        <button
            v-if="comment.children && comment.children.length > 0"
            class="toggle-replies-btn"
            @click="toggleReplySection"
        >
        {{ showReplySection ? "답글 닫기" : "답글 열기" }}
        </button>

        <!-- 답글 달기 버튼 -->
        <button class="reply-btn" @click="toggleReplyForm">
            {{ showReplyForm ? "취소" : "답글 달기" }}
        </button>


        <!-- 대댓글 입력 폼 -->
        <div v-if="showReplyForm" class="reply-form">
            <form @submit.prevent="submitReply" class="reply-form-inner">
                <div class="textarea-wrapper">
                    <textarea
                        v-model="replyContent"
                        placeholder="답글을 입력하세요"
                        required
                        class="reply-textarea"
                    ></textarea>
                    <button type="submit" class="submit-button">등록</button>
                </div>
            </form>
        </div>

        <!-- 자식 댓글 재귀 렌더링 -->
        <div
            class="child-comments"
            v-if="showReplySection && comment.children && comment.children.length > 0"
        >
        <CommentItem
            v-for="child in comment.children"
            :key="child.id"
            :comment="child"
            :reviewId="reviewId"
            @reply-submitted="$emit('reply-submitted')"
        />
        </div>
    </div>
</template>

<script>
import axiosInstance from '../../../../utils/axiosInstance';

export default {
    name: "CommentItem",
    props: ["comment", "reviewId"],
    data() {
        return {
            showReplyForm: false,
            showReplySection: false,
            replyContent: "",
        };
    },
    methods: {
        toggleReplyForm() {
            this.showReplyForm = !this.showReplyForm;
            if (!this.showReplyForm) {
                this.replyContent = "";
            }
        },
        toggleReplySection() {
            this.showReplySection = !this.showReplySection;
        },
        async submitReply() {
            try {
                await axiosInstance.post("/api/comments", {
                targetId: this.reviewId,
                targetType: "REVIEW",
                content: this.replyContent,
                parentId: this.comment.id,
            });
            this.replyContent = "";
            this.showReplyForm = false;
            this.$emit("reply-submitted"); // 댓글 새로고침 이벤트
            } catch (error) {
                console.error("답글 등록 실패", error);
            }
            },
            formatDate(date) {
            const d = new Date(date);
            return d.toLocaleString();
        },
    },
};
</script>

<style scoped>
.comment-item {
    margin-left: 0;
    padding: 10px;
    border-left: 2px solid #ccc;
}

.child-comments {
    margin-left: 20px;
    margin-top: 10px;
}


.toggle-replies-btn {
    font-size: 12px;
    color: #ff4d00;
    background: none;
    border: none;
    cursor: pointer;
    margin-top: 5px;
}

.reply-btn {
    font-size: 12px;
    color: #007bff;
    background: none;
    border: none;
    cursor: pointer;
    margin-top: 5px;
    margin-left: 5px
}

/* 대댓글 등록폼 스타일 */
.reply-form-inner {
    width: 100%;
    margin-top: 5px;
}

.textarea-wrapper {
    position: relative;
    width: 100%;
}

.reply-textarea {
    width: 100%;
    padding: 10px 80px 10px 10px; /* 오른쪽 버튼 공간 확보 */
    resize: vertical;
    min-height: 60px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

.submit-button {
    position: absolute;
    top: 50%;
    right: 20px;
    transform: translateY(-50%);
    background-color: #007bff;
    border: none;
    color: white;
    padding: 6px 14px;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 600;
    transition: background-color 0.3s ease;
}

.submit-button:hover {
    background-color: #0069d9;
}
</style>
