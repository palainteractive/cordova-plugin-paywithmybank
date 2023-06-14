var exec = require('cordova/exec');

exports.selectBankWidget = function (arg0, success, error) {
    exec(success, error, 'PayWithMyBankCordova', 'selectBankWidget', [arg0]);
};
exports.establish = function (arg0, success, error) {
    exec(success, error, 'PayWithMyBankCordova', 'establish', [arg0]);
};
