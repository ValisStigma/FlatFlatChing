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