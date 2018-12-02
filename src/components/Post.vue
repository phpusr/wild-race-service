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
                            <h3><a :href="post.link">#{{post.number}}</a> {{post.from.lastName}} {{post.from.firstName}}</h3>
                            <div class="caption grey--text lighten-3">{{date}}</div>
                        </div>
                        <post-parser-status :status-id="post.statusId" class="ml-2" />
                        <v-spacer></v-spacer>
                        <router-link :to="postEditLink" tag="span">
                            <v-btn icon>
                                <v-icon>edit</v-icon>
                            </v-btn>
                        </router-link>
                    </v-layout>
                    <div class="mt-3 display-1 blue--text font-weight-bold">+{{post.distance}}</div>
                    <div class="display-1 green--text">{{post.sumDistance}}</div>
                    <div class="mt-3 font-italic">{{post.text}}</div>
                </v-flex>
            </v-card-title>
        </v-card>
    </v-flex>
</template>

<script>
    import PostParserStatus from './PostParserStatus'
    import PostDialog from './PostDialog'
    import dateFormat from 'dateformat'

    export default {
        components: {PostParserStatus, PostDialog},
        props: {
            post: Object
        },
        computed: {
            date() {
                return dateFormat(new Date(this.post.date), 'HH:MM dd.mm.yyyy');
            },
            postEditLink() {
                return `/post/${this.post.id}/edit`
            }
        }
    }
</script>
