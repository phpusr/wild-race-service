import Vue from 'vue'

export default {
    login: ({username, password}) => Vue.http.post('/login', {username, password}),
    logout: () => Vue.http.post('/logout')
}