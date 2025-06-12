import { ref } from "vue";
import { parseJwt } from "./jwtUtil";
import axiosInstance from "./axiosInstance";

// 로그인 여부 전역 상태
// !! 연산자 : 값을 boolean 값으로 명시적으로 변환
export const isLoggedIn = ref(!!localStorage.getItem("accessToken"));

export const remainingTime = ref(0); // 토큰만료시간
export const remainingTimeFormatted = ref('');  // 시간 표시 포맷팅
export const remainingTimePercent = ref(100); // 잔여 시간 퍼센트(초기 100%)
const ACCESS_TOKEN_KEY = "accessToken"; // accessToken Key
let logoutTimer = null; // 자동 로그아웃 타이머
let totalSessionTime = 0; // 전체 세션 시간

// 토큰 저장 및 타이머 시작 함수
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
  resetTimerDisplay(); // 타이머 표시 초기화
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
  } else {
    resetTimerDisplay();  // 토큰이 없으면 타이머 표시 초기화
  }
};

// 자동 로그아웃 함수
async function handleAutoLogout() {
  try {
    await axiosInstance.post('/api/logout', null, {
      withCredentials: true, // 쿠키 전송 필요!
    });
    console.log("서버 로그아웃 완료");
  } catch (error) {
    console.error("서버 로그아웃 실패", error);
  } finally {
    removeToken(); // 로컬 스토리지 토큰 제거
    clearAutoLogoutTimer();
    alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
    window.location.href = "/"; // 메인 페이지로 돌아가기
  }
}

// 시간 연장 함수
export const extendSessionTime = async () => {
  try {
    const currentToken = getToken();
    if(!currentToken) {
      throw new Error('토큰이 없습니다.');
    }

  // 서버에 토큰 갱신 요청
  const response = await axiosInstance.post('/api/auth/refresh');
  const newToken = response.data.accessToken;
  // console.log("세션 만료 연장 응답:", response.data);

  // 갱신된 토큰 저장 후 타이머 재시작
  saveToken(newToken);
    return true;
  } catch (error) {
    console.error('세션 연장 실패: ', error);
    return false;
  }
};

// 남은 시작 계산 및 자동 로그아웃 함수
function startAutoLogoutTimer(token) {
  clearAutoLogoutTimer(); // 기존 타이머 제거

  const decoded = parseJwt(token);
  if (!decoded || !decoded.exp) {
    console.error('토큰 파싱 실패 또는 만료시간 없음');
    resetTimerDisplay();
    return;
  }
  const now = Math.floor(Date.now() / 1000); // 현재시간(초)
  const expiresIn = decoded.exp - now; // 만료까지 남은 시간

  if (expiresIn <= 0) {
    console.log('토큰이 이미 만료됨');
    removeToken();
    return;
  }

  // 초기값 설정
  remainingTime.value = expiresIn;
  totalSessionTime = expiresIn; // 세션 전체 시간 저장
  updateFormattedTime();
  updateRemainingTimePercent();

  console.log(`세션 타이머 시작: ${expiresIn}초 남음`);

  // 1초마다 실행되는 타이머
  logoutTimer = setInterval(() => {
    remainingTime.value--;
    updateFormattedTime();
    updateRemainingTimePercent();

    // 5분 전 경고
    if (remainingTime.value === 300) {
      alert("세션이 5분 후 만료됩니다. 계속 사용하시려면 시간을 연장해 주세요.");
    }

    // 세션 만료
    if (remainingTime.value <= 0) {
      handleAutoLogout(); 
    }
  }, 1000);
}

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

// 타이머 표시 초기화
function resetTimerDisplay() {
  remainingTime.value = 0;
  remainingTimeFormatted.value = '';
  remainingTimePercent.value = 0;
  totalSessionTime = 0;
}

// 타이머 클리어
function clearAutoLogoutTimer() {
  if (logoutTimer) {
    clearInterval(logoutTimer);
    logoutTimer = null;
  }
}