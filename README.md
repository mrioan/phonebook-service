## Introduction

This Spring Boot application exposes a REST interface through which you can create contacts and save associated phone numbers.

## Build Instructions

This project requires Maven 3.3.9 and JDK 8 to build. Run the following:

`> mvn clean compile`

### Testing

This project contains a comprehensive set of [Rest-assured](https://github.com/rest-assured/rest-assured/wiki/Usage) tests.
 
In order to run them, just execute: `> mvn verify`

You can take a look at the actual tests; they are all in `src/test/java/com/centralway/assessment/phonebook/PhonebookWebApplicationTests.java`.

## Run Instructions

In order to get the Phonebook service running simply do:

`> mvn spring-boot:run`

That's it, the Phonebook is now running at `http://localhost:8080`.

## Usage Instructions

By default, an [H2 database](http://www.h2database.com/html/main.html) file is stored under the `./phonebook` directory inside 
your user directory (e.g. `/Users/steve/.phonebook` in a Mac). The location can be changed editing 
`spring.datasource.url` in `src/main/resources/db-config.properties`   

Most of the operations provided by this application (e.g. creating a contact, deleting a contact, adding a phone to a contact, etc)
require the request to be authenticated. Therefore, in order to try it out you will need to register first.

You can use the following pre-existent sample user if you do not want to register yet:

```
Username: peter@example.com
Password: 123123
```

It is important to note that each user will have exclusive access to their own contacts. This means that contacts will be only 
visible and editable by the user who created them. 

### Registration

Sample request to register:
 
```
> curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
-d '{
  "username": "foobar@mailinator.com",
  "password": "Pa$wd567"
}' \
http://localhost:8080/user/register
```

### Managing your Phonebook 

The phonebook provides the following operations:

* Create and delete contacts
* Edit the name associated with a contact
* Add phone numbers to a contact
* Delete contacts
* Get a list of all of my contacts

All of the mentioned operations require authenticated requests (security is enforced by [Spring Security](https://projects.spring.io/spring-security/))
   
IMPORTANT NOTE: The REST API specification requested the token-generation operation to be accessed via the `/user` endpoint.
As requested, such feature has been implemented. However, I have also added an OAuth2's `/oauth/token` endpoint. Both endpoints
can be used interchangeably.

#### Create an access_token

The authenticated operations require the user to send a `token` in order for the request to be processed.

Using `curl`, you can create a token like this:

```
> curl -v -X POST --user 'trusted-app:secret' \
-d 'grant_type=password&username=peter@email.com&password=123123' \
"http://localhost:8080/oauth/token"
```

or like this:

```
> curl -v -X POST -H "Accept: application/json" \
-d '{
   "username": "peter@email.com",
   "password": "123123"
   }' \
"http://localhost:8080/user"
```


TIP: the following command stores the obtained `access_token` in `$ACCESS_TOKEN` so you can simply use the token in subsequent requests:

```
> ACCESS_TOKEN=$(curl -X POST \
--user 'trusted-app:secret' -d 'grant_type=password&username=peter@email.com&password=123123' \
"http://localhost:8080/oauth/token" | \
sed -e 's/^.*"access_token":"\([^"]*\)".*$/\1/')
```


#### Sample requests

* List all contacts belonging to the user identified by means of the token stored in variable $ACCESS_TOKEN:

```
> curl -v -L -X GET \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-H "Cache-Control: no-cache" \
"http://localhost:8080/contacts"
```

Tip: If you have [python](https://www.python.org) installed, you can add `| python -mjson.tool` at the end of this command
in order to get a pretty-print JSON result.

* Add a contact:

```
> curl -v -X POST \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{ 
    "first_name": "Alice",
    "last_name": "Cooper"
    }' \
http://localhost:8080/contacts
```

* Edit a contact (assuming the Alice's ID is 132 and the user authenticated by means of $ACCESS_TOKEN is its owner):

```
> curl -v -X POST \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{ 
    "first_name": "Ali",
    "last_name": "Cooper"
    }' \
http://localhost:8080/contacts/132
```

* Add a phone number to the contact with ID 2 

```
> curl -v -X POST \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{ 
    "phone": "+1 (555) 123-456"
    }' \
"http://localhost:8080/contacts/2/entries"
```


* Delete a contact (assuming you want to delete contact 76 and the user authenticated by means of $ACCESS_TOKEN is its owner):

```
> curl -v -X DELETE \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
http://localhost:8080/contacts/76
```

## Authors

* **Mario Antollini**


