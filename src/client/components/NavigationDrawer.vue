<template>
    <v-navigation-drawer v-model="drawer" clipped app dark>
        <v-flex>
            <v-container class="">
                <v-toolbar flat class="transparent">
                    <v-list class="pa-0">
                        <v-list-tile v-if="user" avatar>
                            <v-list-tile-avatar>
                                <img src="https://randomuser.me/api/portraits/men/3.jpg">
                            </v-list-tile-avatar>

                            <v-list-tile-content>
                                <v-list-tile-title>phpusr</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile v-else>
                            <v-list-tile-content>
                                <login-dialog />
                            </v-list-tile-content>
                        </v-list-tile>
                    </v-list>
                </v-toolbar>
            </v-container>
        </v-flex>

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
    import LoginDialog from './LoginDialog'

    export default {
        components: {LoginDialog},
        props: {
            drawer: Boolean
        },
        computed: mapState(['user', 'lastSyncDate'])
    }
</script>
