'use strict';

const expect = require('chai').expect;
const signature = require(`../signature`);

const signatureHelper = new signature('KNOWN-TOKEN','NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=');

describe('Signature Test', function(){

    it('HMAC generation', function(done){
        var result = signatureHelper.calculate();
        expect(result.getSignature()).to.not.undefined;

        var headers = result.getHTTPHeaders();

        expect(headers['Date']).to.not.undefined;
        expect(headers['x-mod-nonce']).to.not.undefined;
        expect(headers['Authorization']).to.not.undefined;
        done();
    });

    it('HMAC generation with known nonce and date', function(done){
        var result = signatureHelper.calculate('28154b2-9c62b93cc22a-24c9e2-5536d7d','Mon, 25 Jul 2016 16:36:07 GMT');
        expect(result.getSignature()).to.equal('WBMr%2FYdhysbmiIEkdTrf2hP7SfA%3D');

        var headers = result.getHTTPHeaders();

        expect(headers['Date']).to.equal('Mon, 25 Jul 2016 16:36:07 GMT');
        expect(headers['x-mod-nonce']).to.equal('28154b2-9c62b93cc22a-24c9e2-5536d7d');
        expect(headers['Authorization']).to.equal('Signature keyId="KNOWN-TOKEN",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="WBMr%2FYdhysbmiIEkdTrf2hP7SfA%3D"');
        done();
    });

});