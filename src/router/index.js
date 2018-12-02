import VueRouter from 'vue-router'
import MainPage from '../pages/Main'
import TestPage from '../pages/Test'
import PostDialog from '../components/PostDialog'

export default new VueRouter({ routes: [
    { path: '/', component: MainPage,
        children: [
            { path: 'post/:postId/edit', component: PostDialog }
        ]
    },
    { path: '/test', component: TestPage },
]});