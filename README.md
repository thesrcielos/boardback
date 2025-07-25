# 🧩 Interactive Board App – Features

This is a realtime based app to make a collaborative drawing board

## 🔐 Authentication
- **OAuth2 Login**: Users can sign in using third-party providers like Google.
- **Username & Password**: Traditional login with secure credential validation.
- **Token-based Access**: WebSocket connections are authenticated via token for real-time security.

## 🖊️ Interactive Board
- **Collaborative Drawing**: Users can draw on a shared canvas in real-time.
- **Persistent Board State**: The board state is stored and shared with new users when they join.
- **Clear Functionality**: Anyone can reset the board with a single command.

## 💬 Chat System
- **Real-time Messaging**: Users can send chat messages instantly.
- **Integrated with Board**: The chat complements the board for seamless collaboration.
- **No Message Persistence**: Chat history is temporary and resets with each session.

## Redis Pub Sub
- **Message Broker**: A simple message broker for Message Publish and Subscription

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

### HTTP ENDPOINTS
* /api/auth/login to login in the app
* /api/auth/register registration endpoint
* /api/auth/token to turn the OAuth2.0 code in a token
* /ws-auth/token to get the Auth token for the WS services
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

## Jacoco

First add the jacoco plugin to the pom.xml plugins

Run the tests with
```
mvn clean verify
```

![](assets/1.png)

Go to *target/site/jacoco/index.html* nad check the coverage

![](assets/2.png)

## SonarQube
First install Sonar with docker image with the command
```
docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
```
Go to localhost:9000, log in with admin admin and change the password and create a new project
![](assets/3.png)
Go to the project and create a token
![](assets/4.png)
In your pom.xml add the sonar properties
![](assets/5.png)
Also, you need to add the sonar plugin
![](assets/6.png)
Run the following command to connect with sonar
![](assets/7.png)
Go to your project again and look at the analysis
![](assets/8.png)
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
