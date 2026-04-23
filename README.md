# transaction-risking

The VAT Transactional Risking (TxR), publicly known as VAT Assist. It is a microservice designed to identify and flag potential risks or inaccuracies in VAT Returns before submission by showing users helpful messages.

The service acts as a pre-submission check within the normal VAT submission process. Since users submit VAT returns via accounting software (e.g. Sage, QuickBooks, Xero), software vendors are required to integrate VAT Assist into their products in order for users to see the relevant messages.

---

## Requirements

- Scala 3.3.6
- Java 21 
- sbt 1.10.x
- MongoDB 

---

## Running the Service

Start MongoDB locally (replica set required):

```bash
docker run --restart unless-stopped --name mongodb -p 27017:27017 -d percona/percona-server-mongodb:7.0-multi --replSet rs0
```

Then initialise the replica set (first time only):

```bash
docker exec -it mongo mongosh --eval "rs.initiate()"
```

Run the service via sbt:

```bash
sbt run
```

The service starts on **port 9000** by default.

---

## Running Tests

```bash
# Unit tests
sbt test

# Integration tests
sbt it/test

# All tests with coverage
sbt clean compile coverage test it/test coverageReport
```

## Endpoints

| Method | Path        | Description  |
|--------|-------------|--------------|
| GET    | `/ping/ping`| Health check |

---

## License

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
