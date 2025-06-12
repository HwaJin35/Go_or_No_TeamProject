/**
 * JWT 토큰을 클라이언트에서 처리하는 유틸리티 함수
 * 
 * JWT 구조
 * - "Header.Payload.Signature" 형태의 문자열
 * - 각 부분은 Base64URL로 인코딩 되어있다.
 * - Payload 부분에 실제 사용자 데이터(Claimes)가 들어있다.
 */

/**
 * JWT 토큰을 파싱하여 페이로드(Claims) 데이터 추출 함수
 * 
 * 1. JWT 토큰을 '.'으로 분리하여 Header, Payload, Signature로 나눔
 * 2. Payload 부분을 Base64 디코딩
 * 3. 디코딩된 JSON 문자열을 JavaScript 객체로 변환
 * 
 * @param {string} token  - 파싱할 JWT 토큰 문자열
 * @returns {Object|null} - 토큰의 페이로드 데이터 또는 null(실패 시)
 */
export function parseJwt(token) {
  try {
    // 토큰이 없거나 빈 문자열인 경우 null 반환
    if(!token) return null; 

    // Header.Payload.Signature 구조에서
    // split('.') [1]로 Payload 부분만 추출 
    const base64Url = token.split('.')[1];

    // Base64URL을 표준 Base64로 변환
    // Base64URL은 URL-safe를 위해 '+' -> '-', '/' -> '_'로 치환된 형태
    // 치환된 것을 원래대로 되돌리는 과정
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

    // Base64 디코딩 후 URL 인코딩 처리
    // atob() : Base64 문자열을 바이너리 문자열로 디코딩
    const jsonPayload = decodeURIComponent(
      atob(base64)    // Base64 디코딩
        .split('')    // 각 문자를 배열로 분리
        .map(c => {
          // 각 문자를 URL 인코딩 형태(%XX)로 변환
          // charCodeAt(0): 문자의 ASCII 코드 값 추출
          // toString(16): 16진수로 변환
          // slice(-2) : 마지막 2자리만 추출(01, 02 형태로 만들기 위해)
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join('')   // 배열을 다시 문자열로 합침
    );

    // JSON 문자열을 JavaScript 객체로 파싱하여 변환
    return JSON.parse(jsonPayload);
  } catch (error) {
    // 파싱 중 오류 발생 시 콘솔에 에러 출력 후 null 반환
    console.error('토큰 파싱 실패', error);
    return null;
  }
}