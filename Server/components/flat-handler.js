var path = require("path");
var errors = require(path.resolve("components/errors.js"));
var flats = require(path.resolve("components/db-handler.js")).flats;
var handler = {};



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


module.exports = handler;