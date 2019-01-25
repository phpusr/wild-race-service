<template>
    <div id="app">
        <v-app>
            <navigation-drawer v-model="drawer" />
            <toolbar @click="drawer = !drawer" />
            <v-content>
                <v-container fluid class="pa-1">
                    <v-flex md6 offset-md3 class="mt-3">
                        <h1 v-html="title"></h1>
                    </v-flex>
                    <router-view/>
                </v-container>
            </v-content>
            <app-footer />
        </v-app>
    </div>
</template>

<script>
    import NavigationDrawer from "./components/NavigationDrawer"
    import Toolbar from "./components/Toolbar"
    import Footer from "./components/Footer"
    import {activityHandler, methods} from "./util/topicActivityHandler"

    export default {
        name: "app",
        components: {NavigationDrawer, Toolbar, AppFooter: Footer},
        data() {
            return {
                drawer: this.$vuetify.breakpoint.mdAndUp
            }
        },
        computed: {
            title() {
                return this.$t("pages")[this.$route.path]
            }
        },
        methods,
        created() {
            activityHandler(this)
        }
    }
</script>