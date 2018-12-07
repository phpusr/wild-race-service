import VueRouter from 'vue-router'
import MainPage from '../pages/MainPage'
import ConfigPage from '../pages/ConfigPage'
import TestPage from '../pages/TestPage'
import PostDialog from '../components/PostDialog'
import PostFilter from '../components/PostFilter'

export default new VueRouter({ routes: [
    {
        path: '/',
        components: {
            default: MainPage,
            menu: PostFilter,
        },
        children: [
            { path: 'post/:postId/edit', component: PostDialog }
        ]
    },
    {
        path: '/config',
        component: ConfigPage,
        children: [
            { path: 'edit' }
        ]
    },
    { path: '/test', component: TestPage },
]});