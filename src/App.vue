<template>
  <div id="app">
    <v-app>
      <v-toolbar app>
        <v-toolbar-title>Wild Race</v-toolbar-title>
      </v-toolbar>
      <v-content>
        <v-container fluid>
          <router-view></router-view>
        </v-container>
      </v-content>
      <v-footer app></v-footer>
    </v-app>
  </div>
</template>

<script>
export default {
  name: 'app'
}
</script>

<style>

</style>
