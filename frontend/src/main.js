import Vue from 'vue'
import { sync } from 'vuex-router-sync'
import VueMeta from 'vue-meta'
import router from './router'
import store from './store'
import './assets/infoWindow.css'
import ApiService from './services/api.service'
import TokenService from './services/storage.service'
import VueChatScroll from 'vue-chat-scroll'
import VueI18n from 'vue-i18n'
import Vuetify from "vuetify";
import vuetify from '@/plugins/vuetify'
import App from './App.vue'
import hu from './locale/hu'
import en from './locale/en'
require("bootstrap/dist/css/bootstrap.min.css");
require("bootstrap/dist/js/bootstrap.min.js");

//import 'bootstrap';
//import 'bootstrap/dist/css/bootstrap.min.css';
//import 'bootstrap/dist/css/bootstrap.css';
import VeeValidate from 'vee-validate';
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import './components'
import {
  faHome,
  faUser,
  faUserPlus,
  faSignInAlt,
  faSignOutAlt, faInfoCircle
} from '@fortawesome/free-solid-svg-icons';

library.add(faHome, faUser, faUserPlus, faSignInAlt, faSignOutAlt, faInfoCircle);

Vue.config.productionTip = false

// Set the base URL of the API
ApiService.init(process.env.VUE_APP_ROOT_API)

// If token exists set header
if (TokenService.getToken() && !(TokenService.getToken() === "undefined")) {
  ApiService.setHeader();
  ApiService.mount401Interceptor();
  ApiService.setStore(store)
}

Vue.use(Vuetify)

Vue.use(VueI18n)

const messages = {
  en : en,
  hu : hu
};

const i18n = new VueI18n({
  locale: 'en', // set locale
  fallbackLocale: 'en', //set fallback locale
  messages, // set locale messages
});


Vue.use(VueMeta, {
  // optional pluginOptions
  refreshOnceOnNavigation: true
})

Vue.use(VeeValidate);
Vue.component('font-awesome-icon', FontAwesomeIcon);
Vue.use(VueChatScroll);
//Vue.use(Vuex);

sync(store, router)


//Global filters
//Vue.use(filters);



new Vue({
  i18n,
  vuetify,
  store,
  router,
  render: h => h(App)
}).$mount('#app')
