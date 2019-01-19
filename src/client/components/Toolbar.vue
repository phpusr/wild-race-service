<template>
    <v-toolbar app dark clipped-left>
        <v-toolbar-side-icon @click.stop="$emit('click')" />
        <v-btn flat to="/" class="text-capitalize">
            <v-toolbar-title>
                <span class="font-weight-regular">Wild</span>
                <span class="font-weight-bold">Race</span>
            </v-toolbar-title>
        </v-btn>

        <v-toolbar-items>
            <v-btn v-if="userIsAdmin" flat @click="sync">{{$t("sync.title")}}</v-btn>
            <v-btn v-if="userIsAdmin" flat to="/config">{{$t("pages./config")}}</v-btn>
            <v-btn flat to="/stat">{{$t("pages./stat")}}</v-btn>
        </v-toolbar-items>

        <v-spacer />
    </v-toolbar>
</template>

<script>
    import postApi from "../api/post"
    import {mapGetters} from "vuex"
    import {fetchHandler} from "../util"

    export default {
        computed: mapGetters(["userIsAdmin"]),
        methods: {
            sync() {
                if (confirm(this.$t("sync.confirm"))) {
                    postApi.sync()
                        .then(() => alert(this.$t("sync.success")))
                        .catch(fetchHandler)
                }
            }
        },
    }
</script>