<template>
    <v-card>
        <v-card-title class="headline">{{ title }}</v-card-title>
        <v-card-text>
            <slot />
        </v-card-text>
    </v-card>
</template>

<script>
    export default {
        props: {
            title: String
        }
    }
</script>