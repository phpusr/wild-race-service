<template>
    <v-card class="my-2">
        <v-card-title primary-title>
            <v-flex xs12>
                <v-layout align-center>
                        <h3><a :href="post.link">#{{post.number}}</a> {{post.from.lastName}} {{post.from.firstName}}</h3>
                        <span class="ml-1 grey--text lighten-3">({{post.date}})</span>
                        <post-parser-status :status-id="post.statusId" class="ml-2" />
                        <v-spacer></v-spacer>
                        <v-btn icon>
                            <v-icon>edit</v-icon>
                        </v-btn>
                </v-layout>
                <div class="mt-3 display-1 blue--text font-weight-bold">+{{post.distance}}</div>
                <div class="display-1 green--text">{{post.sumDistance}}</div>
                <div class="mt-3 font-italic">{{post.text}}</div>
            </v-flex>
        </v-card-title>
    </v-card>
</template>

<script>
    import PostParserStatus from './PostParserStatus'

    export default {
    components: {PostParserStatus},
    props: {
        post: Object
    }
}
</script>
