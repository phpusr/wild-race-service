<template>
    <div>
        <v-tooltip v-if="mobile" top>
            <v-btn :class="color" icon slot="activator">
                <v-icon>{{icon}}</v-icon>
            </v-btn>
            <span>{{value}}</span>
        </v-tooltip>
        <span v-else :class="color" class="py-1 px-2">{{value}}</span>
    </div>
</template>

<script>
    const colors = {1: 'success', 2: 'warning', 3: 'danger', 4: 'warning'};
    const icons = {1: 'done', 2: 'warning', 3: 'error', 4: 'warning'};

export default {
    props: {
        statusId: Number
    },
    computed: {
        value() {
            return this.$t('post.statuses')[this.statusId];
        },
        icon() {
            return icons[this.statusId];
        },
        color() {
            return colors[this.statusId];
        },
        mobile() {
            return this.$vuetify.breakpoint.name === 'xs';
        }
    }
}
</script>