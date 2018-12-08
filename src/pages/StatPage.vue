<template>
    <v-flex md6 offset-md3>
        <v-layout>
            <stat-filter />
        </v-layout>
    </v-flex>
</template>

<script>
    import StatFilter from '../components/StatFilter'

    export default {
        components: {StatFilter},
        data: () => ({

        }),
        created() {
            this.fetchData()
        },
        beforeRouteUpdate (to, from, next) {
            next();

            this.fetchData();
        },
        methods: {
            fetchData() {
                const {params} = this.$route;
                this.$http.get('/stat', {params}).then(response => console.log(response))
            }
        }
    }
</script>