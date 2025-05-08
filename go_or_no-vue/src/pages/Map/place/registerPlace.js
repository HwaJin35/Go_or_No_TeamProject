import axiosInstance from "../../../utils/axiosInstance";

/**
 * 사용자가 선택한 좌표를 서버에 장소로 등록하는 함수
 * - API: POST /api/places
 * - 요청 형식: multipart/form-data
 * 
 * @param {number} lat - 위도
 * @param {number} lng - 경도
 * @returns {Promise<boolean>} 등록 성공 여부 (true 반환)
 * @throws {Error} 등록 실패 시 에러를 throw
 */

// // 테스트용
// export async function registerPlace(lat, lng) {
//   // FormData를 생성해 내용 추가
//   const formData = new FormData();
//   formData.append("latitude", lat);
//   formData.append("longitude", lng);
//   // 나중에 이미지나 텍스트 추가 시 여기에 formData.append(...);

export async function registerPlace(name, description, category, hashtags, uploadFiles, lat, lng) {
  const formData = new FormData();
  formData.append("name", name);
  formData.append("description", description);
  formData.append("categoryName", category);
  formData.append("hashtags", hashtags);
  formData.append("latitude", lat);
  formData.append("longitude", lng);

  // base64로 변환된 이미지들을 FormData에 추가
  uploadFiles.forEach((uploadFiles, idx) => {
    // base64 이미지를 "uploadFiles" 필드로 추가
    formData.append("uploadFiles", uploadFiles);
  });

  try {
    // Spring 서버에 요청을 보냄
    await axiosInstance.post("/api/places", formData, {
      headers: {
        // multipart/form-data 형식으로 전송
        // Spring도 이 형식을 받아낼 수 있어야 함(서로)
        ...axiosInstance.defaults.headers.common, // 기존 헤더 유지
        "Content-Type": "multipart/form-data",
      },
    });
    return true;
  } catch (error) {
    console.error("장소 등록 실패 in registerPlace", error);
    throw error;
  }
}
