# Interactive Board

This is a realtime based app to make a collaborative drawing board
## Getting Started

These instructions will help you get a copy of the project running on your local machine for development and testing purposes.

### Prerequisites

You will need the following installed:

- [Java 17+](https://jdk.java.net/)
- [Maven 3.8+](https://maven.apache.org/install.html)
- Git (optional, for cloning)

### Installing

Clone the repository:

``` 
git clone https://github.com/thesrcielos/boardback.git
cd boardback
```

Build the project using Maven:
```
mvn clean install
```

### Running the Application

You can run the program with:

```
java -cp target/board-0.0.1-SNAPSHOT.jar
```

This will run the websocket server receiving connection in /bbService

### WebSocket
* /bbService in this endpoint the client connects and send messages
### Coding Style

The code follows the Google Java Style Guide and was formatted accordingly.

## Deployment

To package:
```
mvn package
```
Then run using:
```
java -cp target/board-0.0.1-SNAPSHOT.jar
```

## 📚 Javadoc

Code documentation is available in `target/site/apidocs/index.html` after running the command:

```bash
mvn javadoc:javadoc
````

## Built With

* [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

* [Maven](https://maven.apache.org/) - Dependency Management

* [ JUnit 4](https://junit.org/junit4/)

## Contributing

Please read **CONTRIBUTING.md** for details on how to contribute.

## Authors

* Diego Armando Macia Diaz – Initial work

## License

* This project is licensed under the GNU License – see the LICENSE.md file.
