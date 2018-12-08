<template>
    <v-flex md6 offset-md3>
        <v-container>
            <v-layout>
                <v-flex>
                    <v-tabs v-model="activeTab" color="cyan" dark slider-color="yellow">
                        <v-tab ripple>Item</v-tab>
                        <v-tab ripple>Item 2</v-tab>

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
                                        <v-btn>{{ $t('stat.recount') }}</v-btn>
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

                                        <span class="mx-3 display-1">-</span>



                                        <v-btn @click="recount">{{ $t('stat.recount') }}</v-btn>
                                    </v-layout>
                                </v-card-text>
                            </v-card>
                        </v-tab-item>
                    </v-tabs>
                </v-flex>
            </v-layout>
        </v-container>
    </v-flex>
</template>

<script>
    import DatePicker from '../components/DatePicker'

    export default {
        components: {DatePicker},
        data: () => ({
            activeTab: 1,
            startDistance: null,
            endDistance: null,
            menu: false,
            startDate: null,
            endDate: null
        }),
        methods: {
            recount() {
                console.log(this.startDate)
            }
        }
    }
</script>