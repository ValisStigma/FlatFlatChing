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

{
"account_name": "<mail@gmail.com>",
}

#### Response JSON Objekt

{
"account_token": "generated token"
}

#### Error Response JSON

{
"error_code": 1,
"error_message": "Authentication failed!"
}

### POST: /api/create

Erstellen einer neuen WG. 

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

{
"account_token": "<savedToken>",
"flat_name": "<name>",
"flat_address": null
}

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

#### RESPONSE JSON Object

{
"flat_uuid"
}

