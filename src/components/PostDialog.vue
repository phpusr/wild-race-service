<template>
    <v-dialog v-model="dialog" width="500">
        <slot slot="activator" />

        <v-card>
            <v-card-title class="headline grey lighten-2" primary-title>Title</v-card-title>

            <v-card-text>Text</v-card-text>

            <v-divider></v-divider>

            <v-card-actions>
                <v-btn color="primary" @click="dialog = false">Ok</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        data () {
            return {
                dialog: false
            }
        }
    }
</script>