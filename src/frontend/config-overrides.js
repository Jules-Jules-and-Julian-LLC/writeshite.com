const { override, addWebpackPlugin } = require('customize-cra');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = override(
    addWebpackPlugin(
        new HtmlWebpackPlugin({
            inject: false,
            template: 'public/index.html',
            filename: 'static/index.html',
            scriptLoading: 'blocking'
        })
    )
);
