<template>
    <v-card class="my-2">
        <v-card-title primary-title>
            <div>
                <h3 class="headline mb-0">{{from.lastName}} {{from.firstName}}</h3>
                <div>{{text}}</div>
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
        from: Object,
        text: String
    }
}
</script>

<style>

</style>