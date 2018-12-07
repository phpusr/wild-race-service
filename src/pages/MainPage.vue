<template>
    <v-flex md6 offset-md3>
        <router-view />
        <v-container v-bind="containerConfig" class="pa-0">
            <v-layout text-xs-center>
                <v-flex d-flex xs4 v-for="v in statTitles" :key="v.title">
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
    import {addHandler} from '../util/ws'
    import {deleteObject, replaceObject} from '../util/collections'
    import InfiniteLoading from 'vue-infinite-loading'

    export default {
        components: {Post, InfiniteLoading},
        data: () => ({
            posts: [],
            totalElements: 0,
            page: 0,
            infiniteId: +new Date(),
            stat: {
                numberOfRuns: 0,
                sumDistance: 0,
                total: 0
            }
        }),
        created() {
            addHandler('/topic/updatePost', post => {
                replaceObject(this.posts, post);
                this.updateStat();
            });
            addHandler('/topic/deletePost', id => {
                if (deleteObject(this.posts, id)) {
                    this.totalElements--;
                    this.updateStat();
                }
            });

            this.updateStat();
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
                this.page = 0;
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
                    const {list, totalElements} = response.body;
                    this.totalElements = totalElements;
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
                this.$http.get('/post/getStat').then(response => {
                    this.stat = response.body;
                });
            }
        },
        computed: {
            statTitles() {
                const numberOfPostsString = (this.totalElements === this.stat.numberOfPosts) ? this.stat.numberOfPosts : `${this.totalElements} / ${this.stat.numberOfPosts}`;
                return [
                    {title: this.$t('post.totalSumDistance'), value: this.stat.sumDistance},
                    {title: this.$t('post.numberOfRuns'), value: this.stat.numberOfRuns},
                    {title: this.$t('post.numberOfPosts'), value: numberOfPostsString}
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