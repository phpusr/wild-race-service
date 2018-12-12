<template>
    <v-layout>
        <v-flex>
            <v-tabs v-model="activeTab" color="cyan" dark slider-color="yellow">
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

                                <v-btn @click="recount">{{ $t('stat.recount') }}</v-btn>
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

                                <v-btn @click="recount">{{ $t('stat.recount') }}</v-btn>
                            </v-layout>
                        </v-card-text>
                    </v-card>
                </v-tab-item>
            </v-tabs>
        </v-flex>
    </v-layout>
</template>

<script>
    import DatePicker from '../components/DatePicker'

    const DistanceTab = 'distance';
    const DateTab = 'date';

    export default {
        components: {DatePicker},
        data() {
            const {typeForm, startRange, endRange} = this.$route.params;
            return {
                menu: false,
                activeTab: typeForm === DistanceTab ? 0 : 1,
                startDistance: typeForm === DistanceTab ? +startRange : null,
                endDistance: typeForm === DistanceTab ? +endRange : null,
                startDate: typeForm === DateTab ? startRange : null,
                endDate: typeForm === DateTab ? endRange : null
            }
        },
        methods: {
            recount() {
                const isDistanceTab = this.activeTab === 0;
                const startRange = isDistanceTab ? this.startDistance : this.startDate;
                const endRange = isDistanceTab ? this.endDistance : this.endDate;
                const params = {
                    typeForm: isDistanceTab ? DistanceTab : DateTab,
                    startRange: startRange || '-',
                    endRange: endRange || '-'
                };

                this.$router.push({name: 'stat', params});
            }
        }
    }
</script>