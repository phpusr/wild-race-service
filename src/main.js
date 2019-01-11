import Vue from 'vue'
import './api/resource'
import App from './App.vue'
import Vuetify from 'vuetify'
import VueRouter from 'vue-router'
import VueI18n from 'vue-i18n'
import router from './router'
import messages from './i18n'
import {connectToWS} from './util/ws'

import 'vuetify/dist/vuetify.min.css'

connectToWS();

Vue.config.productionTip = false;

Vue.use(Vuetify);
Vue.use(VueRouter);
Vue.use(VueI18n);

const i18n = new VueI18n({
    locale: 'ru',
    messages,
});

new Vue({
    render: h => h(App),
    router,
    i18n
}).$mount('#app');
