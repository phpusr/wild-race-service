const path = require("path")

module.exports = {
    outputDir: path.resolve(__dirname + "/src/main/resources/static"),
    // Used when developing for hot deploy (if main.js inject into backend)
    baseUrl: "http://localhost:8000/",
    devServer: {
        port: 8000
    },
    configureWebpack: {
        resolve: {
            alias: {
                "@": path.resolve(__dirname + "/src/client")
            }
        },
        entry: {
            app: path.resolve("./src/client/main.js")
        }
    }
};