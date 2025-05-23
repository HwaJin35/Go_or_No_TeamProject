<template>
    <div class="comment-list">
        <h4>댓글 목록</h4>
        <div v-if="comments.length === 0">등록된 댓글이 없습니다.</div>
        <ul>
        <li v-for="comment in comments" :key="comment.id">
            <p>{{ comment.content }}</p>
        </li>
        </ul>

        <div class="comment-form">
        <form @submit.prevent="submitComment">
            <textarea
                v-model="newComment.content"
                placeholder="댓글 작성"
                required
            ></textarea>
            <button type="submit">등록</button>
        </form>
        </div>
    </div>
</template>

<script>
import axiosInstance from '../../../../utils/axiosInstance';

export default {
    props: ['reviewId'],
    data() {
        return {
            comments: [],
            newComment: { content: ''},
        };
    },
    methods: {
        fetchComments() {
            fetch(`http://localhost:8090/api/comments/review/${this.reviewId}`)
                .then(res => res.json())
                .then(data => (this.comments = data));
        },
        async submitComment() {
            try {
                await axiosInstance.post('/api/comments', {
                    targetId: this.reviewId,
                    targetType: 'REVIEW',
                    content: this.newComment.content,
                });
                this.newComment.content = '';
                this.fetchComments();
            } catch(error) {
                console.error("댓글 등록 실패: ", error);
            }
        },
    },
    watch: {
        reviewId(newId) {
            if (newId) this.fetchComments();
        }
    },
    mounted() {
        if (this.reviewId) this.fetchComments();
    },
};
</script>