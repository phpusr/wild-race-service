<template>
    <v-container grid-list-lg>
        <v-layout column>
            <v-flex xs6 v-for="i in 6">
                <v-card color="cyan" class="white--text">
                    <v-card-title primary-title>
                        <h3>Test</h3>
                    </v-card-title>
                </v-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
export default {
    data: () => ({

    })
}
</script>

<style>

</style>