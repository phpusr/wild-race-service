import Vue from 'vue'

export default {
    async login({username, password}) {
        const formData = new FormData();
        formData.append("username" ,username);
        formData.append("password" ,password);
        return await Vue.http.post('/login', formData);
    },
    logout: () => Vue.http.post('/logout')
}