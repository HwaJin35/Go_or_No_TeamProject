<template>
    <!-- v-if: 조건부 렌더링 디렉티브 -->
    <div v-if="isVisible" class="popup-overlay">
        <div class="popup-content">
            <h2>장소 등록</h2>

            <!-- Vue에서 form을 처리하는 방식 -->
            <form @submit.prevent="handleSubmit">
                
                <!-- 이름 입력 -->
                <div>
                    <label for="name">장소명</label>
                    <!-- v-model: 양방향 바인딩 디렉티브
                        사용자 입력값이 place.name 데이터에 자동으로 반영됨
                        내부적으로는 :value와 @input을 합친 형태
                    -->
                    <input type="text" id="name" v-model="form.name" required />
                </div>

                <!-- 설명 입력 -->
                <div>
                    <label for="description">설명</label>
                    <textarea id="description" v-model="form.description" required></textarea>
                </div>

                <!-- 카테고리 선택 -->
                <div>
                    <label for="category">카테고리</label>
                    <select id="category" v-model="form.category" required>
                        <option disabled value="">카테고리를 선택하세요</option>
                        <option v-for="category in categories" :key="category" :value="category">
                            {{ category }}
                        </option>
                    </select>
                </div>

                <!-- 해시태그 입력 -->
                <div>
                    <label for="hashtags">해시태그</label>
                    <input
                        id="hashtags"
                        v-model="form.hashtags"
                        type="text"
                        placeholder="해시태그를 입력하세요(예: #맛집 #카페)"
                    />
                </div>

                <!-- 파일 업로드 -->
                <div>
                    <label for="uploadFiles">이미지 업로드</label>
                    <input
                        id="uploadFiles"
                        type="file"
                        @change="handleFileUpload"
                        multiple
                    />
                </div>

                <!-- 숨겨서 보낼 좌표 -->
                <input type="hidden" v-model="form.latitude" />
                <input type="hidden" v-model="form.longitude" />

                <!-- '등록하기'와 '취소' 버튼 -->
                <div class="button-group">
                    <button type="submit">등록하기</button>
                    <button type="button" @click="closePopup">취소</button>
                </div>
            </form>
            <!-- 닫기 버튼 -->
            <button class="close-btn" @click="closePopup">✖</button>
        </div>
    </div>
</template>

<script>
import { registerPlace } from "./registerPlace";
import axios from "axios";

export default {
    data() {
        return {
            isVisible: false, // 팝업 호출 전에는 열리지 않는 상태
            form: {
                name: "",
                description: "",
                category: "",
                hashtags: "",
                uploadFiles: [], // base64로 변환된 이미지들
                latitude: null,
                longitude: null,
            },
            categories: [], // 카테고리 목록
        };
    },
    mounted() {
        // 서버에서 카테고리 목록 불러오기
        axios
            .get("http://localhost:8090/api/places/categories")
            .then((response) => {
                this.categories = response.data;
                console.log(response);
            })
            .catch((error) => {
                console.error("카테고리 목록 불러오기 실패", error);
            });
    },
    methods: {
        // 이미지 파일 업로드 처리(base64로 변환)
        handleFileUpload(event) {
            const files = event.target.files;
            this.form.uploadFiles = Array.from(files);

            // @@@@@ base64로 처리하면 서버에서 못 받아냄!
            // this.form.uploadFiles = Array.from(files).map((file) => {
            //     return new Promise((resolve) => {
            //         const reader = new FileReader();
            //         reader.readAsDataURL(file); // base64로 변환
            //         reader.onload = () => {
            //             resolve(reader.result); // base64로 변환된 파일 반환
            //         };
            //     });
            // });
        },

        // 폼 제출 시 데이터 서버로 전송
        async handleSubmit() {
            // console.log("선택된 카테고리:", this.form.category);

            try {
                // 모든 파일을 base64로 변환 완료 후 서버로 전송
                const files = await Promise.all(this.form.uploadFiles); // 모든 Promise를 기다림
                this.form.uploadFiles = files; // 변환된 base64 데이터를 formData에 저장

                // registerPlace.js 호출하여 처리
                const success = await registerPlace(
                    this.form.name,
                    this.form.description,
                    this.form.category,
                    this.form.hashtags,
                    this.form.uploadFiles,
                    this.form.latitude,
                    this.form.longitude
                );

                if (success) {
                    alert("장소 등록 완료!");
                    this.$emit("place-registered"); // 부모 쪽으로 이벤트 전달
                    this.closePopup(); // 성공 후 팝업 닫기
                }
            } catch (error) {
                console.log(error);
                alert("장소 등록 실패");
            }
        },

        // 팝업 열기
        showPopup(latLng) {
            this.selectedLatLng = latLng;
            this.form.latitude = latLng.getLat(); // 위도
            this.form.longitude = latLng.getLng(); // 경도
            this.isVisible = true; // 팝업을 보여주기 위해 true로 설정
        },
        
        // 팝업 닫기
        closePopup() {
            this.isVisible = false;
        }
    }
}
</script>

<style scoped>
/* 팝업 스타일 */
.popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
}

.popup-content {
    background: #fff;
    padding: 30px 25px;
    border-radius: 12px;
    width: 480px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    position: relative;
    max-height: 90vh;
    overflow-y: auto;
}

.popup-content h2 {
    margin-top: 0;
    margin-bottom: 20px;
    font-size: 22px;
    text-align: center;
    color: #333;
}

.popup-content label {
    display: block;
    margin: 12px 0 6px;
    font-weight: bold;
    color: #555;
}

.popup-content input[type="text"],
.popup-content input[type="file"],
.popup-content textarea,
.popup-content select {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ccc;
    border-radius: 6px;
    box-sizing: border-box;
    font-size: 14px;
}

.popup-content textarea {
    resize: vertical;
    min-height: 80px;
}

.button-group {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    margin-top: 20px;
}

.button-group button {
    flex: 1;
    padding: 12px;
    font-weight: bold;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.button-group button[type="submit"] {
    background-color: #007bff;
    color: white;
    border: none;
}

.button-group button[type="submit"]:hover {
    background-color: #0069d9;
}

.button-group button[type="button"] {
    background-color: #e0e0e0;
    border: none;
    color: #333;
}

.button-group button[type="button"]:hover {
    background-color: #ccc;
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
</style>