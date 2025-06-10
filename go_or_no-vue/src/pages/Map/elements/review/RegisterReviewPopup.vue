<template>
    <div class="modal-backdrop" @click.self="$emit('close')">
        <div class="modal-content">
            <h2 class="modal-title">리뷰 작성</h2>
            <form @submit.prevent="submitReview" class="review-form">
                <input
                    v-model="review.title"
                    placeholder="제목을 입력하세요"
                    required
                    class="input-field"
                />
                <textarea
                    v-model="review.content"
                    placeholder="내용을 입력하세요"
                    required
                    class="textarea-field"
                ></textarea>
                <input
                    type="file"
                    @change="handleFileChange"
                    multiple
                    class="file-input"
                />
                <div class="button-group">
                    <button type="submit" class="submit-btn">등록</button>
                    <button type="button" @click="$emit('close')" class="cancel-btn">취소</button>
                </div>
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
            this.review.uploadFiles = Array.from(e.target.files);
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
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-content {
    background: #fff;
    padding: 30px;
    border-radius: 12px;
    width: 400px;
    max-width: 90%;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 20px;
    text-align: center;
}

.review-form {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.input-field,
.textarea-field {
    padding: 10px 14px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 14px;
    width: 100%;
}

.textarea-field {
    resize: vertical;
    min-height: 100px;
}

.file-input {
    font-size: 14px;
    padding: 4px;
}

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 10px;
}

.submit-btn {
    background-color: #007bff;
    color: white;
    padding: 8px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.2s;
}

.submit-btn:hover {
    background-color: #0069d9;
}

.cancel-btn {
    background-color: #e0e0e0;
    color: #333;
    padding: 8px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.2s;
}

.cancel-btn:hover {
    background-color: #d5d5d5;
}
</style>