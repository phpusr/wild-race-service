<template>
    <v-flex xs6>
        <v-container grid-list-sm fluid>
            <v-layout wrap>
                <!-- Дату можно только выбирать с дата пикера -->
                <v-flex xs12 sm6 md4>
                    <v-menu
                            :nudge-right="40"
                            lazy
                            transition="scale-transition"
                            offset-y
                            full-width
                            min-width="290px"
                    >
                        <v-text-field
                                slot="activator"
                                :value="formatDate(date)"
                                label="Picker in menu"
                                prepend-icon="event"
                                readonly
                        />
                        <v-date-picker v-model="date" no-title scrollable />
                    </v-menu>
                </v-flex>

                <!-- Дату можно выбирать как с дата пикера, так и вводить вручную -->
                <v-flex xs12 lg6>
                    <v-menu
                            :close-on-content-click="false"
                            v-model="menu1"
                            :nudge-right="40"
                            lazy
                            transition="scale-transition"
                            offset-y
                            full-width
                            max-width="290px"
                            min-width="290px"
                    >
                        <v-text-field
                                slot="activator"
                                v-model="dateFormatted"
                                label="Date"
                                prepend-icon="event"
                                @blur="date = parseDate(dateFormatted)"
                        />
                        <v-date-picker v-model="date" no-title @input="menu1 = false" />
                    </v-menu>
                    <p>Date in ISO format: <strong>{{ date }} ({{ dateFormatted }})</strong></p>
                </v-flex>
            </v-layout>
        </v-container>
    </v-flex>
</template>

<script>
    import dateFormat from "date-format"

    const ruDatePattern = "dd.MM.yyyy";
    const isoDatePattern = "yyyy-MM-dd";
    export default {

        data: () => ({
            menu: false,
            menu1: false,
            date: null,
            dateFormatted: null
        }),

        watch: {
            date (value) {
                this.dateFormatted = this.formatDate(value)
            }
        },

        methods: {
            formatDate (date) {
                if (!date) return null;

                return dateFormat(ruDatePattern, new Date(date));
            },
            parseDate (date) {
                if (!date) return null;

                return dateFormat(isoDatePattern, dateFormat.parse(ruDatePattern, date))
            }
        }
    }
</script>

<style>

</style>