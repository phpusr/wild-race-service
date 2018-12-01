<template>
    <v-flex md6 offset-md3>
        <v-container grid-list-lg>
            <v-layout justify-center text-xs-center>
                <v-flex v-for="v in stat">
                    <v-card class="py-3">
                        <div class="display-1">{{v.value}}</div>
                        <div>{{v.title}}</div>
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

    export default {
    components: {Post},
    data: () => ({
        posts: [],
        numberOfRuns: 0,
        sumDistance: 0,
        total: 0
    }),
    created() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            this.$http.get('/post').then(response => {
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
                {title: 'Всего пробежали (км)', value: this.sumDistance},
                {title: 'Количество пробежек', value: this.numberOfRuns},
                {title: 'Количество постов', value: this.total}
            ]
        }
    }
}
</script>

<style>

</style>