<template>
    <v-flex sm6>
        <v-card color="cyan darken-2" class="white--text">
            <v-layout>
                <v-flex xs5>
                    <v-img
                            src="https://cdn.vuetifyjs.com/images/cards/foster.jpg"
                            height="125px"
                            contain
                    ></v-img>
                </v-flex>
                <v-flex xs7>
                    <v-card-title primary-title>
                        <div>
                            <div class="headline">Supermodel</div>
                            <div>Foster the People</div>
                            <div>(2014)</div>
                        </div>
                    </v-card-title>
                </v-flex>
            </v-layout>
            <v-divider light></v-divider>
            <v-card-actions class="pa-3">
                Rate this album
            </v-card-actions>
        </v-card>
    </v-flex>
</template>

<script>
export default {
    data: () => ({

    })
}
</script>

<style>

</style>