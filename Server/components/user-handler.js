var path = require("path");

var users = require(path.resolve("components/db-handler.js")).users;
var flats = require(path.resolve("components/db-handler.js")).flats;
var errors = require(path.resolve("components/errors.js"));

var req = require(path.resolve("components/reqres.js")).req;
var res = require(path.resolve("components/reqres.js")).res;

var handler = {};

handler.loggedIn = function(req, res, next){
    if(!req.body.user_email){
        res.status(401);
        res.json(errors.auth.failed);
    }else {
        users.findOne({user_email: req.body.user_email}, function(err, found){
            if(found && !err){
                next();
            }else{
                res.status(401);
                res.json(errors.auth.failed);
            }
        });
    }
};

handler.registerUser = function(){
    users.findOne({user_email: req().body.account_name}, function (err, user) {
        if (err) {
            console.log(err);
            res().json(errors.auth.failed);
        } else {
            if (!user) {
                users.insert({user_email: req().body.account_name}, function (err, user) {
                    req().session.user_email = user.user_email;
                    res().json({
                        flat_uuids: []
                    });
                });
            } else {
                flats.find({"flat_members.user_email": user.user_email}, function (err, flats) {
                    var flat_uuids = [];
                    flats.forEach(function (entry) {
                        flat_uuids.push(entry.flat_uuid);
                    });
                    req().session.user_email = user.user_email;
                    res().json({
                        flat_uuids: flat_uuids
                    });
                });
            }
        }
    });
};

module.exports = handler;