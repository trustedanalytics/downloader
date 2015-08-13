DAS downloader service
======================

Local development
-----------------

Often you run this service along with DAS. This can cause port conflicts. In order to avoid it run it:

    $ DOWNLOADS_DIR=/tmp SERVER_PORT=8090 mvn clean spring-boot:run

**DOWNLOADS_DIR** is to specify shared object store between Downloader and DAS

Deployment in a Kerberos-enabled environment
--------------------------------------------
* `mvn verify` - builds the package, runs integration tests and creates manifest.yml
* Set `SPRING_PROFILES_ACTIVE: "cloud,secure"` in manifest.yml
* `cf push`
