<template>
  <div class="comment-list">
    <h4 class="title">댓글 목록</h4>

    <!-- 댓글 등록창 -->
    <div class="comment-form">
      <form @submit.prevent="submitComment(null)" class="comment-form-inner">
        <div class="textarea-wrapper">
          <textarea
            v-model="newComment.content"
            placeholder="댓글 작성"
            required
            class="comment-textarea"
          ></textarea>
          <button type="submit" class="submit-button">등록</button>
        </div>
      </form>
    </div>

    <div v-if="comments.length === 0" class="no-comment">
      등록된 댓글이 없습니다.
    </div>

    <!-- 댓글 렌더링 -->
    <div v-for="comment in comments" :key="comment.id" class="comment-card">
      <CommentItem
        :comment="comment"
        :reviewId="reviewId"
        @reply-submitted="fetchComments"
      />
    </div>
  </div>
</template>

<script>
import axiosInstance from "../../../../utils/axiosInstance";
import CommentItem from "./CommentItem.vue";

export default {
  props: ["reviewId"],
  components: { CommentItem },
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
        .then((data) => {
          this.comments = this.sortCommentRecursively(data);
        });
    },
    async submitComment(parentId) {
      try {
        await axiosInstance.post("/api/comments", {
          targetId: this.reviewId,
          targetType: "REVIEW",
          content: this.newComment.content,
          parentId: parentId || null,
        });
        this.newComment.content = "";
        this.fetchComments();
      } catch (error) {
        if (error.response && error.response.status === 401) {
          const goToLogin = window.confirm(
            "로그인 후 등록이 가능합니다.\n로그인 페이지로 이동하시겠습니까?",
          );
          if (goToLogin) this.$router.push("/login");
        } else {
          console.error("댓글 등록 실패: ", error);
        }
      }
    },
    formatDate(date) {
      const d = new Date(date);
      return d.toLocaleString();
    },
    // 재귀 정렬
    sortCommentRecursively(comments) {
        return comments
            .map(comment => {
                if (comment.children && comment.children.length > 0) {
                    comment.children = this.sortCommentRecursively(comment.children);
                }
                return comment;
            })
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    }
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
.comment-form-inner {
  width: 100%;
}

.textarea-wrapper {
  position: relative;
  width: 100%;
}

.comment-textarea {
  width: 100%;
  padding: 10px 80px 10px 10px; /* 오른쪽에 버튼 공간 확보 */
  resize: vertical;
  min-height: 80px;
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
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #0069d9;
}
</style>
