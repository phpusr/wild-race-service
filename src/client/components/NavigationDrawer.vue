<template>
    <v-navigation-drawer v-model="drawer" clipped app dark>
        <v-flex>
            <v-container class="">
                <v-toolbar flat class="transparent">
                    <v-list class="pa-0">
                        <v-list-tile avatar>
                            <v-list-tile-avatar>
                                <img :src="defaultAvatar"  alt="Default avatar" />
                            </v-list-tile-avatar>

                            <v-list-tile-content>
                                <v-list-tile-title v-if="user">{{user.username}}</v-list-tile-title>
                                <login-dialog v-else />
                            </v-list-tile-content>

                            <v-btn v-if="user" flat icon>
                                <v-icon @click="logoutAction">exit_to_app</v-icon>
                            </v-btn>
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
    import {mapActions, mapState} from 'vuex'
    import LoginDialog from './LoginDialog'

    export default {
        components: {LoginDialog},
        props: {
            drawer: Boolean
        },
        data: () => ({
            defaultAvatar: 'https://www.yourfirstpatient.com/assets/default-user-avatar-thumbnail@2x-ad6390912469759cda3106088905fa5bfbadc41532fbaa28237209b1aa976fc9.png'
        }),
        computed: mapState(['user', 'lastSyncDate']),
        methods: mapActions(['logoutAction'])
    }
</script>
