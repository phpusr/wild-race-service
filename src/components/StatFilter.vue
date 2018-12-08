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
                                    <v-text-field v-model="startDistance" solo />
                                </v-flex>

                                <span class="mx-3 display-1">-</span>

                                <v-flex sm2>
                                    <v-text-field v-model="endDistance" solo />
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

    export default {
        components: {DatePicker},
        data() {
            const {typeForm, startRange, endRange} = this.$route.query;
            return {
                menu: false,
                activeTab: +typeForm,
                startDistance: startRange,
                endDistance: endRange,
                startDate: +startRange || null,
                endDate: +endRange || null
            }
        },
        methods: {
            recount() {
                const query = {
                    typeForm: this.activeTab,
                    startRange: this.startRange,
                    endRange: this.endRange
                };

                this.$router.push({path: '/stat', query});
            }
        }
    }
</script>