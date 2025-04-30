import axios from "axios";

const BASE_URL = "http://localhost:8090"; // 실제 서버 주소로 교체

// 이메일 중복 확인
export const checkEmailDuplicate = (email) => {
  return axios.get(`${BASE_URL}/api/users/check-email`, {
    params: { email }
  });
};

// 이메일 인증 코드 요청
export const sendAuthCode = (email) => {
  return axios.post(`${BASE_URL}/api/auth/email/send`, { email });
};

// 이메일 인증 코드 검증
export const verifyAuthCode = (email, code) => {
  return axios.post(`${BASE_URL}/api/auth/email/verify`, { email, code });
};

// 회원가입 요청
export const registerUser = (formData) => {
  return axios.post(`${BASE_URL}/api/users/signup`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};