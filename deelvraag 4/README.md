# Migratie Spring Boot Rest 

Deze repo hoort bij deelvraag 4 van de scriptie Lambda frameworks voor Java. Hierbij wordt er een bestaande REST-applicatie omgezet naar een serverless omgeving met gebruik van het framework Quarkus.

## Opzet

In de folder ``/springboot`` staat de bestaande spring boot REST-applicatie.
Dit is een ToDo-list applicatie die bestaat uit twee services de BoardService en de UserService. 
Bij de BoardService hoort alles wat betrekking heeft met het onderhouden van een ToDo-list hier horen de volgende use cases bij
- Als gebruiker wil ik taken aanmaken met een naam en beschrijving, zodat ik overzicht kan creëren in mijn taken
- Als gebruiker wil ik lijsten (kolommen) maken en taken daarin plaatsen, zodat ik overzicht heb van de status of categorisering van de taken
- Als gebruiker wil ik taken kunnen wisselen van lijst, zodat ik voortgang of werkzaamheden inzichtelijk heb
- Als gebruiker wil ik taken kunnen aanpassen, zodat ik informatie kan toevoegen of aanpassen
- Als nieuwe gebruiker wil ik mij kunnen registreren, zodat ik kan samenwerken (Er hoeft geen gebruik gemaakt te worden van security token zoals JWT. UserId doorgeven is voor deze opdracht voldoende)
- Als gebruiker wil ik andere gebruikers toekennen aan taken, zodat het duidelijk is wie aan welke taak werkt (service moet controleren dat gebruiker bestaat)
- Als gebruiker wil ik meerdere borden met taken kunnen aanmaken, zodat ik verschillende werkgroepen uit elkaar kan houden
- Als gebruiker wil ik andere gebruikers rechten kunnen geven op een bord, zodat ik kan samenwerken met hen (service moet controleren dat gebruiker bestaat en rechten heeft op het bord)
- Als gebruiker wil ik tags kunnen toekennen aan taken en van taken kunnen verwijderen

Dezelfde use cases zullen uitgeprogrammeerd worden, maar dan nu met behulp van het Quarkus Framework voor gebruik in AWS Lambda.
In de ``/docs`` folder zijn er foto's te vinden van het originele ontwerp.
Dit is uitgewerkt met het c4 + 1 model.
Dit model zal verder gevolgd worden in de serverless applicatie.
Er zal wel een nieuw model gemaakt worden voor de serverless applicatie dit zal een Deployment diagram zijn.
Hierdoor zal het duidelijk worden hoe de Lambda's met elkaar communiceren.