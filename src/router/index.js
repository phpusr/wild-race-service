import VueRouter from 'vue-router'

export default new VueRouter({ routes: [
    { path: '/', component: require('../pages/Main').default },
    { path: '/test', component: require('../pages/Test').default },
]});