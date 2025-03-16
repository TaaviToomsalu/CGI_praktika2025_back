# Lennupiletite Broneerimisrakendus back

## Projekti Ülevaade
See on Spring Boot backend rakendus lennuistekohtade haldamiseks ja reserveerimiseks.

## Tehnoloogiad
Spring Boot - backend raamistiku jaoks

Spring Web - REST API loomiseks

Spring Data JPA - andmete haldamiseks

H2 Database - sisseehitatud andmebaas

## Käivitamine

### Nõuded:

Java 17 või uuem

Maven

### Käivitamine lokaalselt

Klooni repo:

```sh
git clone https://github.com/TaaviToomsalu/CGI_praktika2025_back
cd CGI_praktika2025_back
```

### Käivita rakendus:

```sh
mvn spring-boot:run
```

Backend on saadaval aadressil: http://localhost:8080

## API Endpointid

### Istekohtade päringud

Kõik istekoha valikud lennu jaoks

GET /seats/{flightId}

 Parameetrid:

flightId – lennu ID

class (valikuline) – istekoha klass (ECONOMY, BUSINESS jne)

### Soovitatud istekohad

GET /seats/{flightId}/suggest

Parameetrid:

numSeats – soovitud kohtade arv

preferences – soovid (nt window, aisle)

requireAdjacent (valikuline, vaikimisi false) – kas istekohad peavad olema kõrvuti

seatClass – istekoha klass

### Saadaolevad istekohad

GET /seats/{flightId}/available

Parameetrid:

flightId – lennu ID

### Istekoha reserveerimine

Reserveeri istekohad

PUT /seats/reserve

Keha (JSON):

{
"flightId": 123,
"seatIds": ["1A", "1B"]
}

## Andmebaas

Kasutusel on H2 andmebaas. Andmed initsialiseeritakse automaatselt.

H2 konsoolile pääsemiseks ava brauseris:

http://localhost:8080/h2-console

### Seaded:

JDBC URL: jdbc:h2:mem:testdb

Kasutajanimi: sa

Parool:   (tühi)
