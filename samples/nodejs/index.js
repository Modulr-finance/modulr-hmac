'use strict';
const request = require('request');
const signature = require(`./signature`);

const API_KEY = "57502612d1bb2c00010000256c9568f1f7d540e085052b425d46d233";
const API_SECRET = "Y2Q0MzhmZDE2OWIzNDVkNGE5ZTczOTU3ZTAxZWY5NTc=";

const signatureHelper = new signature(API_KEY,API_SECRET);
const signatureResult = signatureHelper.calculate();
request.get(
    {
        url: 'https://api-sandbox.modulrfinance.com/api-sandbox/customers',
        json: true,
        headers: signatureResult.getHTTPHeaders()
    },
    (err, res, data) => {
        if (err) {
            console.error('Error calling API', err);
        } else if (res.statusCode !== 200) {
            console.error('Unsuccessful API call, code: ', res.statusCode, ', messsage: ', res.statusMessage, ', body: ', data);
        } else {
            console.log('OK response from API, body: ', data);
        }
    });
    