import Vue from 'vue'

const api = Vue.resource('/post/{id}', {}, {
    getAll: {method: 'GET', url: '/post?page={page}&statusId={statusId}&manualEditing={manualEditing}'}
});

export default {
    getOne: id => api.get({id}),
    getAll: params => api.getAll(params),
    getStat: () => api.get({id: 'getStat'}, {params: {test: 1}}),
    getLastSyncDate: () => api.get({id: 'getLastSyncDate'}),
    update: post => api.update({id: post.id}, post),
    remove: id => api.remove({id}),
    sync: () => api.get({id: 'sync'})
}