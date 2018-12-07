<template>
    <v-flex xs6>
        <v-container grid-list-sm fluid>
            <v-layout wrap>
                <v-flex d-flex v-for="i in list" :key="i" xs4>
                    <v-card>
                        <v-card-text>{{i}}</v-card-text>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-flex>
</template>

<script>
export default {
    data: () => ({
        list: ['Which text', 'text', 'random text with too long text how to turn around your opinion', 'and more text', 'age of emperies', 'a thousand miles', 'I here without baby', 'desapear now and your face']
    })
}
</script>

<style>

</style>