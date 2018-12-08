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
        methods: {
            fetchData() {
                const params = this.$route.query;
                this.$http.get('/stat', {params}).then(response => console.log(response))
            }
        }
    }
</script>