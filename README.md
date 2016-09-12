Google Cloud Config
===================

Spring Boot based generic auth and config service for Paxport using Google Cloud Datastore.

Each config store/retrieve requires a paxport-security-token header.

## Create new Security tokens for Agent systems

    POST /v1/auth
    paxport-security-token: <super user security token>
    
    {
        "userId": "paxshop",
        "agentId": "testagent",
        "admin": false,
        "superUser": false,
        "productionAllowed": true,
        "internal": false
    }

* _admin_ is required to upload config for the agent
* _superUser_ is required to create new agent admin tokens

returns something like:

    eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOiJwYXhzaG9wIiwiYWlkIjoidGVzdGFnZW50In0.eWWDuOJMqhEUU63GKV--YNutLEwFDW2mOUBD87PLXKN1LMFDYxwoFX9bwnYurqf2vB0xB6yI9sshZfgyv9E_fg


## Upload a new config object like a PaymentCardSet

    PUT /v1/config/agent/testagent/payment.cards/TEST
    paxport-security-token: <agent admin security token>
    Content-Type: application/json   
    
    {
      "cards" : [ {
        "payor" : "MR BLOGGS",
        "cardNumber" : {
          "value" : "4111111111111111"
        },
        "debitCard" : false,
        "expiryDate" : "2018-08-31",
        "cvc" : "737"
      } ]
    }
    
## Upload credentials for multicommerce

    PUT /v1/config/agent/testagent/multicommerce.credentials/TEST
    paxport-security-token: <agent admin security token>
    Content-Type: application/json
    
    {
      "username" : "tester",
      "password" : "password"
    }

## Store a Global Property

    POST /v1/config/global/example.prop.name
    paxport-security-token: <super user security token>
    
    example_prop_value
    
returns 202 accepted:

    {
        "name": "example.prop",
        "value": "example_prop_value"
    }


## To Release new version to Bintray

    mvn clean release:prepare -Darguments="-Dmaven.javadoc.skip=true"
    mvn release:perform -Darguments="-Dmaven.javadoc.skip=true"