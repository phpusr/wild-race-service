<template>
    <v-flex xs11>
        <v-container class="pr-0">
            <v-layout row wrap>
                <v-flex class="mb-2">
                    <span class="headline grey--text text--lighten-5">{{$t('post.filter')}}</span>
                </v-flex>
                <v-flex>
                    <v-select
                            :label="$t('post.status')"
                            :value="this.$route.query.statusId"
                            @change="changeQuery('statusId', $event)"
                            :items="statuses"
                            solo
                            clearable
                    >
                        <template slot="item" slot-scope="{ item }">
                            <span>
                                <v-icon :color="item.color">{{item.icon}}</v-icon>
                                <span class="ml-2">{{item.text}}</span>
                            </span>
                        </template>
                    </v-select>
                </v-flex>
                <v-flex>
                    <v-checkbox
                            :label="$t('post.manualEditing')"
                            :value="$route.query.manualEditing"
                            @change="changeQuery('manualEditing', $event)"
                    />
                </v-flex>
            </v-layout>
        </v-container>
    </v-flex>
</template>

<script>
    import {postStatusColors, postStatusIcons} from "../util/data"

    export default {
        computed: {
            statuses() {
                const statuses = this.$t('post.statuses');
                return Object.keys(statuses).map(key => (
                    { value: key, text: statuses[key], icon: postStatusIcons[key], color: postStatusColors[key] }
                ))
            }
        },
        methods: {
            changeQuery(name, value) {
                const query = { ...this.$route.query, [name]: value || undefined };
                this.$router.push({ path: '/', query });
            }
        }
    }
</script>