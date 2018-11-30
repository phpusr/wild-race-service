<template>
    <div id="app">
        <v-app>
            <v-navigation-drawer v-model="drawer" app dark></v-navigation-drawer>
            <v-toolbar app dark clipped-left>
                <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
                <v-toolbar-title>Wild Race</v-toolbar-title>
            </v-toolbar>
            <v-content>
                <v-container fluid>
                    <router-view/>
                </v-container>
            </v-content>
            <v-footer app dark class="pa-3">
                <v-layout justify-center>
                    <div>Created by <a href="http://vk.com/phpusr">Sergey Doronin</a></div>
                </v-layout>
            </v-footer>
        </v-app>
    </div>
</template>

<script>
export default {
    name: 'app',
    data: () => ({
        drawer: false
    }),
}
</script>

<style>

</style>
