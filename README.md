# Pay My Buddy

PayMyBuddy enables the sharing of real world currency between users in a simple, fast, and secure manner.

## Table of contents

- [Pay My Buddy](#pay-my-buddy)
  - [Table of contents](#table-of-contents)
  - [Running the application](#running-the-application)
    - [Gradle](#gradle)

## Running the application

There is 1 way to run this application:

### Gradle

Run the tests using:

```sh
./gradlew test
```

Run the mutation tests using:

```sh
./gradlew pitest
```

Build the application as an uber JAR (a JAR containing all of its dependencies) using:

```sh
./gradlew bootJar
java -jar build/**.jar # Run
```

Run the application using:

```sh
cp .env.CHANGEME .env
# Edit the `DATABASE_**` variables to match your PostgreSQL instance
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh # Install Rust, used to install the migration tool
cargo install sqlx-cli
sqlx database setup
./gradlew bootRun
```
