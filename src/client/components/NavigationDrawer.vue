<template>
    <v-navigation-drawer v-model="drawer" clipped app dark>
        <v-flex xs11>
            <v-container class="pr-0">
                <router-view name="menu" />
                <v-layout row wrap>
                    <v-flex xs12>
                        <span class="subheading grey--text text--lighten-1">
                            <span class="font-weight-medium">{{$t('post.lastSyncDate')}}: </span>
                            <span>{{lastSyncDate}}</span>
                        </span>
                    </v-flex>
                </v-layout>
            </v-container>
        </v-flex>
    </v-navigation-drawer>
</template>

<script>
    import {mapState} from 'vuex'

    export default {
        props: {
            drawer: Boolean
        },
        computed: mapState(['lastSyncDate'])
    }
</script>
