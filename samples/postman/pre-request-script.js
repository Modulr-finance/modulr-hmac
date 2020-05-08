const uuid = require('uuid');
const moment = require('moment');
const crypto = require('crypto-js');
const DATE_TIME_FORMAT = 'ddd, DD MMM YYYY HH:mm:ss';
const TIMEZONE = ' GMT';


pm.variables.set('authorization', generateAuthorisationHeader());

function generateAuthorisationHeader() {
    const key = pm.variables.get('api_key');
    const secret = pm.variables.get('api_secret');

    return secret === "" ? key : generateHMAC(key, secret);
}

function generateHMAC(key, secret) {
    const nonce = uuid();
    const date = moment.utc().format(DATE_TIME_FORMAT) + TIMEZONE;
    setEnvironmentVariables(nonce, date);

    const signaturePlain = "date: " + date + "\nx-mod-nonce: " + nonce;
    const hmac = crypto.HmacSHA1(signaturePlain, secret);
    const hmacEncoded = encodeURIComponent(hmac.toString(crypto.enc.Base64));

    return 'Signature keyId="' + key + '",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="' + hmacEncoded + '"';
}

function setEnvironmentVariables(nonce, date) {
    pm.variables.set('nonce', nonce);
    pm.variables.set('date', date);
}
