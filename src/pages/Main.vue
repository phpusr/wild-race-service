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
            </v-layout>
        </v-container>
    </v-flex>
</template>

<script>
    import Post from '../components/Post'
    import {addHandler} from '../util/ws'
    import {deleteObject, replaceObject} from '../util/collections'

    export default {
        components: {Post},
        data: () => ({
            posts: [],
            numberOfRuns: 0,
            sumDistance: 0,
            total: 0
        }),
        created() {
            addHandler('/topic/updatePost', post =>
                replaceObject(this.posts, post)
            );
            addHandler('/topic/deletePost', id => {
                deleteObject(this.posts, id)
            });
            this.fetchData();
        },
        beforeRouteUpdate (to, from, next) {
            next();
            this.fetchData();
        },
        methods: {
            fetchData() {
                const params = this.$route.query;
                this.$http.get('/post', {params}).then(response => {
                    const {list, numberOfRuns, sumDistance, total} = response.body;
                    this.posts = list;
                    this.numberOfRuns = numberOfRuns;
                    this.sumDistance = sumDistance;
                    this.total = total;
                });
            }
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