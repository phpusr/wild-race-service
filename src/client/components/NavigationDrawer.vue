<template>
    <v-navigation-drawer :value="value" @input="$emit('input', $event)" clipped app dark>
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

                        <v-list-tile v-if="userIsAdmin" @click="syncPosts">
                            <v-list-tile-action>
                                <v-icon>sync</v-icon>
                            </v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>{{$t("sync.title")}}</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile v-if="userIsAdmin" to="/config">
                            <v-list-tile-action>
                                <v-icon>settings</v-icon>
                            </v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>{{$t("pages./config")}}</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile to="/stat">
                            <v-list-tile-action>
                                <v-icon>signal_cellular_alt</v-icon>
                            </v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>{{$t("pages./stat")}}</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile :href="config.groupLink" target="_blank" exact>
                            <v-list-tile-action class="font-weight-bold">VK</v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>{{$t("pages.groupTitle")}}</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile>
                            <v-list-tile-action>
                                <span class="subheading grey--text text--lighten-1">
                                    <span class="font-weight-medium">{{$t("post.lastSyncDate")}}: </span>
                                    <span>{{lastSyncDate}}</span>
                                </span>
                            </v-list-tile-action>
                        </v-list-tile>

                        <v-list-tile>
                            <v-list-tile-action>
                                <router-view name="menu" />
                            </v-list-tile-action>
                        </v-list-tile>
                    </v-list>
                </v-toolbar>
            </v-container>
        </v-flex>

        <v-flex xs11>
            <v-container class="pr-0">

            </v-container>
        </v-flex>
    </v-navigation-drawer>
</template>

<script>
    import {mapActions, mapGetters, mapState} from "vuex"
    import LoginDialog from "./LoginDialog"

    export default {
        components: {LoginDialog},
        props: {
            value: Boolean
        },
        data: () => ({
            defaultAvatar: "https://www.yourfirstpatient.com/assets/default-user-avatar-thumbnail@2x-ad6390912469759cda" +
                "3106088905fa5bfbadc41532fbaa28237209b1aa976fc9.png"
        }),
        computed: {
            ...mapState(["user", "lastSyncDate", "config"]),
            ...mapGetters(["userIsAdmin"])
        },
        methods: mapActions(["logoutAction", "syncPosts"])
    }
</script>
