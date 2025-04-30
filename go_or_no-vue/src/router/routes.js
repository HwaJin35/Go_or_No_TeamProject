import DashboardLayout from "@/layout/dashboard/DashboardLayout.vue";
// GeneralViews
import NotFound from "@/pages/NotFoundPage.vue";

// Admin pages
import Dashboard from "@/pages/Dashboard.vue";
import UserProfile from "@/pages/UserProfile.vue";
import Notifications from "@/pages/Notifications.vue";
import Icons from "@/pages/Icons.vue";
import Maps from "@/pages/Maps.vue";
import Typography from "@/pages/Typography.vue";
import MyPlace from "@/pages/MyPlace.vue";
import Notice from "@/pages/Notice.vue";
import Community from "@/pages/Community.vue";
import Favorites from "@/pages/Favorites.vue";
import Message from "@/pages/Message.vue";
import Popularity from "@/pages/Popularity.vue";
import Signup from "@/pages/Signup/Signup.vue";
import Login from "@/pages/Login.vue";


const routes = [
  {
    path: "/",
    component: DashboardLayout,
    redirect: "/maps",
    children: [
      {
        path: "maps",
        name: "📌 click on the map to drop a pin  ",
        component: Maps,
        meta: { fullMap: true }
      },
      {
        path: "my-place",
        name: "나의 장소",
        component: MyPlace,
      },
      {
        path: "notice",
        name: "공지사항",
        component: Notice,
      },
      {
        path: "community",
        name: "지역별 커뮤니티",
        component: Community,
      },
      {
        path: "popularity",
        name: "인기순 정렬",
        component: Popularity,
      },
      {
        path: "favorites",
        name: "즐겨찾기",
        component: Favorites,
      },
      {
        path: "message",
        name: "실시간 메시지",
        component: Message,
      },
      {
        path: "dashboard",
        name: "관리자 페이지",
        component: Dashboard,
      },
      {
        path: "stats",
        name: "마이페이지",
        component: UserProfile,
      },
      {
        path: "notifications",
        name: "notifications",
        component: Notifications,
      },
      {
        path: "icons",
        name: "icons",
        component: Icons,
      },
      {
        path: "typography",
        name: "typography",
        component: Typography,
      },
      {
        path: "signup",
        name: "회원가입",
        component: Signup,
      },
      {
        path: "login",
        name: "로그인",
        component: Login
      },
    ],
  },
  { path: "*", component: NotFound },
];

/**
 * Asynchronously load view (Webpack Lazy loading compatible)
 * The specified component must be inside the Views folder
 * @param  {string} name  the filename (basename) of the view to load.
function view(name) {
   var res= require('../components/Dashboard/Views/' + name + '.vue');
   return res;
};**/

export default routes;
