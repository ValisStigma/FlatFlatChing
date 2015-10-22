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
            if(!Array.isArray(found)){
                res.json([]);
            }else {
                var expenses = [];
                found.forEach(function(ind, expense){
                    if(expense.expense_type === "static"){
                        var paybacktimes = 0;
                        var oldDate = expense.expense_start;
                        while(oldDate < (Date.now() / 1000)) {
                            paybacktimes++;
                            oldDate += expense.expense_interval;
                        }
                        expenses._paybacks.forEach(function(i, payback){
                            if(payback.payback_user === req.query.user_email){
                                if(paybacktimes>0){
                                    expenses.push(expense);
                                }
                                paybacktimes--;
                            }
                        });
                    }else{
                        var foundUser = false;
                        expense._paybacks.forEach(function(i, payback){
                            if(payback.payback_user === req.query.user_email){
                                foundUser = true;
                            }
                        });
                        if(!foundUser){
                            expenses.push(expense);
                        }
                    }
                });
                res.json(expenses);
            }
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