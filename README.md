# Address Book

A RESTful API for a virtual address book containing contact's address and various phone numbers.

## Endpoints

| Endpoint | Source location |
| ----------- | ----------- |
| **GET** /contact?pageSize={}&page={}&query={} | [Server.java - L99](src/main/java/me/coley/addressbook/Server.java#L99) |
| **POST** /contact          | [Server.java - L88](src/main/java/me/coley/addressbook/Server.java#L88)   |
| **GET** /contact/{name}    | [Server.java - L118](src/main/java/me/coley/addressbook/Server.java#L118) |
| **PUT** /contact/{name}    | [Server.java - L112](src/main/java/me/coley/addressbook/Server.java#L112) |
| **DELETE** /contact/{name} | [Server.java - L94](src/main/java/me/coley/addressbook/Server.java#L94)   |

## Features

| Feature | Source location | 
| ----------- | ----------- | 
| Backed by ElasticSearch  | [ElasticContactService.java](src/main/java/me/coley/addressbook/service/impl/ElasticContactService.java) |
| Sensible data model      | [me.coley.addressbook.model](src/main/java/me/coley/addressbook/model) |
| HTTP REST architecture   | [Server.java](src/main/java/me/coley/addressbook/Server.java) |
| Customizable <br><ul><li>ElasticSearch version</li><li>ElasticSearch port</li><li>Use existing ElasticSearch server, or use a bundled one</li><li>SparkJava port</li></ul> | [Options.java](src/main/java/me/coley/addressbook/Options.java) |
| Unit tests <br><ul><li>[coverage.png](coverage.png)</li></ul> | [test-directory](src/test/java/me/coley/addressbook) |
| Prebuilt JavaDocs        | [apidocs](apidocs) | :heavy_check_mark: |
| Self-Contained<br><ul><li>ElasticSearch server automatically downloaded and started if no existing server is specified</li></ul> | [Server.java - L61](src/main/java/me/coley/addressbook/Server.java#L61) | :heavy_check_mark: |

## Command line usage

Usage can be displayed by invoking the program with the flag `-help`, alternatively you can simply refer to the table below:

* Can be invoked easily with `java -jar target/addressbook-1.0.0-jar-with-dependencies.jar` after compiling via `mvn clean package`

```markdown
Usage: me.coley.addressbook.Server [-existing] [-eport=<elasticPort>]
                    [-eversion=<elasticVersion>] [-sport=<sparkPort>]
```

| Argument | Description | Default Value |
| -------- | ----------- | ------------- |
| existing | Use an currently running ElasticSearch server     | false |
| eversion | ElasticSearch version                             | 6.8.6 |
| eport    | ElasticSearch port                                | 9200  |
| sport    | SparkJava port                                    | 25565 |

## Service usage 

**Reccomended**

* Firefox: [RESTer](https://github.com/frigus02/RESTer) - [Screenshot](rester.png)
