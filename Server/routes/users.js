var express = require('express');
var router = express.Router();
var path = require("path")
var errors = require(path.resolve("components/errors.js"));
var userHandler = require(path.resolve("components/user-handler.js"));

router.post("/register", function(req, res, next){
    if(!req.body.user_email){
        res.json(errors.auth.failed);
    }else {
        userHandler.registerUser(req.body.user_email);
    }
});

module.exports = router;
