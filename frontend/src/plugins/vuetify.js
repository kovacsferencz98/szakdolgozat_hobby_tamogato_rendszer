import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import '@mdi/font/css/materialdesignicons.css'

Vue.use(Vuetify)

const opts = {}

export default new Vuetify({
    theme: {
        themes: {
            light: {
                primary: "#346C47"
            }
        }
    },
    icons: {
        iconfont: 'mdi',
        //theme// default - only for display purposes
    },
    opts})