import { ref } from "vue";
import { parseJwt } from "./jwtUtil";

// 로그인 여부 전역 상태
// !! 연산자 : 값을 boolean 값으로 명시적으로 변환
export const isLoggedIn = ref(!!localStorage.getItem("accessToken"));

export const remainingTime = ref(0); // 토큰만료시간
export const remainingTimeFormatted = ref('');  // 시간 표시 포맷팅
export const remainingTimePercent = ref(100); // 초기 100%
const ACCESS_TOKEN_KEY = "accessToken"; // 토큰 키 이름
let logoutTimer = null; // 자동 로그아웃 타이머

// 토큰 저장 함수
export const saveToken = (token) => {
  localStorage.setItem(ACCESS_TOKEN_KEY, token);
  isLoggedIn.value = true;
  startAutoLogoutTimer(token); // 토큰 저장 시 로그아웃 타이머 시작
};

// 토큰 삭제 함수
export const removeToken = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  isLoggedIn.value = false;
  clearAutoLogoutTimer(); // 로그아웃 시 타이머 클리어
};

// 토큰 조회 함수
export const getToken = () => {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
};

// 앱 시작 시 로그인 상태 초기화 함수
export const initializeLoginState = () => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY);
  isLoggedIn.value = !!token; // 토큰 있으면 true, 없으면 false

  if (token) {
    startAutoLogoutTimer(token); //  앱 시작할때도 타이머 시작
  }
};

// 남은 시작 계산 및 자동 로그아웃 함수
function startAutoLogoutTimer(token) {
  clearAutoLogoutTimer(); // 기존 타이머 제거

  const decoded = parseJwt(token);
  if (!decoded || !decoded.exp) return;

  const now = Math.floor(Date.now() / 1000); // 현재시간(초)
  const expiresIn = decoded.exp - now; // 만료까지 남은 시간

  if (expiresIn <= 0) {
    removeToken();
    return;
  }

  remainingTime.value = expiresIn;
  totalSessionTime = expiresIn; // 세션 전체 시간 저장
  updateFormattedTime();
  updateRemainingTimePercent();

  logoutTimer = setInterval(() => {
    remainingTime.value--;
    updateFormattedTime();
    updateRemainingTimePercent();

    if (remainingTime.value <= 0) {
      removeToken();
      clearAutoLogoutTimer();
      alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
      window.location.href = "/"; // 메인 페이지로 돌아가기
    }
  }, 1000);
}
let totalSessionTime = 0; // 추가: 전체 세션 시간

// 남은 시간을 "분:초"로 변환
function updateFormattedTime() {
  const minutes = Math.floor(remainingTime.value / 60);
  const seconds = remainingTime.value % 60;
  remainingTimeFormatted.value = `${minutes}분 ${seconds.toString().padStart(2, '0')}초`;
}

// 프로그레스 바 함수
function updateRemainingTimePercent() {
  if (totalSessionTime === 0) {
    remainingTimePercent.value = 0;
  } else {
    remainingTimePercent.value = (remainingTime.value / totalSessionTime) * 100;
  }
}

// 타이머 클리어
function clearAutoLogoutTimer() {
  if (logoutTimer) {
    clearInterval(logoutTimer);
    logoutTimer = null;
  }
}