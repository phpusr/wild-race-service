<template>
    <v-layout>
        <v-flex>
            <v-tabs v-model="activeTabIndex" color="cyan" dark slider-color="yellow">
                <v-tab ripple>{{ $t('stat.distanceRange') }}</v-tab>
                <v-tab ripple>{{ $t('stat.dateRange') }}</v-tab>

                <v-tab-item>
                    <v-card flat>
                        <v-card-text>
                            <v-layout>
                                <v-flex sm2>
                                    <v-text-field v-model="startDistance" mask="##########" solo @keyup.enter="recount" />
                                </v-flex>

                                <span class="mx-3 display-1">-</span>

                                <v-flex sm2>
                                    <v-text-field v-model="endDistance" mask="##########" solo @keyup.enter="recount" />
                                </v-flex>

                                <span class="mx-3 mt-2 headline">{{ $t('default.km') }}</span>

                                <v-btn @click="recount(false)">{{ $t('stat.recount') }}</v-btn>
                                <v-btn @click="recount(true)" color="info">{{ $t('stat.publish') }}</v-btn>
                            </v-layout>
                        </v-card-text>
                    </v-card>
                </v-tab-item>

                <v-tab-item>
                    <v-card flat>
                        <v-card-text>
                            <v-layout>
                                <v-flex sm2>
                                    <date-picker v-model="startDate" />
                                </v-flex>

                                <span class="mx-3 mt-2 display-1">-</span>

                                <v-flex sm2>
                                    <date-picker v-model="endDate" />
                                </v-flex>

                                <v-btn @click="recount(false)">{{ $t('stat.recount') }}</v-btn>
                                <v-btn @click="recount(true)" color="info">{{ $t('stat.publish') }}</v-btn>
                            </v-layout>
                        </v-card-text>
                    </v-card>
                </v-tab-item>
            </v-tabs>
        </v-flex>
    </v-layout>
</template>

<script>
    import DatePicker from './DatePicker'

    const DistanceTab = {id: 0, name: 'distance', tabIndex: 0, isDistanceTab: true};
    const DateTab = {id: 1, name: 'date', tabIndex: 1, isDateTab: true};

    export default {
        components: {DatePicker},
        data() {
            const {typeOfForm, startRange, endRange} = this.$route.params;
            const activeTab = typeOfForm === DateTab.name ? DateTab : DistanceTab;
            return {
                activeTabIndex: activeTab.tabIndex,
                startDistance: activeTab.isDistanceTab  ? +startRange : null,
                endDistance: activeTab.isDistanceTab ? +endRange : null,
                startDate: activeTab.isDateTab ? startRange : null,
                endDate: activeTab.isDateTab ? endRange : null
            }
        },
        methods: {
            recount(publishPost) {
                if (publishPost && !confirm(this.$t('stat.confirmPublish'))) {
                    return
                }

                const activeTab = this.activeTabIndex === 0 ? DistanceTab : DateTab;
                const startRange = activeTab.isDistanceTab ? this.startDistance : this.startDate;
                const endRange = activeTab.isDistanceTab ? this.endDistance : this.endDate;
                const params = {
                    type: activeTab.id,
                    startRange: startRange || '-',
                    endRange: endRange || '-',
                    publishPost
                };

                this.$router.push({name: 'stat', params});
            }
        }
    }
</script>