<template>
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
                :value="viewFormattedDate"
                :label="label"
                prepend-icon="event"
                readonly
        />
        <v-date-picker :value="isoFormattedDate" @input="input" no-title scrollable />
    </v-menu>
</template>

<script>
    import dateFormat from 'date-format'

    export default {
        props: {
            value: Number,
            label: String
        },
        computed: {
            viewFormattedDate() {
                if (!this.value) {
                    return null;
                }

                return dateFormat(this.$t('default.datePattern'), new Date(this.value));
            },
            isoFormattedDate() {
                if (!this.value) {
                    return null;
                }

                return dateFormat(this.$t('default.isoDatePattern'), new Date(this.value))
            }
        },
        methods: {
            input(date) {
                this.$emit('input', new Date(date).getTime());
            }
        }
    }
</script>