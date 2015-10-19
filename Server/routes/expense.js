var express = require('express');
var router = express.Router();
var path = require("path");
var errors = require(path.resolve("components/errors.js"));
var userHandler = require(path.resolve("components/user-handler.js"));
var flats = require(path.resolve("components/db-handler.js")).flats;
var uuid = require("uuid");
var req = require(path.resolve("components/reqres.js")).req;
var res = require(path.resolve("components/reqres.js")).res;

function ifRequestFlatExists(next){
    var expense = req().body;
    if(!expense.flat_uuid){
        res().json(errors.not_found.flat);
    }else{
        flats.findOne({flat_uuid: expense.flat_uuid}, function(err, flat){
            if(!flat){
                res().json(errors.not_found.flat);
            }else{
                next();
            }
        });
    }
}

function generateExpenseModel(next){
    var expense = req().body;
    ifRequestFlatExists(function(){
        if( !expense.expense_name   ||
            !expense.expense_end    ||
            !expense.expense_amount ){

            res().json(errors.auth.failed);
        }else{
            next({
                expense_uuid: uuid.v4(),
                expense_name: expense.expense_name,
                expense_end: expense.expense_end,
                expense_amount: expense.expense_amount,
                expense_users: []
            });
        }
    });
}

function extractUsers(expModel, next){
    var expense = req().body;
    if(!Array.isArray(expense.expense_users)){
        res().json(errors.not_found.user);
    }else{
        var notFoundUser;
        expense.expense_users.forEach(function(user){

        });
        if(expModel.expense_type === "static"){
            var div_keys = 0;
            expense.expense_users.forEach(function(user){
                div_keys += user.division_key;
            });

        }
        next(expModel);
    }
}

function createStaticExpense(next){
    var expense = req().body;
    generateExpenseModel(function(expenseModel){
        expenseModel.expense_type = "static";
        if(expense.expense_interval) {
            expenseModel.expense_interval = expense.expense_interval;
        } else {
            expenseModel.expense_interval = 0;
        }
        extractUsers(expenseModel, function(expenseModel){

        });
    });
}

router.post("/create/static", userHandler.loggedIn, function(req, res, next){

});







module.exports = router;