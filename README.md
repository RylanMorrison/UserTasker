A practice RESTful HTTP service that manages Users and Tasks for Users.

Users can be created, updated and listed. Tasks can be created, updated, deleted and listed.
Users and Tasks are persisted using a H2 database and JPA.

Build: mvn package

Run: java -jar UserTasker-1.0-SNAPSHOT.jar

Displays an 'illegal reflective access' warning if run using Java9+ due to a change in how restrictive Java is to reflection.
To remove the warning run with --add-opens java.base/java.lang=ALL-UNNAMED
