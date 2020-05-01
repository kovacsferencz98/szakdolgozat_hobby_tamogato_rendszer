import Vue from 'vue';
import Vuex from 'vuex';

import auth  from './modules/auth.module';
import drawer  from './modules/drawer.module';
import user  from './modules/user.module';
import userDetails  from './modules/userDetails.module';
import location  from './modules/location.module';
import eventType  from './modules/eventType.module';
import event  from './modules/event.module';
import eventParticipant from './modules/eventParticipant.module'

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    auth,
    drawer,
    user,
    userDetails,
    location,
    eventType,
    event,
    eventParticipant
  },
  actions : {
    logout({dispatch}) {
      dispatch('auth/logout');
    }
  }
});
