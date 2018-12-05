<template>
    <v-flex md6 offset-md3 lazy-validation>
        <v-form ref="form" v-model="valid">
            <v-checkbox
                    v-model="config.syncPosts"
                    :label="$t('config.syncPosts')"
                    :readonly="show"
            />
            <v-text-field
                    v-model="config.syncSeconds"
                    :label="$t('config.syncSeconds')"
                    mask="#####"
                    :rules="requiredRules"
                    required
                    :readonly="show"
            />
            <v-text-field
                    v-model="config.groupId"
                    :label="$t('config.groupId')"
                    mask="-##########"
                    :rules="requiredRules"
                    required
                    :readonly="show"
            />
            <v-text-field
                    v-model="config.groupShortLink"
                    :label="$t('config.groupShortLink')"
                    :rules="requiredRules"
                    required
                    :readonly="show"
            />
            <v-checkbox
                    v-model="config.commenting"
                    :label="$t('config.commenting')"
                    :readonly="show"
            />
            <v-text-field
                    v-model="config.commentAccessToken"
                    :label="$t('config.commentAccessToken')"
                    :rules="requiredRules"
                    required
                    :readonly="show"
            />
            <v-checkbox
                    v-model="config.commentFromGroup"
                    :label="$t('config.commentFromGroup')"
                    :readonly="show"
            />
            <v-checkbox
                    v-model="config.publishStat"
                    :label="$t('config.publishStat')"
                    :readonly="show"
            />

            <div v-if="show">
                <v-btn to="/config/edit">{{$t('default.editButton')}}</v-btn>
            </div>
            <div v-else>
                <v-btn :disabled="!valid" @click="save" color="primary">{{$t('default.saveButton')}}</v-btn>
                <v-btn @click="cancel" primary>{{$t('default.cancelButton')}}</v-btn>
            </div>
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
        created() {
            this.$http.get('/config').then(response =>
                this.config = response.body
            );
        },
        computed: {
            show() {
                return this.$route.path === '/config'
            }
        },
        methods: {
            save() {
                if (this.$refs.form.validate()) {
                    this.$http.put('/config', this.config).then(response => {
                        this.config = response.body;
                        this.$router.push('/config');
                    });
                }
            },
            cancel () {
                this.$router.go(-1);
            }
        }
    }
</script>