var express = require('express');
var router = express.Router();
var path = require("path");

var expenses = require(path.resolve("components/db-handler.js")).expenses;
var flats = require(path.resolve("components/db-handler.js")).flats;
var users = require(path.resolve("components/db-handler.js")).users;
var ifRequestFlatExists = require(path.resolve("components/flat-handler.js")).ifRequestFlatExists;


router.get("/expenses", function (req, res, next) {
    ifRequestFlatExists(req.query.flat_uuid, function () {
        expenses.find({
            expense_flat: req.query.flat_uuid,
            "expense_users.user_email": req.query.user_email
        }, function (err, found) {
            res.json(found);
        })
    });
});

router.get("/flat", function (req, res, next) {
    console.log(req.query.flat_uuid);
    flats.findOne({flat_uuid: req.query.flat_uuid}, function (err, found) {
        console.log(found);
        res.json(found);
    });
});

router.get("/flat/members", function (req, res, next) {
    console.log(req.query.flat_uuid);
    users.find({_current_flat: req.query.flat_uuid}, function (err, found) {
        console.log(found);
        res.json(found);
    });
});

module.exports = router;