<template>
    <v-flex md6 offset-md3>
        <stat-filter />

        <v-container grid-list-lg class="pa-0 my-3">
            <v-layout wrap>
                <v-flex d-flex xs6>
                    <stat-card :title="$t('stat.topAllRunners')">
                        <ol>
                            <li v-for="r in stat.topAllRunners" :key="r.id">
                                <a :href="r.profile.vklink">{{ r.profile.firstName }} {{ r.profile.lastName }}</a>
                                <span> - {{ r.sumDistance }}</span>
                                <span> {{ $t('default.km') }}</span>
                            </li>
                        </ol>
                    </stat-card>
                </v-flex>

                <v-flex d-flex xs6>
                    <stat-card :title="$t('stat.topIntervalRunners')">
                        <ol>
                            <li v-for="r in stat.topIntervalRunners" :key="r.id">
                                <a :href="r.profile.vklink">{{ r.profile.firstName }} {{ r.profile.lastName }}</a>
                                <span> - {{ r.sumDistance }}</span>
                                <span> {{ $t('default.km') }}</span>
                            </li>
                        </ol>
                    </stat-card>
                </v-flex>

                <v-flex d-flex xs6>
                    <stat-card :title="$t('stat.daysCount')">
                        <div>- {{ $t('stat.daysCountAll') }} - {{ stat.daysCountAll }} {{ $t('default.days') }}</div>
                        <div>- {{ $t('stat.daysCountInterval') }} - {{ stat.daysCountInterval }} {{ $t('default.days') }}</div>
                    </stat-card>
                </v-flex>

                <v-flex d-flex xs6>
                    <stat-card :title="$t('stat.distance')">
                        <div>- {{ $t('stat.distancePerDayAvg') }} - {{ stat.distancePerDayAvg.toFixed(1) }} {{ $t('default.kmPerDay') }}</div>
                        <div>- {{ $t('stat.distancePerTrainingAvg') }} - {{ stat.distancePerTrainingAvg.toFixed(1) }} {{ $t('default.kmPerTraining') }}</div>
                        <div>- {{ $t('stat.distanceMaxOneMan') }} - {{ stat.distanceMaxOneMan }} {{ $t('default.km') }}</div>
                    </stat-card>
                </v-flex>

                <v-flex d-flex xs6>
                    <stat-card :title="$t('stat.runners')">
                        <div>- {{ $t('stat.runnersCountAll') }} - {{ stat.runnersCountAll }} {{ $t('default.people') }}</div>
                        <div>- {{ $t('stat.runnersCountInterval') }} - {{ stat.runnersCountInterval }} {{ $t('default.peoplePerDay') }}</div>
                        <div>- {{ $t('stat.newRunners') }} - {{ stat.newRunners.length }} {{ $t('default.people') }} ()</div>
                    </stat-card>
                </v-flex>

                <v-flex d-flex xs6>
                    <stat-card :title="$t('stat.trainings')">
                        <div>- {{ $t('stat.trainingCountAll') }} - {{ stat.trainingCountAll }} {{ $t('default.trainings') }}</div>
                        <div>- {{ $t('stat.trainingCountPerDayAvgFunction') }} - {{ stat.trainingCountPerDayAvgFunction.toFixed(1) }} {{ $t('default.trainingsPerDay') }}</div>
                        <div>- {{ $t('stat.trainingMaxOneMan') }} - {{ stat.trainingMaxOneMan }} {{ $t('default.trainings') }}</div>
                    </stat-card>
                </v-flex>

            </v-layout>
        </v-container>
    </v-flex>
</template>

<script>
    import StatFilter from '../components/StatFilter'
    import StatCard from '../components/StatCard'

    export default {
        components: {StatFilter, StatCard},
        data: () => ({
            stat: {
                distancePerDayAvg: 0.0,
                distancePerTrainingAvg: 0.0,
                trainingCountPerDayAvgFunction: 0.0,
                newRunners: []
            }
        }),
        created() {
            this.fetchData()
        },
        beforeRouteUpdate (to, from, next) {
            next();

            this.fetchData();
        },
        methods: {
            fetchData() {
                const {params} = this.$route;
                this.$http.get('/stat', {params}).then(response =>
                    this.stat = response.body
                );
            }
        }
    }
</script>