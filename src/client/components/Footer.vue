<template>
    <v-footer dark class="pa-4">
        <v-layout justify-center>
            <div class="mt-1 font-weight-bold">{{$t("default.projectName")}} v{{config.projectVersion}}</div>
            <div class="mt-1 ml-2">
                Created by <a href="http://vk.com/phpusr">Sergey Doronin</a>
            </div>
            <div class="ml-2">
                <a href="https://github.com/phpusr/wild-race-service" target="_blank">
                    <img src="/img/GitHub-Mark-32px.png" class="logo" alt="GitHub logo"/>
                </a>
            </div>
        </v-layout>
    </v-footer>
</template>

<script>
    import {mapState} from "vuex"

    export default {
        computed: mapState(["config"])
    }
</script>

<style>
    .logo {
        filter: invert(100%);
        height: 25px;
    }
</style>