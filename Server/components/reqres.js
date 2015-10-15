reqres = {
    req: null,
    res: null
};

reqres.registerReqRes = function registerReqRes(req, res, next){
    reqres.res = res;
    reqres.req = req;
    next();
};

module.exports = reqres;