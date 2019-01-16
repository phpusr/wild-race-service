import Vue from 'vue'

const api = Vue.resource('/post/{id}', {}, {
    getAll: {method: 'GET', url: '/post?page={page}&statusId={statusId}&manualEditing={manualEditing}'},
    update: {method: 'PUT', url: '/post/{id}?updateNextPosts={updateNextPosts}'},
    remove: {method: 'DELETE', url: '/post/{id}?updateNextPosts={updateNextPosts}'}
});

export default {
    getOne: id => api.get({id}),
    getAll: params => api.getAll(params),
    getStat: () => api.get({id: 'getStat'}, {params: {test: 1}}),
    getLastSyncDate: () => api.get({id: 'getLastSyncDate'}),
    update: (post, updateNextPosts) => api.update({id: post.id, updateNextPosts}, post),
    remove: (id, updateNextPosts) => api.remove({id, updateNextPosts}),
    sync: () => api.get({id: 'sync'})
}