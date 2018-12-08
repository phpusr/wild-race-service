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
                :value="formatDate(value)"
                :label="label"
                prepend-icon="event"
                readonly
        />
        <v-date-picker :value="value" @input="$emit('input', $event)" no-title scrollable />
    </v-menu>
</template>

<script>
    import dateFormat from 'date-format'

    export default {
        props: {
            value: String,
            label: String
        },
        methods: {
            formatDate (date) {
                if (!date) return null;

                return dateFormat(this.$t('default.datePattern'), new Date(date));
            }
        }
    }
</script>