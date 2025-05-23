module.exports = {
  lintOnSave: false,
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8090', // 백엔드 포트
        changeOrigin: true,
      },
    },
  },
};
