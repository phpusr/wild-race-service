<template>
    <div id="app">
        <v-app>
            <v-navigation-drawer v-model="drawer" clipped app dark>
                <router-view name="menu" />
            </v-navigation-drawer>
            <v-toolbar app dark clipped-left>
                <v-toolbar-side-icon @click.stop="drawer = !drawer" />
                <v-btn flat to="/" class="text-capitalize">
                    <v-toolbar-title>
                        <span class="font-weight-regular">Wild</span>
                        <span class="font-weight-bold">Race</span>
                    </v-toolbar-title>
                </v-btn>

                <v-toolbar-items>
                    <v-btn flat @click="sync">{{$t('sync.title')}}</v-btn>
                </v-toolbar-items>

                <v-toolbar-items>
                    <v-btn flat to="/config">{{$t('pages./config')}}</v-btn>
                </v-toolbar-items>

                <v-toolbar-items>
                    <v-btn flat to="/stat">{{$t('pages./stat')}}</v-btn>
                </v-toolbar-items>

                <v-spacer />
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
    import postApi from "./api/post"
    import {mapMutations} from "vuex"
    import {addHandler} from "./util/ws";

    export default {
        name: 'app',
        data() {
            return {
                drawer: false,
            }
        },
        computed: {
            title() {
                return this.$t('pages')[this.$route.path]
            }
        },
        methods: {
            ...mapMutations(['addPostMutation', 'updatePostMutation', 'removePostMutation', 'updatePostStatMutation']),
            async sync() {
                try {
                    await postApi.sync();
                    alert(this.$t('sync.success'));
                } catch(e) {
                    alert(`${e.status}: ${e.body.error} on "${e.url}"`);
                }
            }
        },
        created() {
            addHandler('/topic/activity', data => {
                if (data.objectType === 'Post') {
                    switch(data.eventType) {
                        case 'Create':
                            this.addPostMutation(data.body);
                            break;
                        case 'Update':
                            this.updatePostMutation(data.body);
                            break;
                        case 'Remove':
                            this.removePostMutation(data.body);
                            break;
                        default:
                            throw new Error(`Looks like the event type is unknown: "${data.eventType}"`);
                    }
                } else if (data.objectType === 'Stat') {
                    this.updatePostStatMutation(data.body);
                } else {
                    throw new Error(`Looks like the object type is unknown: "${data.objectType}"`);
                }
            });
        }
    }
</script>

<style>

</style>
