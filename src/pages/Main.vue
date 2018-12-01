<template>
    <v-flex xs12 md6 offset-md3>
        <v-layout column>
            <post v-for="post in posts" :post="post" :config="config" :key="post.id" />
        </v-layout>
    </v-flex>
</template>

<script>
    import Post from '../components/Post'

    export default {
        components: {Post},
        data: () => ({
            posts: [],
            config: {},
        }),
        created() {
            this.fetchData();
        },
        methods: {
            fetchData() {
                this.$http.get('/post').then(response => {
                    const {list, config} = response.body;
                    this.posts = list;
                    this.config = config;
                });
            }
        }
    }
</script>

<style>

</style>