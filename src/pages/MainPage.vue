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
    import postApi from '../api/post'

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
            addHandler('/topic/activity', data => {
                if (data.objectType === 'Post') {
                    switch(data.eventType) {
                        case 'Update':
                            replaceObject(this.posts, data.body);
                            this.updateStat();
                            break;
                        case 'Remove':
                            if (deleteObject(this.posts, data.body.id)) {
                                this.totalElements--;
                                this.updateStat();
                            }
                            break;
                        default:
                            throw new Error(`Looks like the event type is unknown: "${data.eventType}"`);
                    }
                } else {
                    throw new Error(`Looks like the object type is unknown: "${data.objectType}"`);
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
            async infiniteHandler($state) {
                const params = this.$route.query;
                const {body} = await postApi.getAll({
                        ...params,
                        page: this.page,
                });
                const {list, totalElements} = body;
                this.totalElements = totalElements;
                if (list.length) {
                    this.page += 1;
                    this.posts.push(...list);
                    $state.loaded();
                } else {
                    $state.complete();
                }
            },
            async updateStat() {
                const {body} = await postApi.getStat();
                this.stat = body;
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