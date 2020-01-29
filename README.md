# Address Book

A RESTful API for a virtual address book containing contact's address and various phone numbers.

## Assesment completion

**Endpoints**

| Requirement | Location    | Completion |
| ----------- | ----------- | ---------- |
| **GET** /contact?pageSize={}&page={}&query={} | [Server.java - L99](src/main/java/me/coley/addressbook/Server.java#L99) | :heavy_check_mark:  |
| **POST** /contact          | [Server.java - L88](src/main/java/me/coley/addressbook/Server.java#L88) | :heavy_check_mark:  |
| **GET** /contact/{name}    | [Server.java - L118](src/main/java/me/coley/addressbook/Server.java#L118) | :heavy_check_mark:  |
| **PUT** /contact/{name}    | [Server.java - L112](src/main/java/me/coley/addressbook/Server.java#L112) | :heavy_check_mark:  |
| **DELETE** /contact/{name} | [Server.java - L94](src/main/java/me/coley/addressbook/Server.java#L94) | :heavy_check_mark:  |

**Technical Requirements**

| Requirement | Location    | Completion |
| ----------- | ----------- | ---------- |
| Hosted on GitHub         | :wave: | :heavy_check_mark:  |
| Backed by ElasticSearch  | [ElasticContactService.java](src/main/java/me/coley/addressbook/service/impl/ElasticContactService.java) | :heavy_check_mark:  |
| Sensible data model      | [me.coley.addressbook.model](src/main/java/me/coley/addressbook/model) | :heavy_check_mark:  |
| HTTP REST architecture   | [Server.java](src/main/java/me/coley/addressbook/Server.java) | :heavy_check_mark:  |
| Customizable <br><ul><li>ElasticSearch version</li><li>ElasticSearch port</li><li>Use existing ElasticSearch server, or use a bundled one</li><li>SparkJava port</li></ul> | [Options.java](src/main/java/me/coley/addressbook/Options.java) | :heavy_check_mark:  |
| Unit tests               | [test-directory](src/test/java/me/coley/addressbook) | :heavy_check_mark: - [coverage.png](coverage.png) |

**Bonus**

* Full JavaDocs, prebuilt in: [apidocs](apidocs)
* Fully independent, can `git clone ...` then `mvn clean package` with no additional setup 
   * ElasticSearch's server will be dynamically fetched & started

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

## Interaction

**Reccomended**

* Firefox: [RESTer](https://github.com/frigus02/RESTer) - [Screenshot](rester.png)

## Additional information

* No prior experience with _ElasticSearch_
* Time breakdown:
    * 3 hours of documentation, mostly on ElasticSearch _(hopping between versions docs due to API changes / deprecation)_
    * 4 hours programming `src/main/...`
    * 3 hours programming `src/test/...`
* Various time-sinks:
	* Figuring out a portable solution for controlling all the options for ElasticSearch
	* Working on ElasticSearch latest `7.X` before discovering the bundler only supports up to `6.X`
	* Attempting to use mockito _(and giving up due to rigid api's that mockito can't support)_ instead of a bundled ElasticSearch server to mock the backing high-level rest client.
	* Refactoring to `src/main/...` classes to achieve higher code-coverage in tests.