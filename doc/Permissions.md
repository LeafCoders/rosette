# Rättigheter

Rättigheter i Rosette anges på ett format som används av Shiro (http://shiro.apache.org/permissions.html). Här är några exempel på rättigheter: `posters:create`, `locations:*` och `eventTypes:read:scout`. Formatet är enligt följande: `[resurs]:[åtgärd]:[resurs-id]`. Samtliga resurser har åtgärderna `create`, `read`, `update` och `delete`. Flera av dem har även åtgården `view` men den används enbart av Cordate för att visa menyer. Om `resurs-id` utlämnas så innebär det att åtgärden gäller för alla resurser.

När en användare loggar in skapar servern en lisa på vilka rättigheter användaren har. Listan med rättigheter sparas i sessionen. Vissa rättigheter automatgenereras av systemet (punkt 1 och 2) medans andra (3, 4 och 5) hämtas från angivna rättigheter i databasen. Användaren (med id 4711) får sina rättigheter enligt följande regler:

1. `users:read:4711` och `users:update:4711` för sin egna användare.
2. `users:read:xxxx` för varje användare i de grupper som användaren är medlem i.
3. Angivna rättigheter för den egna användaren.
4. Angivna rättigheter från de grupper som användaren är medlem i.
5. Angivna rättigheter för Alla.

Resurs-id anges på två olika sätt. För t.ex. en användare så används [ObjectId](http://docs.mongodb.org/manual/reference/object-id/) som id. I detta dokument anges dessa id:n men förenklade tal som t.ex. 4711 för att det ska vara lättare att läsa. Den andra typen av id anges med [kamelNotation](http://sv.wikipedia.org/wiki/Kamelnotation) med liten första bokstav. Filkatalogers id:n anges med kamelNotaion för att det ska vara lättare att ange rättigheter för en specifik katalog, t.ex. `read:uploadFolders:posters`.

## signupUsers
Anger rättigheter för användarförfrågningar. T.ex. `signupUsers:read`. En användarförfrågnings id skrivs med ObjectId.

**create** - Rättigheten finns alltid.  
**read** - Får läsa användarförfrågning  
**update** - Får uppdatera användarförfrågning  
**delete** - Får ta bort användarförfrågning

## users
Anger rättigheter för användare. T.ex. `users:read:89`. En användares id skrivs med ObjectId. Var försiktig med att dela ut rättighetena `create` och `delete`.

**create** - Får skapa ny användare  
**read** - Får läsa användare  
**update** - Får uppdatera användare  
**delete** - Får ta bort användare

## groups
Anger rättigheter för grupper. T.ex. `groups:read:admins`. En grupps id skrivs med kamelNotation.

**create** - Får skapa ny grupp  
**read** - Får läsa grupp  
**update** - Får uppdatera grupp  
**delete** - Får ta bort grupp

## groupMemberships
Anger rättigheter för gruppmedlemskap. T.ex. `groupMemberships:read`. En gruppmedlemskaps id skrivs med ObjectId.

**create** - Får skapa ny gruppmedlemskap  
**read** - Får läsa gruppmedlemskap  
**update** - Får uppdatera gruppmedlemskap  
**delete** - Får ta bort gruppmedlemskap

## permissions
Anger rättigheter för behörigheter. T.ex. `permissions:read`. En behörighets id skrivs med ObjectId.

**create** - Får skapa ny behörighet  
**read** - Får läsa behörighet  
**update** - Får uppdatera behörighet  
**delete** - Får ta bort behörighet

## resourceTypes
Anger rättigheter för resurstyper. T.ex. `resourceTypes:read`. En resurstyps id skrivs med kamelNotation.

**create** - Får skapa ny resurstyp  
**read** - Får läsa resurstyp  
**update** - Får uppdatera resurstyp  
**delete** - Får ta bort resurstyp  

## eventTypes
Anger rättigheter för händelsetyper. T.ex. `eventTypes:read:scout`. En händelsetyps id skrivs med kamelNotation.

**create** - Får skapa ny händelsetyp  
**read** - Får läsa händelsetyp  
**update** - Får uppdatera händelsetyp  
**delete** - Får ta bort händelsetyp

## events
Anger rättigheter för händelser. T.ex. `events:read`. En händelses id skrivs med ObjectId.

**create** - Får skapa ny händelse  
**read** - Får läsa händelse  
**update** - Får uppdatera händelse  
**delete** - Får ta bort händelse

Rättigheterna ovan gäller för alla händelser. Det går att ge rättigheter för händelser av en viss händelsetyp. Rättigheten `events:*:eventTypes:xxx` anger att händelser med händelsetypen `xxx` får skapas/läsas/uppdateras/tas bort. 

Det går att ge läsrättigheter för händelser som innehåller en specifik resurstyp. Rättigheten `events:read:resourceTypes:xxx` anger att händelser, som innehåller resurstypen `xxx`, får läsas. Det går inte att ge skapa- eller ta bort-rättighet för händelser via resurstyp. Rättigheten `events:update:resourceTypes:xxx` anger att resurser med resurstypen `xxx` i en händelse får uppdateras, övrig information i händelsen får inte uppdateras.

## uploads
Anger rättigheter för filer. T.ex. `uploads:read:postersFolder`. Den tredje delen i `uploads`-rättigheten är inte ett resurs-id som för de andra rättigheterna. Här är den tredje delen ett id för en filkatalog. I exemplet här innan ges rättigheten att läsa filer som ligger i katalogen med id `postersFolder`.

Rättigheten `uploads:read:xxx` get automatiskt rättigheten `uploadFolders:read:xxx`.

**create** - Får skapa ny fil  
**read** - Får läsa fil  
**update** - En fil kan inte uppdateras så denna rättighet har ingen effekt.  
**delete** - Får ta bort fil

## assets
!!!!!!!!!!!!!! Vad ska denna vara bra till för? Kan man använda uploadFolders istället?

## uploadFolders
Anger rättigheter för filkataloger. T.ex. `uploadFolders:read:postersFolder`. En filkatalogs id skrivs med kamelNotation. En användare behöver inte ha läsrättighet till en filkatalog för att få läsa, skriva eller ta bort filer från en filkatalog. Det är rättigheten `uploads` som anger rättigheterna för filer.

**create** - Får skapa ny filkatalog  
**read** - Får läsa filkatalog  
**update** - Får uppdatera filkatalog  
**delete** - Får ta bort filkatalog

## locations
Anger rättigheter för lokaler. T.ex. `locations:read:hall`. En lokals id skrivs med kamelNotation.

**create** - Får skapa ny lokal  
**read** - Får läsa lokal  
**update** - Får uppdatera lokal  
**delete** - Får ta bort lokal

## bookings
Anger rättigheter för bokningar. T.ex. `bookings:read:admins`. En boknings id skrivs med ObjectId.

**create** - Får skapa ny bokning  
**read** - Får läsa bokning  
**update** - Får uppdatera bokning  
**delete** - Får ta bort bokning

## posters
Anger rättigheter för affischer. T.ex. `posters:read`. En affischs id skrivs med ObjectId.

För att en användare ska kunna ladda upp filer och hantera affischer så krävs följande rättigheter: `uploads:view`, `uploads:*:posters` och `posters`.

**create** - Får skapa ny affisch  
**read** - Får läsa affisch  
**update** - Får uppdatera affisch  
**delete** - Får ta bort affisch
