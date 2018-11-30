<template>
    <v-layout>
        <v-flex xs12 md6 offset-md3>
            <h1>Main page</h1>

            <v-layout column>
                <v-flex v-for="post in posts" :key="post.id">
                    <v-card class="my-2">
                        <v-card-title primary-title>
                            <div>
                                <h3 class="headline mb-0">{{post.from.lastName}} {{post.from.firstName}}</h3>
                                <div>{{post.text}}</div>
                            </div>
                        </v-card-title>

                        <v-card-actions v-if="false">
                            <v-btn flat color="orange">Edit</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-flex>
    </v-layout>
</template>

<script>
    export default {
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