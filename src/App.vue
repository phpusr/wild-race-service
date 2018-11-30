<template>
    <div id="app">
        <v-app>
            <v-navigation-drawer v-model="drawer" app dark></v-navigation-drawer>
            <v-toolbar app dark clipped-left>
                <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
                <v-toolbar-title>Wild Race</v-toolbar-title>
            </v-toolbar>
            <v-content>
                <v-flex xs11 offset-xs1 md6 offset-md3 class="mt-3">
                    <h1>{{title}}</h1>
                </v-flex>
                <v-container fluid>
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
    import msg from './i18n'

    export default {
    name: 'app',
    data() {
        return {
            drawer: false,
            title: msg.pages[this.$route.path]
        }
    },
}
</script>

<style>

</style>
