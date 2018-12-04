<template>
    <v-flex md6 offset-md3>
        <router-view />
        <v-container v-bind="containerConfig" class="pa-0">
            <v-layout text-xs-center>
                <v-flex d-flex v-for="v in statTitles" :key="v.title">
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
                <infinite-loading :identifier="infiniteId"  @infinite="infiniteHandler">
                    <div slot="no-more">{{$t('post.noMoreMessages')}}</div>
                    <div slot="no-results">{{$t('post.noResults')}}</div>
                </infinite-loading>
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
            page: 1,
            infiniteId: +new Date(),
            stat: {
                numberOfRuns: 0,
                sumDistance: 0,
                total: 0
            }
        }),
        created() {
            addHandler('/topic/updatePost', post =>
                replaceObject(this.posts, post)
            );
            addHandler('/topic/deletePost', id => {
                deleteObject(this.posts, id)
            });

            sendData('/app/getLastSyncDate');
            this.updateStat();
        },
        beforeRouteUpdate (to, from, next) {
            next();

            if (JSON.stringify(to.query) !== JSON.stringify(from.query)) {
                this.resetData();
            }

            if (to.fullPath !== from.fullPath) {
                this.updateStat();
            }
        },
        methods: {
            resetData() {
                this.posts = [];
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
                    const list = response.body;
                    if (list.length) {
                        this.page += 1;
                        this.posts.push(...list);
                        $state.loaded();
                    } else {
                        $state.complete();
                    }
                });
            },
            updateStat() {
                const params = this.$route.query;
                this.$http.get('/post/getStat', {params}).then(response => {
                    this.stat = response.body;
                });
            }
        },
        computed: {
            statTitles() {
                return [
                    {title: this.$t('post.totalSumDistance'), value: this.stat.sumDistance},
                    {title: this.$t('post.numberOfRuns'), value: this.stat.numberOfRuns},
                    {title: this.$t('post.numberOfPosts'), value: this.stat.total}
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