var path = require("path");
var Datastore = require("nedb");

db = {};

db.users = new Datastore(path.resolve('data/user.db'));
db.flats = new Datastore(path.resolve('data/flats.db'));
db.expenses = new Datastore(path.resolve('data/expenses.db'))

db.users.loadDatabase();
db.flats.loadDatabase();
db.expenses.loadDatabase();

module.exports = db;