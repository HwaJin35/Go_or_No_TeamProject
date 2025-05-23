<template>
    <div class="modal-backdrop" @click.self="$emit('close')">
        <div class="modal-content">
        <h4>리뷰 작성</h4>
        <form @submit.prevent="submitReview">
            <input v-model="review.title" placeholder="제목" required />
            <textarea v-model="review.content" placeholder="내용" required></textarea>
            <input type="file" @change="handleFileChange" multiple />
            <button type="submit">등록</button>
            <button type="button" @click="$emit('close')">닫기</button>
        </form>
        </div>
    </div>
</template>

<script>
import axiosInstance from '../../../../utils/axiosInstance';

export default {
    props: ['placeId'],
    data() {
        return {
            review: { title: '', content: '', uploadFiles: [] },
        };
    },
    methods: {
        handleFileChange(e) {
            this,review.uploadFiles = Array.from(e.target.files);
        },
        async submitReview() {
            const formData = new FormData();
            formData.append('title', this.review.title);
            formData.append('content', this.review.content);
            formData.append('placeId', this.placeId);
            this.review.uploadFiles.forEach(file => formData.append('uploadFiles', file));

            try {
                await axiosInstance.post('/api/reviews', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                this.$emit('submitted');
                this.$emit('close');
            } catch (error) {
                console.error('리뷰 등록 실패:', error.response || error.message);
                alert('리뷰 등록 중 오류가 발생했습니다.');
            }
        },
    },
};
</script>

<style scoped>
.modal-backdrop {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.4);
    display: flex;
    align-items: center;
    justify-content: center;
}
.modal-content {
    background: white;
    padding: 20px;
    border-radius: 10px;
    min-width: 300px;
}
</style>