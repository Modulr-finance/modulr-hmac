'use strict';
const signature = require(`./signature`);

const API_KEY = "<YOUR-API-KEY>";
const API_SECRET = "<YOUR-SECRET>";

const signatureHelper = new signature(API_KEY,API_SECRET);
const signatureResult = signatureHelper.calculate();

async function getCustomers(){
    try {
        const response = await fetch('https://api-sandbox.modulrfinance.com/api-sandbox/customers',
            {
                headers: signatureResult.getHTTPHeaders()
            })

        const jsonBody = await response.json();

        if (response.ok) {
            console.log('OK response from API, body: ', jsonBody);
        }else {
            console.error('Unsuccessful API call, code: ', response.status,  ', body: ', jsonBody);
        }
    }catch(error){
        console.error('Error calling API', error);
    }
}

getCustomers();