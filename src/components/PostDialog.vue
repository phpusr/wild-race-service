<template>
    <v-dialog v-model="show" persistent scrollable width="500">
        <v-card>
            <v-card-title class="headline grey lighten-2">{{$t('post.editDialogTitle')}}</v-card-title>

            <v-card-text>
                <v-form v-model="valid">
                    <v-text-field
                            v-model="post.number"
                            :label="$t('post.number')"
                    />
                    <v-select
                            :value="post.statusId + ''"
                            @change="post.statusId = +$event"
                            :items="statuses"
                            :label="$t('post.status')"
                            required
                    />
                    <v-text-field
                            v-model="post.distance"
                            :label="$t('post.distance')"
                    />
                    <v-text-field
                            v-model="post.sumDistance"
                            :label="$t('post.sumDistance')"
                    />
                    <v-textarea
                            v-model="post.editReason"
                            :label="$t('post.editReason')"
                    />
                </v-form>
            </v-card-text>

            <v-divider/>

            <v-card-actions>
                <v-btn color="error" @click="remove">Delete</v-btn>
                <v-spacer/>
                <v-btn color="primary" @click="update">Save</v-btn>
                <v-btn @click="$router.push('/')">Close</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {sendData} from "../util/ws"

    export default {
        data() {
            return {
                valid: false,
                post: {},
            }
        },
        created() {
            this.fetchData();
        },
        computed: {
            postId() {
                return this.$route.params.postId
            },
            show() {
                return !!this.postId
            },
            statuses() {
                const statuses = this.$t('post.statuses');
                return Object.keys(statuses).map(key => (
                    { value: key, text: statuses[key] }
                ))
            }
        },
        methods: {
            fetchData() {
                this.$http.get(`/post/${this.postId}`).then(response => {
                    this.post = response.body;
                });
            },
            update() {
                sendData('/app/updatePost', this.post);
                this.$router.push('/')
            },
            remove() {
                if (confirm(this.$t('post.confirmDelete'))) {
                    sendData('/app/deletePost', this.post.id);
                    this.$router.push('/')
                }
            }
        }
    }
</script>