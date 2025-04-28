import axios from "axios";

// Axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8090',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터 
// API 요청할 때마다 localstorage에 저장된 AccessToken을 자동으로 Authentication 헤더에 붙임
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken') // 토큰 가져오기
    if(token) {
      config.headers.Authorization = `Bearer ${token}`  // 헤더에 토큰 추가
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default axiosInstance