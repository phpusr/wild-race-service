<template>
    <v-card class="my-2">
        <v-card-title primary-title>
            <div>
                <h3 class="headline mb-0"><a :href="post.link">#{{post.number}}</a> {{post.from.lastName}} {{post.from.firstName}}</h3>
                <div>{{post.text}}</div>
            </div>
        </v-card-title>

        <v-card-actions v-if="false">
            <v-btn flat color="orange">Edit</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
export default {
    props: {
        post: Object
    }
}
</script>

<style>

</style>