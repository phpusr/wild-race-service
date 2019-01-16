<template>
    <div id="app">
        <v-app>
            <navigation-drawer :drawer="drawer" />
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
    import {addHandler} from "./util/ws"
    import {isEmptyObject} from "./util/collections"
    import NavigationDrawer from "./components/NavigationDrawer"

    export default {
        name: 'app',
        components: {NavigationDrawer},
        data: () => ({
            drawer: false
        }),
        computed: {
            title() {
                return this.$t('pages')[this.$route.path]
            }
        },
        methods: {
            ...mapMutations(['addPostMutation', 'updatePostMutation', 'removePostMutation', 'updatePostStatMutation',
                'updateLastSyncDateMutation']),
            async sync() {
                if (confirm(this.$t('sync.confirm'))) {
                    try {
                        await postApi.sync();
                        alert(this.$t('sync.success'));
                    } catch(e) {
                        alert(`${e.status}: ${e.body.error} on "${e.url}"`);
                    }
                }
            }
        },
        created() {
            addHandler('/topic/activity', data => {
                const body = data.body;
                if (data.objectType === 'Post') {
                    switch(data.eventType) {
                        case 'Create':
                            if (isEmptyObject(this.$route.query)) {
                                this.addPostMutation(body);
                            }
                            break;
                        case 'Update':
                            this.updatePostMutation(body);
                            break;
                        case 'Remove':
                            this.removePostMutation(body);
                            break;
                        default:
                            throw new Error(`Looks like the event type is unknown: "${data.eventType}"`);
                    }
                } else if (data.objectType === 'Stat') {
                    this.updatePostStatMutation(body);
                } else if (data.objectType === 'LastSyncDate') {
                    this.updateLastSyncDateMutation(body);
                } else {
                    throw new Error(`Looks like the object type is unknown: "${data.objectType}"`);
                }
            });
        }
    }
</script>

<style>

</style>
