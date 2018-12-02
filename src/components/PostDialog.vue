<template>
    <v-dialog v-model="show" persistent width="500">
        <v-card>
            <v-card-title class="headline grey lighten-2">Title</v-card-title>

            <v-card-text>
                <v-form v-model="valid">
                    <v-text-field
                            v-model="post.number"
                            label="Number"
                    />
                    <v-select
                            :value="post.statusId + ''"
                            @change="post.statusId = +$event"
                            :items="statuses"
                            label="Status"
                            required
                    />
                    <v-text-field
                            v-model="post.distance"
                            label="Distance"
                    />
                    <v-text-field
                            v-model="post.sumDistance"
                            label="Sum distance"
                    />
                </v-form>
            </v-card-text>

            <v-divider/>

            <v-card-actions>
                <v-spacer/>
                <router-link to="/" tag="span">
                    <v-btn>Close</v-btn>
                </router-link>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {post} from '../i18n'

    export default {
        data() {
            return {
                valid: false,
                post: {},
            }
        },
        computed: {
            show() {
                return !!this.$route.params.postId
            },
            statuses() {
                return Object.keys(post.status).map(key => ({value: key, text: post.status[key]}))
            }
        },
        methods: {
            changeSelect(e) {
                console.log(e, this.post)
            }
        }
    }
</script>