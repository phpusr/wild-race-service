<template>
    <span :class="color" class="py-1 px-2">{{value}}</span>
</template>

<script>
    import {post} from '../i18n';

    const colors = {1: 'success', 2: 'warning', 3: 'danger', 4: 'warning'};

export default {
    props: {
        statusId: Number
    },
    computed: {
        value() {
            return post.status[this.statusId];
        },
        color() {
            return colors[this.statusId];
        }
    }
}
</script>