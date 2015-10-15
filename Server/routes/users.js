var express = require('express');
var router = express.Router();
var path = require("path")
var db = require(path.resolve("components/db-handler.js"));
var users = db.users;
var flats = db.flats;
var errors = require(path.resolve("components/errors.js"));

router.post("/register", function(req, res, next){
    if(!req.body.user_email){
        res.json(errors.auth.no_admin);
    }else {
        users.findOne({user_email: req.body.user_email}, function (err, user) {
            if (err) {
                console.log(err);
                res.json(errors.auth.failed);
            } else {
                if (!user) {
                    users.insert({user_email: req.body.user_email}, function (err, user) {
                        req.session.user_email = user.user_email;
                        res.json({
                            flat_uuids: []
                        });
                    });
                } else {
                    flats.find({"flat_members.user_email": user.user_email}, function (err, flats) {
                        var flat_uuids = [];
                        flats.forEach(function (entry) {
                            flat_uuids.push(entry.flat_uuid);
                        });
                        req.session.user_email = user.user_email;
                        res.json({
                            flat_uuids: flat_uuids
                        });
                    });
                }
            }
        });
    }
});

module.exports = router;
