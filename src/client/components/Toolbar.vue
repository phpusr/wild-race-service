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

    export default {
        computed: mapGetters(["userIsAdmin"]),
        methods: {
            async sync() {
                if (confirm(this.$t("sync.confirm"))) {
                    try {
                        await postApi.sync();
                        alert(this.$t("sync.success"));
                    } catch(e) {
                        alert(`${e.status}: ${e.body.error} on "${e.url}"`);
                    }
                }
            }
        },
    }
</script>