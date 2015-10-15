
var errors = {
    1:{
        "error_code": 1,
        "error_message": "Authentication failed!"
    },

    2:{
        "error_code": 2,
        "error_message": "Authentication failed! No admin"
    },

    11:{
        "error_code": 11,
        "error_message": "User not found!"
    },

    17:{
        "error_code": 17,
        "error_message": "Division Keys not valid"
    },

    111:{
        "error_code": 111,
        "error_message": "flat address street missing"
    },

    112:{
        "error_code": 112,
        "error_message": "flat address house number missing"
    },

    113:{
        "error_code": 113,
        "error_message": "flat address plz missing"
    },

    114:{
        "error_code": 114,
        "error_message": "flat address place missing"
    },

    115:{
        "error_code": 115,
        "error_message": "flat address land missing"
    },

    201:{
        "error_code": 201,
        "error_message": "No invite found"
    },

    202:{
        "error_code": 202,
        "error_message": "No flat found"
    },

    301:{
        "error_code": 301,
        "error_message": "open finance posts"
    },

    701:{
        "error_code": 701,
        "error_message": "expense not found"
    }

};

errors.auth = {
    failed: errors[1],
    no_admin: errors[2]
};

errors.flat_address = {
    street: errors[111],
    number: errors[112],
    plz: errors[113],
    place: errors[114],
    land: errors[115]
};

errors.not_found = {
    user: errors[11],
    flat: errors[202],
    invite: errors[201],
    expense: errors[701]
};

errors.finance = {
    open_posts: errors[301],
    division_keys: errors[17]
};

module.exports = errors;