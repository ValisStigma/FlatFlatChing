var express = require('express');
var router = express.Router();
var path = require("path");

var expenses = require(path.resolve("components/db-handler.js")).expenses;
var ifRequestFlatExists = require(path.resolve("components/flat-handler.js")).ifRequestFlatExists;


router.get("/expenses", function(req,res,next){
    ifRequestFlatExists(req.query.flat_uuid, function() {
        expenses.find({expense_flat: req.query.flat_uuid, "expense_users.user_email": req.query.user_email}, function (err, found) {
            res.json(found);
        })
    });
});

module.exports = router;