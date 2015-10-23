var path = require("path");
var errors = require(path.resolve("components/errors.js"));
var flats = require(path.resolve("components/db-handler.js")).flats;
var handler = {};
var expenses = require(path.resolve("components/db-handler.js")).expenses;



function ifRequestFlatExists(flat_uuid, next) {
    flats.findOne({flat_uuid: flat_uuid}, function (err, flat) {
        if (!flat) {
            res().json(errors.not_found.flat);
        } else {
            next();
        }
    });
}

handler.flatExists = ifRequestFlatExists;
handler.ifRequestFlatExists = ifRequestFlatExists;


function getUserExpenses(flat_uuid, user_email, next){
    expenses.find({
        expense_flat: flat_uuid,
        "expense_users.user_email": user_email
    }, function (err, found) {
        if(!Array.isArray(found)){
            next([]);
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
                        if(payback.payback_user === user_email){
                            if(paybacktimes>0){
                                expenses.push(expense);
                            }
                            paybacktimes--;
                        }
                    });
                }else{
                    var foundUser = false;
                    expense._paybacks.forEach(function(i, payback){
                        if(payback.payback_user === user_email){
                            foundUser = true;
                        }
                    });
                    if(!foundUser){
                        expenses.push(expense);
                    }
                }
            });
            next(expenses);
        }
    });
}
handler.getUserExpenses = getUserExpenses;


module.exports = handler;