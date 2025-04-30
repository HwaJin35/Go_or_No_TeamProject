export function parseJwt(token) {
  try {
    if(!token) return null; 

    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/'); // base64 padding 대응
    const jsonPayload = decodeURIComponent(
      atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join('')
    );

    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('토큰 파싱 실패', error);
    return null;
  }
}