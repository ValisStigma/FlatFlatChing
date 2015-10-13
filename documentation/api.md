# Schnittstellen Doc

Applikation kommuniziert über eine HTTPS REST Schnittstelle implementiert mit NodeJS.
Geräteauthentifizierung erfolgt über den auf dem gerät aktivierten Google Account. Code Beispiel:
---------
```
AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
Account[] list = manager.getAccountsByType("com.google");

for(Account account: list) {
    //display account
}
```
And you will need the following permission in your manifest:
```
<uses-permission android:name="android.permission.GET_ACCOUNTS"></uses-permission>
```
-----------


## API:

Alle aufrufe der API kommunizeren über JSON Objekte.

### RegisterWithGoogleAccount

Immer aufgerufen wenn App gestartet wird, um die Richtigkeit des Accounts zu prüfen oder wenn nichts lokal gespeichert wieder den Account Auswahl Screen anzeigen.

Es wird ein account_token zurück gegeben.
Dieses Token wird bei den anderen Requests mitgegeben um so den account zu authentifizieren.

###  /api/register

#### Request JSON Objekt

```
{
"account_name": "<mail@gmail.com>",
}
```

#### Response JSON Objekt

```
{
"account_token": "generated token",
"flat_uuids": ["<list with uuids or empty list>"]
}
```

#### Error Response JSON

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

### POST: /api/create

Erstellen einer neuen WG. 

Ersteller wird zum WG-Admin

Addresse Optional (Alles oder nichts d.h. entweder man gibt die komplette Adresse an oder gar keine)
##### Daten:
 - UUID
 - Name
 - Addresse
    - Strasse
    - Hausnummer
    - PLZ
    - Ort
    - Land

#### REQUEST Json Objekt
```
{
"account_token": "<savedToken>",
"flat_name": "<name>",
"flat_address": null
}
```
```
{
"account_token": "<savedToken>",
"flat_name": "<name>",
"flat_address": {
    "flat_address_street": "<strasse>",
    "flat_address_number": <number>,
    "flat_address_plz": <plz>,
    "flat_address_place": "<ortsname>",
    "flat_address_land": "<landname>"
}
}
```
#### RESPONSE JSON Object
```
{
"flat_uuid": "<generatedUUID>"
}
```

#### Error Response JSON

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 111,
"error_message": "flat address street missing"
}
```

```
{
"error_code": 112,
"error_message": "flat address house number missing"
}
```

```
{
"error_code": 113,
"error_message": "flat address plz missing"
}
```

```
{
"error_code": 114,
"error_message": "flat address place missing"
}
```

```
{
"error_code": 115,
"error_message": "flat address land missing"
}
```

### POST /api/invite

Requires User to be admin.
Läd einen Benutzer anhand der EmailAdresse in die WG ein.
Der Nutzer erhält darauffolgend eine Benachrichtigung. Über welche er die Einladung annehmen kann.


#### REQUEST JSON Object

```
{
"account_token": "<savedToken>",
"flat_uuid": "<saved uuid>",
"user_email": "<email to be invited>"
}
```

Saves new notification for invited user.

#### RESPONSE JSON Object

```
{
"response": "invitation send"
}
```

#### Error Response JSON

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 2,
"error_message": "Authentication failed! No admin"
}
```

```
{
"error_code": 11,
"error_message": "User not found!"
}
```

### GET /api/notifications

Notifications are beeing polled

or fancy: https://developers.google.com/cloud-messaging/android/client 


### POST /api/accept/invite

User Polls notification for invitations to new WG

if notification recieved and displayed he can accept or decline the invitation:

#### REQUEST JSON Object

Polled message for invitation:

```
{
"flat_uuid": "<uuid>",
"flat_admin_email": "<mail of flat_admin>"
}
```

REquest Object:


```
{
"account_token": "<token>",
"flat_uuid": "<uuid>",
"flat_admin_email": "<mail of flat_admin>",
"user_email": "<email>",
"invite_accpet": <boolean true for accept, false for decline>
}
```

#### RESPONSE JSON Object

```
{
"flat_uuid": "<uuid>"
}
```

#### Error Objects

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 201,
"error_message": "No invite found"
}
```


### POST /api/exit

#### REQUEST JSON OBJECT

```
{
"account_token": "<token>",
"flat_uuid": "<uuid>"
}
```


#### RESPONSE JSON object

```
{
"response": "good bye :("
}
```


#### Error Messages

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 202,
"error_message": "No flat found"
}
```

```
{
"error_code": 301,
"error_message": "open finance posts"
}
```


### /api/set/admin

Wird aufgerufen wenn ein WG-Admin ein anderes WG Mitglied zum Admin machen möchte. Es kann mehrere Admins geben, sprich der alte verliert keine Rechte.

#### REQUEST JSON Object

```
{
"account_token": "<token>",
"user_email": "<mail of the promoted>"
}
```

#### RESPONSE JSON Object

```
{
"response": "Done!"
}
```


#### Error Messages

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 2,
"error_message": "Authentication failed! No admin"
}
```

```
{
"error_code": 11,
"error_message": "User not found!"
}
```

-------------------------------------------------------------

### POST /api/expense/create/static

#### REQUEST JSON Object

```
{
"account_token": "<token>",
"flat_uuid": "<uuid>",
"expnese_name": "<name>",
"expense_amount": <amaount>,
"expense_end": timestamplong,
"expense_interval": interval_in_seconds(or not set),
"expense_users": [<list of user objects below>]
}


//Users all division keys added must be 100

{
"user_email": "<mail>",
"division_key": number between 0 and 100
}

```

#### RESPONSE JSON Object

```
{
"response": "Done!",
"expense_id": "<uuid>"
}
```


#### Error Messages

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 11,
"error_message": "User not found!",
"error_data": "useremail"
}
```

```
{
"error_code": 17,
"error_message": "Division Keys not valid"
}
```

```
{
"error_code": 202,
"error_message": "No flat found"
}
```


### POST /api/expense/create/variable

#### REQUEST JSON Object

```
{
"account_token": "<token>",
"flat_uuid": "<uuid>",
"expnese_name": "<name>",
"expense_amount": <amaount>,
"expense_end": timestamplong,
"expense_users": ["<list of user-emails objects below>"]
}


```

#### RESPONSE JSON Object

```
{
"response": "Done!",
"expense_id": "<uuid>"
}
```


#### Error Messages

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 11,
"error_message": "User not found!",
"error_data": "useremail"
}
```

```
{
"error_code": 202,
"error_message": "No flat found"
}
```


### POST /api/expense/payback/static

#### REQUEST JSON Object

```
{
"account_token": "<token>",
"flat_uuid": "<uuid>",
"expnese_id": "<uuid>",
"user_email": <mail>
}


```

#### RESPONSE JSON Object

```
{
"response": "Done!"
}
```


#### Error Messages

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 11,
"error_message": "User not found!"
}
```

```
{
"error_code": 701,
"error_message": "expense not found"
}
```

```
{
"error_code": 202,
"error_message": "No flat found"
}
```



### POST /api/expense/payback/variable

#### REQUEST JSON Object

```
{
"account_token": "<token>",
"flat_uuid": "<uuid>",
"expnese_id": "<uuid>",
"user_email": <mail>
}


```

#### RESPONSE JSON Object

```
{
"response": "Done!"
}
```


#### Error Messages

```
{
"error_code": 1,
"error_message": "Authentication failed!"
}
```

```
{
"error_code": 11,
"error_message": "User not found!"
}
```

```
{
"error_code": 701,
"error_message": "expense not found"
}
```

```
{
"error_code": 202,
"error_message": "No flat found"
}
```

-------------------------------------------------------------

### --GETTER API--


### GET /api/get/flat

Hohlt daten der WG. Falls benötigt.

#### Parameters

flat_uuid=<uuid>

#### RESPONSE JSON Object

```
{
"flat_name": "<name>",
"flat_address": {
    "flat_address_street": "<strasse>",
    "flat_address_number": <number>,
    "flat_address_plz": <plz>,
    "flat_address_place": "<ortsname>",
    "flat_address_land": "<landname>"
}
}
```


### GET /api/get/flat/members

Hohlt daten der WG. Falls benötigt.

#### Parameters

flat_uuid=<uuid>

#### RESPONSE JSON Object

Liste mit User-Daten

```
[
{
"email": "<mail@juser>",
"isAdmin": boolean
},
{
"email": "<mail>",
"isAdmin": boolean
}
]
```

### GET /api/get/expenses

#### PARAMS

flat_uuid=<uuid>
user_email=<mail>

#### RESPONSE

Hohlt alle expenses für einen user

liste mit solchen objekten:

```
{
"expense_uuid": "<uuid>",
"expense_name": "name",
"expense_end": timestamp,
"expense_amount": <amount>,
"expense_user_amount": <money_the_user_must_pay>,
"expense_type": "static/variable",
"expense_users": [list of objects below]
}
```

User Objects static
```
{
"user_mail": "mail",
"division_key": number(see /api/expense/create/static)
}
```


User Objects variable
```
{
"user_mail": "mail"
}
```


------------------------------

