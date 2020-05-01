import Vue from 'vue';
import Router from 'vue-router';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import About from '../views/About.vue';
import PasswordForgotten from "../views/PasswordForgotten";
import TokenService from "../services/storage.service";
import {Role} from "../models/role";
import {AccountStorageService} from "../services/storage.service";
import ResetPassword from "../views/ResetPassword";

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      component: Home,
      meta: {
        public: true
      }
    },
    {
      path: '/about',
      component: About
    },
    {
      path: '/login',
      component: Login,
      meta: {
        public: true,  // Allow access to even if not logged in
        onlyWhenLoggedOut: true
      }
    },
    {
      path: '/forgotten-password',
      component: PasswordForgotten,
      meta: {
        public: true,  // Allow access to even if not logged in
        onlyWhenLoggedOut: true
      }
    },
    {
      path: '/reset-password/:key',
      component: ResetPassword,
      meta: {
        public: true,  // Allow access to even if not logged in
        onlyWhenLoggedOut: true
      }
    },
    {
      path: '/register',
      component: Register,
      meta: {
        public: true,  // Allow access to even if not logged in
        onlyWhenLoggedOut: true
      }
    },
    {
      path: '/change-password',
      name: 'changePassword',
      // lazy-loaded
      component: () => import('../views/ChangePassword.vue')
    },
    {
      path: '/activate-account/:key',
      name: 'changePassword',
      meta: {
        public: true,  // Allow access to even if not logged in
        onlyWhenLoggedOut: true
      },
      // lazy-loaded
      component: () => import('../views/ActivateAccount.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      // lazy-loaded
      component: () => import('../views/Profile.vue')
    },
    {
      path: '/manage-users',
      name: 'manage users',
      // lazy-loaded
      component: () => import('../views/ManageUsers.vue'),
      meta: {
        roles: [Role.admin]
      }
    },
    {
      path: '/manage-user-details',
      name: 'manage user details',
      // lazy-loaded
      component: () => import('../views/ManageUserDetails.vue'),
      meta: {
        roles: [Role.admin]
      }
    },
    {
      path: '/manage-locations',
      name: 'manage locations',
      // lazy-loaded
      component: () => import('../views/ManageLocations.vue'),
      meta: {
        roles: [Role.admin]
      }
    },
    {
      path: '/manage-event-types',
      name: 'manage event types',
      // lazy-loaded
      component: () => import('../views/ManageEventTypes.vue'),
      meta: {
        roles: [Role.admin]
      }
    },
    {
      path: '/manage-events',
      name: 'manage events',
      // lazy-loaded
      component: () => import('../views/ManageEvents.vue'),
      meta: {
        roles: [Role.admin]
      }
    },
    {
      path: '/manage-event-participants',
      name: 'manage event participants',
      // lazy-loaded
      component: () => import('../views/ManageEventParticipants.vue'),
      meta: {
        roles: [Role.admin]
      }
    },
    {
      path: '/event-view/:id',
      name: 'EventView',
      // lazy-loaded
      component: () => import('../views/EventView'),
      meta: {
      }
    },
    {
      path: '/user-profile/:id',
      name: 'UserProfile',
      // lazy-loaded
      component: () => import('../views/UserProfile'),
      meta: {
      }
    },
    {
      path: '/chat-room/:id',
      name: 'Chat',
      // lazy-loaded
      component: () => import('../views/Chat'),
      meta: {
      }
    },
    {
      path: '/user-events',
      name: 'User Events',
      // lazy-loaded
      component: () => import('../views/UserEvents'),
      meta: {
      },
    },
    {
      path: '/browse-events/:type?',
      name: 'BrowseEvents',
      // lazy-loaded
      component: () => import('../views/BrowseEvents'),
      meta: {
      }
    },
    {
      path: "/404",
      component: () => import("../views/NotFound")
    },
    {
      path: "*",
      component: () => import("../views/NotFound")
    }

  ]
});

router.beforeEach((to, from, next) => {
  const isPublic =
      to.matched.some(record => record.meta.public);
  const onlyWhenLoggedOut = to.matched.some(record => record.meta.onlyWhenLoggedOut);
  const loggedIn = !(TokenService.getToken() === "undefined") && !!TokenService.getToken();
  const requiredRoles = to.meta.roles;
  var userAccount = AccountStorageService.getAccount();

  // trying to access a restricted page + not logged in
  // redirect to login page
  if (!isPublic && !loggedIn) {
    return next({
      path:'/login',
      query: {redirect: to.fullPath}  // Store the full path to redirect the user to after login
    });
  }

  // Do not allow user to visit login page or register page if they are logged in
  if (loggedIn && onlyWhenLoggedOut) {
    return next('/')
  }

  //Do not allow user to access page restricted by role
  if(loggedIn && requiredRoles) {
    console.log('Check roles');
    console.log(requiredRoles + '  ' + userAccount.roles);
    if(!checkRoles(requiredRoles, userAccount.roles)) {
      console.log('Not authorized');
      return next('/')
    }
  }

  next();
})

function checkRoles(requiredRoles, userRoles) {
  var role;
  for(role in requiredRoles) {
    console.log('Check role: ' +  requiredRoles[role])
    if(userRoles.includes(requiredRoles[role]))
      return true;
  }

  return false;
}

export default router;