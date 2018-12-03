<template>
    <div id="app">
        <v-app>
            <v-navigation-drawer v-model="drawer" clipped app dark>
                <post-filter />
            </v-navigation-drawer>
            <v-toolbar app dark clipped-left>
                <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
                <v-toolbar-title>
                    <span class="font-weight-regular">Wild</span>
                    <span class="font-weight-bold">Race</span>
                </v-toolbar-title>
            </v-toolbar>
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
    import PostFilter from "./components/PostFilter"

    export default {
        name: 'app',
        components: {PostFilter},
        data() {
            return {
                drawer: !false,
            }
        },
        computed: {
            title() {
                return this.$t('pages')[this.$route.path]
            }
        }
    }
</script>

<style>

</style>
