var exec = require('cordova/exec');

exports.selectBankWidget = function (arg0, success, error) {
    exec(success, error, 'PayWithMyBank', 'selectBankWidget', [arg0]);
};
