<template>
    <v-dialog v-model="show" persistent scrollable width="500">
        <v-btn slot="activator" color="info">
            <v-icon left>account_circle</v-icon>
            {{$t('user.login')}}
        </v-btn>

        <v-card>
            <v-card-title class="headline grey lighten-2">{{$t('user.loginTitle')}}</v-card-title>

            <v-card-text>
                <v-form v-model="valid">
                    <v-text-field
                            v-model="user.username"
                            :label="$t('user.username')"
                    />
                    <v-text-field
                            v-model="user.password"
                            :label="$t('user.password')"
                            :append-icon="showPassword ? 'visibility_off' : 'visibility'"
                            :type="showPassword ? 'text' : 'password'"
                            @click:append="showPassword = !showPassword"
                    />
                </v-form>
            </v-card-text>

            <v-divider/>

            <v-card-actions>
                <v-spacer/>
                <v-btn color="primary" @click="login">
                    {{$t('user.login')}}
                </v-btn>
                <v-btn @click="show = false">
                    {{$t('default.cancelButton')}}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        data: () => ({
            show: false,
            valid: false,
            user: {},
            showPassword: false
        }),
        methods: {
            login() {

            }
        }
    }
</script>