<template>
    <v-layout>
        <v-flex>
            <v-tabs v-model="activeTabIndex" color="cyan" dark slider-color="yellow">
                <v-tab ripple>{{ $t("stat.distanceRange") }}</v-tab>
                <v-tab ripple>{{ $t("stat.dateRange") }}</v-tab>

                <v-tab-item>
                    <v-card flat>
                        <v-card-text>
                            <v-layout wrap>
                                <v-flex sm3 xs5>
                                    <v-text-field v-model="startDistance" mask="##########" solo @keyup.enter="recount" clearable />
                                </v-flex>

                                <v-flex xs1>
                                    <div class="mt-1 display-1 text-xs-center">-</div>
                                </v-flex>

                                <v-flex sm3 xs5>
                                    <v-text-field v-model="endDistance" mask="##########" solo @keyup.enter="recount" clearable />
                                </v-flex>

                                <v-flex xs1>
                                    <div class="ml-2 mt-2 headline">{{ $t("default.km") }}</div>
                                </v-flex>

                                <v-flex xs5 sm3 md2>
                                    <v-btn @click="recount">{{ $t("stat.recount") }}</v-btn>
                                </v-flex>

                                <v-flex xs5 sm3 md2>
                                    <v-btn v-if="userIsAdmin" @click="publishPost" color="info">{{ $t("stat.publish") }}</v-btn>
                                </v-flex>
                            </v-layout>
                        </v-card-text>
                    </v-card>
                </v-tab-item>

                <v-tab-item>
                    <v-card flat>
                        <v-card-text>
                            <v-layout wrap>
                                <v-flex sm3 xs5>
                                    <date-picker v-model="startDate" />
                                </v-flex>

                                <v-flex xs1>
                                    <div class="mt-2 display-1 text-xs-center">-</div>
                                </v-flex>

                                <v-flex sm3 xs5>
                                    <date-picker v-model="endDate" />
                                </v-flex>

                                <v-flex xs5 sm3 md2>
                                    <v-btn @click="recount">{{ $t("stat.recount") }}</v-btn>
                                </v-flex>

                                <v-flex xs5 sm3 md2>
                                    <v-btn v-if="userIsAdmin" @click="publishPost" color="info">{{ $t("stat.publish") }}</v-btn>
                                </v-flex>
                            </v-layout>
                        </v-card-text>
                    </v-card>
                </v-tab-item>
            </v-tabs>
        </v-flex>
    </v-layout>
</template>

<script>
    import DatePicker from "./DatePicker"
    import statApi from "../api/stat"
    import {fetchHandler, stringToInt} from "../util"
    import {mapGetters} from "vuex"

    const DistanceTab = {name: "distance", tabIndex: 0, isDistanceTab: true}
    const DateTab = {name: "date", tabIndex: 1, isDateTab: true}

    export default {
        components: {DatePicker},
        data() {
            const {typeOfForm, startRange, endRange} = this.$route.params
            const activeTab = typeOfForm === DateTab.name ? DateTab : DistanceTab
            return {
                activeTabIndex: activeTab.tabIndex,
                startDistance: activeTab.isDistanceTab  ? startRange : null,
                endDistance: activeTab.isDistanceTab ? endRange : null,
                startDate: activeTab.isDateTab ? startRange : null,
                endDate: activeTab.isDateTab ? endRange : null
            }
        },
        computed: {
            ...mapGetters(["userIsAdmin"]),
            params() {
                const activeTab = this.activeTabIndex === 0 ? DistanceTab : DateTab
                const startRange = activeTab.isDistanceTab ? stringToInt(this.startDistance) : this.startDate
                const endRange = activeTab.isDistanceTab ? stringToInt(this.endDistance) : this.endDate
                return {
                    type: activeTab.name,
                    startRange: startRange != null ? startRange : "-",
                    endRange: endRange != null ? endRange : "-"
                }
            }
        },
        methods: {
            recount() {
                const params = this.params;
                if (params.startRange !== "-" && params.endRange !== "-" && params.startRange >= params.endRange) {
                    alert(this.$t("stat.startRangeLessEndRange"))
                    return
                }

                this.$router.push({name: "stat", params})
            },
            publishPost() {
                if (!confirm(this.$t("stat.confirmPublish"))) {
                    return
                }

                statApi.publishPost(this.params)
                    .then(response => alert(this.$t("stat.successPublishPost", {id: response.body})))
                    .catch(fetchHandler)
            }
        }
    }
</script>