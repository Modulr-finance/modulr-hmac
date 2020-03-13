'use strict';

const uuid = require('uuid');
const crypto = require('crypto-js');

class Signature {
    constructor(apiKey, apiSecret) {
        if (this.apikey || this.apiSecret) {
            throw new Error('apikey and apiSecret must be suppiled to calculate signature');
        }
        this.apikey = apiKey;
        this.apiSecret = apiSecret;
    }


    calculate(nonce, timestamp){
        if (!nonce){
            nonce = uuid.v4();
        }

        if (!timestamp){
            timestamp = new Date().toUTCString();
        }

        // format raw signature
        const signature = `date: ${timestamp}\nx-mod-nonce: ${nonce}`;
        console.debug(`Raw signature string is:\n-----------\n${signature}\n-----------`);
    
        // sign and encode signature
        const signatureSigned = crypto.HmacSHA1(signature, this.apiSecret);
        const signatureEncoded = encodeURIComponent(crypto.enc.Base64.stringify(signatureSigned));
        console.debug(`Signed signature is [${signatureSigned}] and encoded is [${signatureEncoded}]`);
    
        return new Result(this.apikey, timestamp, nonce, signatureEncoded);
    }

}

class Result {
    constructor(key, timestamp, nonce, encodedSignature){
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.encodedSignature = encodedSignature;
        this.authorisation = `Signature keyId="${key}",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="${encodedSignature}"`;
    }

    getTimeStamp(){
        return this.timestamp;
    }

    getHTTPHeaders(){
        return {
            'Date': this.timestamp,
            'x-mod-nonce': this.nonce,
            'Authorization': this.authorisation
        };
    }

    getSignature(){
        return this.encodedSignature;
    }
}

module.exports = Signature, Result;