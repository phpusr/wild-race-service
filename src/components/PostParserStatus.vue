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
    import {postStatusColors, postStatusIcons} from "../util/data"

    export default {
        props: {
            statusId: Number
        },
        computed: {
            value() {
                return this.$t('post.statuses')[this.statusId];
            },
            icon() {
                return postStatusIcons[this.statusId];
            },
            color() {
                return postStatusColors[this.statusId];
            },
            mobile() {
                return this.$vuetify.breakpoint.name === 'xs';
            }
        }
    }
</script>