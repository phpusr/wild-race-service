module.exports = {
    baseUrl: 'http://localhost:8000/',
    devServer: {
        port: 8000
    },
    configureWebpack: {
        resolve: {
            alias: {
                '@': __dirname + '/src/client'
            }
        },
        entry: {
            app: './src/client/main.js'
        }
    }
};