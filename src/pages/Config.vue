<template>
    <v-flex md6 offset-md3 lazy-validation>
        <v-form ref="form" v-model="valid">
            <v-checkbox
                    v-model="config.syncPosts"
                    :label="$t('config.syncPosts')"
            />
            <v-text-field
                    v-model="config.syncSeconds"
                    :label="$t('config.syncSeconds')"
                    mask="#####"
                    :rules="requiredRules"
                    required
            />
            <v-text-field
                    v-model="config.groupId"
                    :label="$t('config.groupId')"
                    mask="-##########"
                    :rules="requiredRules"
                    required
            />
            <v-text-field
                    v-model="config.groupShortLink"
                    :label="$t('config.groupShortLink')"
                    :rules="requiredRules"
                    required
            />
            <v-checkbox
                    v-model="config.commenting"
                    :label="$t('config.commenting')"
            />
            <v-text-field
                    v-model="config.commentAccessToken"
                    :label="$t('config.commentAccessToken')"
                    :rules="requiredRules"
                    required
            />
            <v-checkbox
                    v-model="config.commentFromGroup"
                    :label="$t('config.commentFromGroup')"
            />
            <v-checkbox
                    v-model="config.publishStat"
                    :label="$t('config.publishStat')"
            />

            <v-btn :disabled="!valid" @click="save">{{$t('default.saveButton')}}</v-btn>
            <v-btn @click="cancel" primary>{{$t('default.cancelButton')}}</v-btn>
        </v-form>
    </v-flex>
</template>

<script>
    export default {
        data: () => ({
            valid: true,
            config: {},
            requiredRules: [v => !!v || 'Filed is required']
        }),
        methods: {
            save() {
                if (this.$refs.form.validate()) {
                    console.log(this.config)
                }
            },
            cancel () {
                //TODO
            }
        }
    }
</script>