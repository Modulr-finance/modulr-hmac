'use strict';
const request = require('request');
const uuid = require('uuid');
const crypto = require('crypto-js');

// parameters for signature calculate
const timestamp = new Date().toUTCString();
const nonce = uuid.v4();
const key = process.env.API_KEY;
const secret = process.env.API_SECRET;
if (!key || !secret) throw new Error('You need to set API_KEY and API_SECRET environment variables');

function calculateSignature(timestamp, nonce, secret) {
    // format raw signature
    const signature = `date: ${timestamp}\nx-mod-nonce: ${nonce}`;
    console.debug(`Raw signature string is:\n-----------\n${signature}\n-----------`);

    // sign and encode signature
    const signatureSigned = crypto.HmacSHA1(signature, secret);
    const signatureEncoded = encodeURIComponent(crypto.enc.Base64.stringify(signatureSigned));
    console.debug(`Signed signature is [${signatureSigned}] and encoded is [${signatureEncoded}]`);

    return signatureEncoded;
}

// call some API
const auth = `Signature keyId="${key}",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="${calculateSignature(timestamp, nonce, secret)}"`;
request.get(
    {
        url: 'https://api-sandbox.modulrfinance.com/api-sandbox/customers',
        json: true,
        headers: {
            'Date': timestamp,
            'x-mod-nonce': nonce,
            'Authorization': auth
        }
    },
    (err, res, data) => {
        if (err) {
            console.error('Error calling API', err);
        } else if (res.statusCode !== 200) {
            console.error('Unsuccessful API call', res.statusCode, res.statusMessage);
        } else {
            console.log('OK response from API', data);
        }
    });