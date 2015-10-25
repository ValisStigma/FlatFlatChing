var express = require('express');
var router = express.Router();
var path = require("path");
var errors = require(path.resolve("components/errors.js"));
var userHandler = require(path.resolve("components/user-handler.js"));
var flats = require(path.resolve("components/db-handler.js")).flats;
var users = require(path.resolve("components/db-handler.js")).users;
var uuid = require("uuid");
var getUserExpenses = require(path.resolve("components/flat-handler.js")).getUserExpenses;
var ifRequestFlatExists = require(path.resolve("components/flat-handler.js")).ifRequestFlatExists;


router.post("/create", userHandler.loggedIn, function (req, res, next) {
    var error = false;
    if (!req.body.flat_name) {
        res.json(errors.auth.failed);
    } else {
        var flat_address = {};
        if (req.body.flat_address) {
            if (!req.body.flat_address.flat_address_street) {
                res.json(errors.flat_address.street);
                error = true;
            } else if (!req.body.flat_address.flat_address_number) {
                res.json(errors.flat_address.number);
                error = true;
            } else if (!req.body.flat_address.flat_address_plz) {
                res.json(errors.flat_address.plz);
                error = true;
            } else if (!req.body.flat_address.flat_address_place) {
                res.json(errors.flat_address.place);
                error = true;
            } else if (!req.body.flat_address.flat_address_land) {
                res.json(errors.flat_address.land);
                error = true;
            }
            flat_address = {
                flat_address_street: req.body.flat_address.flat_address_street,
                flat_address_number: req.body.flat_address.flat_address_number,
                flat_address_plz: req.body.flat_address.flat_address_plz,
                flat_address_place: req.body.flat_address.flat_address_place,
                flat_address_land: req.body.flat_address.flat_address_land
            };
        }

        if (!error) {
            flats.insert({
                flat_name: req.body.flat_name,
                flat_address: flat_address,
                flat_uuid: uuid.v4()
            }, function (err, flat) {
                if (!err) {
                    users.update({user_email: req.body.user_email}, {$set: {_current_flat: flat.flat_uuid, _is_admin: true}}, {}, function(){
                        res.json({flat_uuid: flat.flat_uuid});
                    });
                }
            });
        }
    }
});


router.post("/set/admin", userHandler.loggedIn, function (req, res, next) {
    users.findOne({user_email: req.body.user_email}, function(err, found){
        if(found){
            if(found._is_admin){
                users.update({user_email: req.body.email}, {$set: {_is_admin: true}}, {}, function(){
                    res.json({
                        "response": "Done!"
                    });
                });
            }
        }
    });
});


router.post("/exit", userHandler.loggedIn, function(req, res, next){
    getUserExpenses(req.body.flat_uuid, req.body.user_email, function(expenses){
        if(expenses.length > 0){
            res.json(errors.finance.open_posts);
        }else{
            users.update({user_email: req.body.user_email}, {$set: {_is_admin: false}, $unset: {_current_flat: true}}, {}, function(){
                res.json({
                    "response": "good bye :("
                });
            });
        }
    });
});

router.post("/invite", userHandler.loggedIn, function(req,res,next){
    ifRequestFlatExists(req.body.flat_uuid, function(flat){
        users.update({user_email: req.body.email}, {$set: {_is_admin: false, _current_flat: flat.flat_uuid}}, {}, function(){
            res.json({
                "response": "invitation send"
            });
        });
    });
});

module.exports = router;
