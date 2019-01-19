import Vue from 'vue'

export default {
    get: (params) => Vue.http.get('/stat', {params})
}