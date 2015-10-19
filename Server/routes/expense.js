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

            res.json(errors.auth.failed);
        }else{

        }
    });
};

function createStaticExpense(){

}

router.post(userHandler.loggedIn, "/create/static", function(req, res, next){

});







module.exports = router;