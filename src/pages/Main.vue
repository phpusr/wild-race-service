<template>
    <v-flex md6 offset-md3>
        <router-view />
        <v-container v-bind="containerConfig" class="pa-0">
            <v-layout text-xs-center>
                <v-flex d-flex v-for="v in stat" :key="v.title">
                    <v-card>
                        <v-card-text>
                            <div class="display-1">{{v.value}}</div>
                            <div>{{v.title}}</div>
                        </v-card-text>
                    </v-card>
                </v-flex>
            </v-layout>

            <v-layout column>
                <post v-for="post in posts" :post="post" :key="post.id" />
                <infinite-loading :identifier="infiniteId"  @infinite="infiniteHandler" />
            </v-layout>
        </v-container>
    </v-flex>
</template>

<script>
    import Post from '../components/Post'
    import {addHandler, sendData} from '../util/ws'
    import {deleteObject, replaceObject} from '../util/collections'
    import InfiniteLoading from 'vue-infinite-loading'

    export default {
        components: {Post, InfiniteLoading},
        data: () => ({
            posts: [],
            numberOfRuns: 0,
            sumDistance: 0,
            total: 0,
            page: 1,
            infiniteId: +new Date()
        }),
        created() {
            addHandler('/topic/updatePost', post =>
                replaceObject(this.posts, post)
            );
            addHandler('/topic/deletePost', id => {
                deleteObject(this.posts, id)
            });

            sendData('/app/getLastSyncDate');
        },
        beforeRouteUpdate (to, from, next) {
            next();
            if (JSON.stringify(to.query) !== JSON.stringify(from.query)) {
                this.resetData();
            }
        },
        methods: {
            resetData() {
                this.posts = [];
                this.numberOfRuns = 0;
                this.sumDistance = 0;
                this.total = 0;
                this.page = 1;
                this.infiniteId += 1;
            },
            infiniteHandler($state) {
                const params = this.$route.query;
                this.$http.get('/post', {
                    params: {
                        ...params,
                        page: this.page,
                    },
                }).then(response => {
                    const {list, numberOfRuns, sumDistance, total} = response.body;
                    if (list.length) {
                        this.page += 1;
                        this.posts.push(...list);
                        this.numberOfRuns = numberOfRuns;
                        this.sumDistance = sumDistance;
                        this.total = total;
                        $state.loaded();
                    } else {
                        $state.complete();
                    }
                });
            },
        },
        computed: {
            stat() {
                return [
                    {title: this.$t('post.totalSumDistance'), value: this.sumDistance},
                    {title: this.$t('post.numberOfRuns'), value: this.numberOfRuns},
                    {title: this.$t('post.numberOfPosts'), value: this.total}
                ]
            },
            containerConfig() {
                return {
                    ['grid-list-' + this.$vuetify.breakpoint.name]: true
                };
            }
        }
    }
</script>

<style>

</style>