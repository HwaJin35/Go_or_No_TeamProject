import axiosInstance from "../../../utils/axiosInstance";

/**
 * 등록된 모든 장소 데이터를 백엔드에서 불러오는 함수
 * - API: GET /api/places
 * - 응답: 장소 객체 리스트 (latitude, longitude 등 포함)
 *
 * @returns {Promise<Array>} 장소 데이터 배열
 * @throws {Error} 요청 실패 시 에러 발생
 */

export async function getAllPlaces() {
  try {
    const response = await axiosInstance.get("/api/places");
    return response.data;   // 장소 리스트 반환
  } catch (error) {
    console.error("장소 불러오기 실패", error);
    throw error;
  }
}