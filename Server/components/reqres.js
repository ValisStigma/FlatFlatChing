var reqres = {};
var internal = {};

reqres.registerReqRes = function registerReqRes(req, res, next){
    internal._res = res;
    internal._req = req;
    next();
};

reqres.req = function(){return internal._req};
reqres.res = function(){return internal._res};

module.exports = reqres;