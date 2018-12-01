<template>
    <v-flex xs12 md6 offset-md3>
        <v-layout justify-center>
            <v-card v-for="v in stat" class="mx-1 pa-3 text-xs-center">
                <div class="display-1">{{v.value}}</div>
                <div>{{v.title}}</div>
            </v-card>
        </v-layout>
        <v-layout column>
            <post v-for="post in posts" :post="post" :key="post.id" />
        </v-layout>
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