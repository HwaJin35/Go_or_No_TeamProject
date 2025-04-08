<template>
  <footer class="footer">
    <div class="container-fluid d-flex flex-wrap justify-content-between">
      <nav>
        <ul>
          <li>
            <router-link :to="{ path: '/admin' }">Dashboard</router-link>
          </li>
        </ul>
      </nav>
      <div class="copyright d-flex flex-wrap">
        깃허브 주소는 이름을 클릭해 주세요. &copy; Coded with
        <span>
          <i class="fa fa-user" style="color: orange; margin-right: -10px"></i>
          <i class="fa fa-user" style="color: slateblue"></i>
        </span>
          by
        <a href="https://github.com/HwaJin35/Go_or_No_TeamProject" target="_blank">
          &nbsp; 정화진 & 어도하</a
        >
        <span>
          <i class="fa fa-flag"></i>
        </span>
      </div>
    </div>
  </footer>
</template>
<script>
export default {};
</script>
<style></style>