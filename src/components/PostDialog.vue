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
                    <v-text-field
                            v-model="post.statusId"
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

            <v-card-actions>
                <v-divider></v-divider>
                <router-link to="/" tag="span">
                    <v-btn>Close</v-btn>
                </router-link>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        data() {
            return {
                valid: false,
                post: {}
            }
        },
        computed: {
            show() {
                return !!this.$route.params.postId
            }
        }
    }
</script>