import Vue from 'vue'
import App from './App.vue'
import Vuetify from 'vuetify'
import VueRouter from 'vue-router'
import VueResource from 'vue-resource'
import VueI18n from 'vue-i18n'
import router from './router'
import messages from './i18n'

import 'vuetify/dist/vuetify.min.css'

Vue.config.productionTip = false;

Vue.use(Vuetify);
Vue.use(VueRouter);
Vue.use(VueResource);
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
