<template>
  <div class="popup-overlay">
    <div class="popup-content">
      <h2>장소 수정</h2>

      <form @submit.prevent="submitUpdate">
        <div>
          <label for="name">장소명</label>
          <input type="text" id="name" v-model="form.name" required />
        </div>

        <div>
          <label for="description">설명</label>
          <textarea
            id="description"
            v-model="form.description"
            required
          ></textarea>
        </div>

        <div>
          <label for="category">카테고리</label>
          <select id="category" v-model="form.category" required>
            <option disabled value="">카테고리를 선택하세요</option>
            <option v-for="cat in categories" :key="cat" :value="cat">
              {{ mapCategoryName(cat) }}
            </option>
          </select>
        </div>

        <div>
          <label for="hashtags">해시태그</label>
          <input
            id="hashtags"
            v-model="form.hashtags"
            type="text"
            placeholder="해시태그를 입력하세요(예: #맛집 #카페)"
          />
        </div>

        <div class="button-group">
          <button type="submit">저장</button>
          <button type="button" @click="$emit('close')">취소</button>
        </div>
      </form>

      <button class="close-btn" @click="$emit('close')">✖</button>
    </div>
  </div>
</template>

<script>
import axiosInstance from "../../../utils/axiosInstance";

export default {
  name: "PlaceUpdatePopup",
  props: {
    place: Object,
  },
  data() {
    return {
      form: {
        name: this.place.name,
        description: this.place.description,
        category: this.mapCategoryName(this.place.category) || "",
        hashtags: Array.isArray(this.place.hashtags)
          ? this.place.hashtags.map((tag) => `#${tag}`).join(" ")
          : "",
      },
      categories: [],
    };
  },
  mounted() {
    axiosInstance
      .get("http://localhost:8090/api/places/categories")
      .then((res) => {
        this.categories = res.data;
      })
      .catch((err) => {
        console.error("카테고리 목록 불러오기 실패", err);
      });
    // console.log("초기 place.category:", this.place.category);
  },
  methods: {
    async submitUpdate() {
      try {
        // "#맛집 #카페" → ["맛집", "카페"]
        const hashtagsArray = this.form.hashtags
          .split(" ")
          .map((tag) => tag.replace(/^#/, "").trim())
          .filter((tag) => tag.length > 0);

        const payload = {
          ...this.form,
          latitude: this.place.latitude,
          longitude: this.place.longitude,
          hashtags: hashtagsArray,
          category: this.mapCategoryCode(this.form.category),
        };
        console.log("수정 전송 payload 확인:", payload);

        const response = await axiosInstance.put(
          `/api/places/${this.place.id}`,
          payload,
        );
        this.$emit("updated", response.data);
      } catch (error) {
        console.error("장소 수정 실패 in submitUpdate", error);
        alert("수정 중 오류가 발생했습니다.");
      }
    },
    mapCategoryName(cat) {
      const map = {
        FOOD: "음식",
        DRINK: "음료",
        SPOT: "명소",
        LANDMARK: "상징물",
        STORE: "가게",
        NATURE: "자연공간",
        ETC: "기타",
      };
      return map[cat] || cat;
    },
    mapCategoryCode(name) {
      const reverseMap = {
        음식: "FOOD",
        음료: "DRINK",
        명소: "SPOT",
        상징물: "LANDMARK",
        가게: "STORE",
        자연공간: "NATURE",
        기타: "ETC",
      };
      return reverseMap[name] || name;
    },
  },
  watch: {
    place: {
      immediate: true,
      handler(newPlace) {
        this.form = {
          name: newPlace.name || "",
          description: newPlace.description || "",
          category: this.mapCategoryName(newPlace.category) || "",
          hashtags: Array.isArray(newPlace.hashtags)
            ? newPlace.hashtags.map((tag) => `#${tag}`).join(" ")
            : "",
        };
      },
    },
  },
};
</script>

<style scoped>
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
  transition:
    background-color 0.2s ease,
    transform 0.1s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  line-height: 36px;
  text-align: center;
  padding: 0;
}

.close-btn:hover {
  background: #ccc;
}
</style>
