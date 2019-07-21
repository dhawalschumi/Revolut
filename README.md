# Revolut

This application consists of two apis namely ***transfer*** and ***accountDetails***

## Design 
![Revolut Money Transfer](https://github.com/dhawalschumi/Revolut/blob/master/Revolut.jpg)

**1. Ratpack** - *Ratpack is a set of Java libraries for building scalable HTTP applications. Reason of choosing this is because its light weight, its not a servlet based web container(ex Tomcat etc), it communicates directly with underlying sockets and supports all HTTP paradigms*

**2. TransfersHandler** - *Ratpack Handler for handling requests to **transfers** API. TransfersHandler communicates with **TransfersService** for performing transfers between two accounts. This handler also validates and reads requests and handles  responses depending upon success or failures.* 

**3. AccountDetails Handler** - *Ratpack Handler for handling requests to **accountDetails** api. AccountDetails Handler communicates with **AccountService** for fetching account details of a customer.  This handler also validates and reads requests and handles  responses depending upon success or failures.* 

**4. TransfersService** - *TransfersService actually performs the transfers between the two accounts. TransfersService communicates with **BalanceService** and **AccountService** for performing transfers amount between accounts. 

**5. BalanceService** - *BalanceService manages the balances of accounts, like performing credit and debit operations for accounts. For doing so it communicates with **QueryService** *

**6. AccountsService** - *AccountService manages(What's account number for a customer?) customer accounts. AccountsService depends on **QueryService** for managing the customer accounts. *

**7. QueryService** - *QueryService has set of operations which are performed by **AccountsService & BalanceService**, this service is only way to communicate with Database. *

**8. DatasbeManager** - *DatabaseManager manages connections to the in-memory database. Whenever **QueryService** wants to perform a databse query, connection to perform the same is provided by DatabaseManager. Also during JVM startup it sources the tables and data from build_tables.sql. *

### Technology / Libraries 

1. Ratpack
2. Java 11
3. MariaDB4j - In Memory MariaDB
4. Junit 4
5. Apache Commons DBCP
