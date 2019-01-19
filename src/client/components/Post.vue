<template>
    <v-flex>
        <v-card>
            <v-card-title primary-title>
                <v-flex>
                    <v-layout align-start>
                        <v-avatar class="elevation-2">
                            <img :src="post.from.photo_50" />
                        </v-avatar>
                        <div class="ml-2">
                            <h3>
                                <a :href="post.link" target="_blank">#{{post.number}}</a>
                                {{post.from.firstName}}
                                {{post.from.lastName}}
                            </h3>
                            <div class="caption grey--text lighten-3">{{date}}</div>
                        </div>
                        <post-parser-status :status-id="post.statusId" class="ml-2" />

                        <v-tooltip top v-if="post.lastUpdate">
                            <v-btn icon class="blue-grey lighten-4" slot="activator">
                                <v-icon>how_to_reg</v-icon>
                            </v-btn>
                            <span>{{$t('post.manualEditing')}}</span>
                        </v-tooltip>

                        <v-spacer></v-spacer>
                        <v-btn icon @click="postEditHandler">
                            <v-icon>edit</v-icon>
                        </v-btn>
                    </v-layout>
                    <div v-if="post.distance" class="mt-3 display-1 blue--text font-weight-bold">+{{post.distance}}</div>
                    <div class="display-1 green--text">{{post.sumDistance}}</div>
                    <div class="mt-3 font-italic">{{post.text}}</div>
                </v-flex>
            </v-card-title>
            <div v-if="post.editReason" class="blue-grey lighten-4 pa-2">
                <span class="font-weight-medium">{{$t('post.editReason')}}:</span>
                {{post.editReason}}
            </div>
        </v-card>
    </v-flex>
</template>

<script>
    import PostParserStatus from './PostParserStatus'
    import dateFormat from 'date-format'

    export default {
        components: {PostParserStatus},
        props: {
            post: Object
        },
        computed: {
            date() {
                return dateFormat('hh:mm dd.MM.yyyy', new Date(this.post.date));
            }
        },
        methods: {
            postEditHandler() {
                this.$router.push({
                    path: `/post/${this.post.id}/edit`,
                    query: this.$route.query
                });
            }
        }
    }
</script>
