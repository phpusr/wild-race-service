<template>
    <v-flex xs11>
        <v-select
                :label="$t('post.status')"
                :value="this.$route.query.statusId"
                @change="changeQuery('statusId', $event)"
                :items="statuses"
                prepend-icon="list"
                solo
        />
        <v-checkbox
                :label="$t('post.manualEditing')"
                :value="$route.query.manualEditing"
                @change="changeQuery('manualEditing', $event)"
        ></v-checkbox>
        <v-list dense>
            <v-list-tile>
                <v-list-tile-content>
                </v-list-tile-content>
            </v-list-tile>

        </v-list>
    </v-flex>
</template>

<script>
    export default {
        computed: {
            statuses() {
                const statuses = this.$t('post.statuses');
                return Object.keys(statuses).map(key => (
                    { value: key, text: statuses[key] }
                ))
            }
        },
        methods: {
            changeQuery(name, value) {
                const query = { ...this.$route.query, [name]: value };
                this.$router.push({ path: '/', query });
            }
        }
    }
</script>