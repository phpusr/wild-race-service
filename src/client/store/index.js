import Vue from 'vue'
import Vuex from 'vuex'
import {deleteObject, replaceObject} from "../util/collections";
import dateFormat from "date-format"

Vue.use(Vuex);

function formatDate(date) {
    return dateFormat('hh:mm dd.MM.yyyy', new Date(date))
}

export default new Vuex.Store({
    state: {
        post: {
            posts: [],
            totalElements: 0,
            stat: document.frontendData.stat,
        },
        lastSyncDate: formatDate(document.frontendData.lastSyncDate)
    },
    getters: {},
    mutations: {
        addPostMutation(state, post) {
            const data = state.post;
            const index = data.posts.findIndex(el => el.id === post.id);
            if (index === -1) {
                data.posts.unshift(post);
                data.posts.sort((a, b) => b.date - a.date);
                data.totalElements++;
            } else {
                replaceObject(data.posts, post);
            }
        },
        addPostsMutation(state, {list, totalElements}) {
            state.post.totalElements = totalElements;
            if (list.length) {
                state.post.posts.push(...list);
            }
        },
        resetPostsMutation(state) {
            state.post.posts = [];
        },
        updatePostMutation(state, post) {
            replaceObject(state.post.posts, post);
        },
        removePostMutation(state, post) {
            if (deleteObject(state.post.posts, post.id)) {
                this.totalElements--;
            }
        },
        updatePostStatMutation(state, stat) {
            state.post.stat = stat;
        },
        updateLastSyncDateMutation(state, date) {
            state.lastSyncDate = formatDate(date);
        }
    },
    actions: {}
})