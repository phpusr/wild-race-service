<template>
    <div id="app">
        <v-app>
            <navigation-drawer :drawer="drawer" />
            <toolbar @click="drawer = !drawer" />
            <v-content>
                <v-container fluid class="pa-1">
                    <v-flex md6 offset-md3 class="mt-3">
                        <h1>{{title}}</h1>
                    </v-flex>
                    <router-view/>
                </v-container>
            </v-content>
            <v-footer dark class="pa-3">
                <v-layout justify-center>
                    <div>Created by <a href="http://vk.com/phpusr">Sergey Doronin</a></div>
                </v-layout>
            </v-footer>
        </v-app>
    </div>
</template>

<script>
    import NavigationDrawer from "./components/NavigationDrawer"
    import Toolbar from "./components/Toolbar"
    import {activityHandler, methods} from "./util/topicActivityHandler";

    export default {
        name: 'app',
        components: {NavigationDrawer, Toolbar},
        data: () => ({
            drawer: !false
        }),
        computed: {
            title() {
                return this.$t('pages')[this.$route.path]
            }
        },
        methods,
        created() {
            activityHandler(this);
        }
    }
</script>