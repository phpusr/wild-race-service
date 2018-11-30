<template>
    <v-flex xs12 md6 offset-md3>
        <v-layout column>
            <post v-for="post in posts" v-bind="post" :key="post.id" />
        </v-layout>
    </v-flex>
</template>

<script>
    import Post from '../components/Post'

    export default {
        components: {Post},
        data: () => ({
            posts: []
        }),
        created() {
            this.fetchData();
        },
        methods: {
            fetchData() {
                this.$http.get('/post').then(response => {
                    const {list} = response.body;
                    this.posts = list;
                });
            }
        }
    }
</script>

<style>

</style>