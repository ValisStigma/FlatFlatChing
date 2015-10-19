var express = require('express');
var router = express.Router();
var path = require("path");
var errors = require(path.resolve("components/errors.js"));
var userHandler = require(path.resolve("components/user-handler.js"));
var flats = require(path.resolve("components/db-handler.js")).flats;
var uuid = require("uuid");

router.post("/create", userHandler.loggedIn, function (req, res, next) {
    var error = false;
    if (!req.body.flat_name) {
        res.json(errors.auth.failed);
    }else {
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
            flat_address = req.body.flat_address;

            if (!error) {
                flats.insert({
                    flat_name: req.body.flat_name,
                    flat_address: flat_address,
                    flat_uuid: uuid.v4()
                }, function (err, flat) {
                    if (!err) {
                        res.json({flat_uuid: flat.flat_uuid});
                    }
                });
            }
        }
    }
});

module.exports = router;
