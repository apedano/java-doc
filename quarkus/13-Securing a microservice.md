# 13 - Securing a microservice

Code: https://github.com/apedano/kubernetes-native-microservices-sources/tree/ch-13-securing-a-microservice

<img src="images/13/security_diagram.png" width="500">

## Supported authentication mechanisms in Quarkus
Mechanisms with **(*)** are covered in here

<img src="images/13/quarkus_supported_authentication.png" width="500">

## Supported authorization mechanisms in Quarkus

<img src="images/13/quarkus_supported_authorization.png" width="500">

## Using file-based authentication and authorization

One approach to securing the `transaction-service` uses the Quarkus **built-in HTTP
policy configuration** and a **file-based identity provider**.

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-elytron-security-properties-file</artifactId>
</dependency>
```

After we added a method to the `TransactionResource` on a secured (by config) path, we can add the property configuration:

```properties
## Security using Quarkus built-in policy controls

quarkus.http.auth.permission.customer.paths=/transactions/config-secure/*
quarkus.http.auth.permission.customer.methods=GET
#Refines the customer permission to allow only authenticated users
# to access the secured endpoint
quarkus.http.auth.permission.customer.policy=authenticated
# Security - Embedded users/roles (File realm)
%dev.quarkus.security.users.embedded.enabled=true
#Enables clear-text passwords. If set to false or omitted, then the password
# is assumed to be an MD5 password hash.
# Only clear-text and MD5 hashed passwords are currently supported
%dev.quarkus.security.users.embedded.plain-text=true
#Creates user duke with a password of duke
%dev.quarkus.security.users.embedded.users.duke=duke
#Assigns user duke the customer role
%dev.quarkus.security.users.embedded.roles.duke=customer
%dev.quarkus.security.users.embedded.users.quarkus=quarkus
%dev.quarkus.security.users.embedded.roles.quarkus=teller
# Enable HTTP BASIC AUTHENTICATION only during development
#Enables HTTP basic authentication. The user is prompted to provide a username and password when
#using a web browser.
# When using the curl command, user credentials are provided using the --user
#command line option, like --user duke:duke.
%dev.quarkus.http.auth.basic=true
```

```http
GET http://localhost:8088/transactions/config-secure/5465
    HTTP/1.1 401 Unauthorized
    www-authenticate: basic

GET http://localhost:8088/transactions/config-secure/5465
Authorization: Basic duke duke 
    HTTP/1.1 200 OK
    Content-Type: application/json
    239.33
```

### Limit access to specific role

```properties
#Creates a policy, named customer-policy, that grants access to users
# that are assigned the customer role
quarkus.http.auth.policy.customer-policy.roles-allowed=customer
#Applies the customer-policy policy to the customer permission
quarkus.http.auth.permission.customer.policy=customer-policy
#Refines the customer permission to allow only authenticated users
# to access the secured endpoint
#quarkus.http.auth.permission.customer.policy=authenticated

```
Examples with different roles
```
GET http://localhost:8088/transactions/config-secure/5465/balance
Authorization: Basic quarkus quarkus
HTTP/1.1 403 Forbidden

GET http://localhost:8088/transactions/config-secure/5465
Authorization: Basic duke duke 
    HTTP/1.1 200 OK
```

### Test with `test-security` Quarkus extension

Quarkus supports testing secured endpoints by defining users and roles with the
`@TestSecurity` annotation.

```xml
<dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-test-security</artifactId>
 <scope>test</scope>
</dependency>
```

`@TestSecurity` defines a user, `duke`, in the `customer` role.
Because it is applied to the TestSecurity class, it will be applied to all test
methods in the class. This `duke` user applies only when running tests, whereas the embedded `duke` user defined in `application.properties` applies only during development.

```JAVA
@QuarkusTest
@QuarkusTestResource(WiremockAccountService.class)
@TestSecurity(user = "duke", roles = {"customer"})
public class SecurityTest {
    @Test
    public void built_in_security() {
        given()
            .when()
            .get("/transactions/config-secure/{acctNumber}/balance", 121212)
            .then()
            .statusCode(200)
            .body(containsString("435.76"));
    }
}
```

## Oauth 2.0 - An AUTHORIZATION framework

OAuth is an **authorization framework** that allows a client app(could be third party application) to retrieve information from another system using a token which is valid for a limited time. Now, there could be a case where the application users authorize the client app to retrieve information on their behalf. For example, you (user) are logging into Yelp.com (third party app - Client) using your google (Authorization server) account. In this case, OAuth provides a way for delegated authorization which means OAuth provides a way for the client app (Yelp.com) to gain access to a user’s protected resources without asking the user (you) to provide its login credentials to the app.

### Understanding OAuth 2.0 roles:
* **Resource Owner**: An entity capable of granting access to a protected resource. When you log in to Yelp.com using your **google account and you are granting access to yelp to access your basic profile**, in this case, “**You**” are the resource owner. Basically, a resource owner is someone who is capable of granting access to an application on his/her behalf.

* **Client**: An application making protected resource requests. Yelp is the client and the resource owner (“You”) must grant access to the Client(“Yelp”). When Client registers in the developer portal, a client id/secret is issued by the Authorization Server.

* **Authorization server**: The server issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization. In typical use cases, Octa, Ping or any IDP works as Authorization Server.

* **Resource Server**: The server hosting the resources, capable of accepting and responding to protected resource requests using access tokens. This is the server where your APIs( protected resources) are hosted.

* **Authorization Grant**: An authorization grant is a credential representing the resource owner’s authorization (to access its protected resources) used by the client to obtain an access token. This specification defines four grant types: **authorization code**, **implicit**, **resource owner password credentials**, and **client credentials**. Check out the next table for deep dive into different grant types and their use.

* **Redirect URI**: This is the client application’s URI where the Authorization Server will send the authorization code. Sometimes, this called callback URI.

* **Access Token**: Access tokens are credentials used to access protected resources. An access token is a string **representing an authorization issued to the client**. The string is usually opaque to the client. Tokens represent specific scopes and durations of access, granted by the resource owner, and enforced by the resource server and authorization server.

### OAuth 2.0 Grant Types and when to use

#### Client Credentials
Client requests for the access token by sending its client id/secret to the authorization server and once the access token is issued, the client app uses the access token to make the API call to the protected resources.

<img src="images/13/client-credentials.jpg" width="500">

* Typically used for B2B use cases.
* The client app is the resource owner.
* Typical use cases are intranet app but work with highly trusted internet applications that are written by the internal developer or trusted partner’s application developer.

#### Authorization code

<img src="images/13/authorization-code.png" width="600">


* Typical use case when untrusted apps are written by third-party developers who do not have a trusted business relationship with the API provider.
* Requires users log-in to the resource server
* Client app needs to be registered in the resource server and the client app never gets hold of user login credentials.

#### Implicit

This is similar to the Authorization code flow, except the authorization server sends the access token instead of the authorization code directly to the client app after capturing consent from the user.

<img src="images/13/implicit.png" width="500">

* Typical use case when trusted apps written by internal or trusted third-party developers.
* Requires users log-in to the resource server
* Client app needs to be registered in the resource server and the client app never gets hold of user login credentials.

## OpenId Connect - Oauth extension for AUTHENTICATION (ID Token)

When Login with Facebook or Google introduced for the first time , sometime in 2014, **OAuth started to be used for Authentication**, not only for Authorization. This means when the Client gets the access token from the authorization server, the client wants to know a few user information like name, address, etc. that is already available in the authorization server, therefore the authorization server is capable of sending the info back to the client. Now, OAuth is designed for Authorization (permission, scopes, easier access to the protected resources), not for Authentication. So, it is bad to send the user information because **there is no standard way to send the user information back to the client**. **`OpenId Connect` is going comes in play when you need to pass user info after client authorization**.

So, OAuth is for Authorization, which means no user information will be returned in the response of the access token call. If the OAuth token response looks like the below, then consider this OAuth implementation doesn’t follow the OAuth standard and it is bad.

OpenId Connet is a kind of add-on top of OAuth 2.0. What `OpenId Connect` adds

* It issues an **ID Token**
* `UserInfo endpoint` for getting more user information. Basically it means if an  Authorization server understands ID token, it can provide more user information when the client makes `/userInfo/<ID Token>` endpoint with the **ID token**. 

In red the difference with the authorization flow applied by OpenId Connect

<img src="images/13/authentication_code.png" width="600">

## Authentication and authorization with `OpenID Connect` (`OIDC`)

In this section, we use OpenID Connect (OIDC) to access new secure REST endpoints
in the `bank-service`.

## Keycloak

Keycloak is used as Identity Provider, supposely installed as a Kubernetes Operator (it requires 5Gb of memory). We will use the Docker version.

### Installation

Here we are using a version without the backed database (ephemeral) because we will import the real settings from the file `bank-realm.json` at startup with the command `import-realm`

```yaml
auth:
image: quay.io/keycloak/keycloak
ports:
    - "8070:8080"
environment:
    KEYCLOAK_ADMIN: admin
    KEYCLOAK_ADMIN_PASSWORD: admin
command:
    - start-dev
    - --import-realm
volumes:
    - ./bank-realm.json:/opt/keycloak/data/import/realm.json
```
By logging with the indicated credentials on the exposed port we can manage the realm from the GUI.

<img src="images/13/bank-realm.png" width="400">

### Accessing a protected resource with OpenID Connect

OIDC Authorization Code Flow defers user authentication to an authentication server,
Keycloak, in this case
<img src="images/13/keycloak_authentication_code.png" width="500">

1. The user accesses a protected resource, perhaps protected by the built-in HTTP
security policy or `@RolesAllowed`.
2. The Bank service will redirect the user to the OIDC provider (Keycloak) specified with the `quarkus.oidc.auth-server-url` property. 
3. The OIDC provider presents the user with an authentication form to enter a
username and password.
4. Upon successful authentication, Keycloak returns a **JWT token** and an **HTTP
redirect** to the originally requested resource. We explain JWT’s role in the
authorization shortly.
1. The browser is redirected to the protected resource (using the access token).
2. The service checks the access token (extract the role, check expiration) and if it is successfully validated returns the resource content.

In the `bank-service` add the following properties

```properties
#The URL for Keycloak as an OIDC authorization server.
# The bank realm is specified in the URL
%dev.quarkus.oidc.auth-server-url=http://localhost:8180/realms/bank
%prod.quarkus.oidc.auth-server-url=https://keycloak:8443/realms/bank
#The OIDC client ID. An OIDC client ID typically has an associated client secret (e.g., password), the
#credentials for a client to access an identity provider.
# To keep things simple, the bank client does not have a client secret.
quarkus.oidc.client-id=bank
#this is the secret of the client on Keycloak Clients->Client details->credentials-client secret
quarkus.oidc.credentials.secret=4DePVOuZxVKNGYD9FLQk9sQ2SCT0QMYZ
#Uses the Authorization Code Flow (web app).
# The OIDC extension redirects the user to a Keycloak provided login screen.
quarkus.oidc.application-type=web-app
#The Bank service, copied from chapter 3, requires the username and password properties to be
#defined. These properties are not used in this example.
quarkus.oidc.authentication.fail-on-missing-state-param=false
```

And the following secured endpoint
```java
@RolesAllowed("bankadmin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/secure/secrets")
    public Map<String, String> secureGetSecrets() {
        return getSecrets();
    }
```

The call to the secured endpoint http://localhost:8286/bank/secure/secrets 

```shell
[]Starting an authentication challenge
[]Authentication request redirect_uri parameter: http://localhost:8286/bank/secure/secrets
    q_auth_a0b01fff-c24c-4bf0-8345-e28403d608af cookie 'max-age' parameter is set to 1800
[]Code flow redirect to: http://localhost:8180/realms/bank/protocol/openid-connect/auth?response_type=code&client_id=bank&scope=openid&redirect_uri=http%3A%2F%2Flocalhost%3A8286%2Fbank%2Fsecure%2Fsecrets&state=a0b01fff-c24c-4bf0-8345-e28403d608af
```
The browser is redirected to the Keycloacl login page

Once logged in (this will create a `Session` in Keycloak), then the application will negotiate an acces token with the authorization code and, once the token has been verified


```shell
[] Registered TenantResolver has not provided the configuration for tenant 'a0b01fff-c24c-4bf0-8345-e28403d608af', using the default tenant
[] State cookie is present, processing an expected redirect from the OIDC provider
[] Authorization code is present, completing the code flow

[] Exchanging the authorization code for the tokens
[] Token request redirect_uri parameter: http://localhost:8286/bank/secure/secrets
[] Get token on: http://localhost:8180/realms/bank/protocol/openid-connect/token params: grant_type=authorization_code
code=57348c77-7bc4-4a6c-bd07-00e61f943271.7b5bef36-6652-44f4-a3e0-6b7994309732.c75aa0dd-b7f2-421c-99de-fc7cb31597b2
redirect_uri=http://localhost:8286/bank/secure/secrets
 headers: user-agent=Vert.x-WebClient/4.4.5
content-type=application/x-www-form-urlencoded
accept=application/json
authorization=Basic YmFuazo0RGVQVk91WnhWS05HWUQ5RkxRazlzUTJTQ1QwUU1ZWg==

[] Request succeeded: {"access_token":"***","not-before-policy":0,"session_state":"7b5bef36-6652-44f4-a3e0-6b7994309732","scope":"openid microprofile-jwt email profile"}
[] Authorization code has been exchanged, verifying ID token
[] Starting creating SecurityIdentity
[] ID token has been verified, removing the existing session cookie if any and creating a new one
[] ID token is valid for 300 seconds
[] q_session cookie 'max-age' parameter is set to 300
[] Removing code flow redirect parameters, final redirect URI: http://localhost:8286/bank/secure/secrets
[] Starting the final redirect
```
Then the browser is finally redirected to the endpoint authorized by the access token

### Testing the code authorization  flow
Quarkus offers an OIDC WireMock that can replace a Keycloak instance during a test

```xml
<!--The dependency that includes the Quarkus OIDC WireMock server-->
<dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-test-oidc-server</artifactId> d
 <scope>test</scope>
</dependency>
<!--UI-less web browser. The testing uses HtmlUnit to browse through the Keycloak login UI-->
<dependency>
 <groupId>net.sourceforge.htmlunit</groupId>
 <artifactId>htmlunit</artifactId> e
 <version>2.36.0</version>
 <scope>test</scope>
</dependency>
```

`BankTest.java`

## Json Web Tokens (JWT) and MicroProfile JWT

Like the Bank example used throughout this book, a microservices architecture often revolves around REST APIs, which in turn require REST security. REST microservices tend to be stateless, so they benefit from the **stateless security approach offered by
JWT**. The security state is encapsulated in lightweight *JSON Web Tokens (JWT)* defined in RFC 7519 (https://datatracker.ietf.org/doc/html/rfc7519). Because JWTs are lightweight, they are propagated efficiently through a chain of REST service calls.

Because **JWTs are not encrypted**, it is highly recommended that JWTs are transferred over a secure transport layer, like HTTPS.

### JWT structure
A JWT contains three sections —a **header**, a **payload**, and a **signature**— with a dot separating each section (“.”). 

<img src="images/13/jwt_structure.png" width="600">

#### Header

```json
{
  "alg": "HS256",
  "typ": "JWT",
  "kid": "XSFQUParnbCfeHqvMQ_-b8bm7zkeoFAKEyrNPh3qZko"
}
```

<img src="images/13/jwt_header.png" width="600">

#### Payload

A JWT payload consists of a collection of **standardized claims** defined by RFC 7519.
MicroProfile JWT extends these standardized claims, and developers can also add **custom claims** if needed.

The token can also be injected in the Microprofile context

```java
/**
 * Injection point for the Access Token issued
 * by the OpenID Connect Provider
 */
@Inject
JsonWebToken accessToken;

@GET
@Path("/tokeninfo")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public Set<String> token() {
    return accessToken.getClaimNames().stream()
            .map(claimName -> claimName + " = " + accessToken.getClaim(claimName))
            .collect(Collectors.toSet());
}
```

the content is

```json
{
  "exp": 1702455951,
  "iat": 1702455651,
  "auth_time": 1702455651,
  "jti": "f79fe81b-1dd3-48ef-a0cf-57fe22fd8041",
  "iss": "http://localhost:8180/realms/bank",
  "sub": "b88c15c5-a1da-4b83-8d63-f4416c75367b",
  "typ": "Bearer",
  "azp": "bank",
  "session_state": "96c0fbe3-fed5-4806-afd8-a69d7f391961",
  "acr": "1",
  "allowed-origins": [
    "*"
  ],
  "realm_access": {
    "roles": [
      "bankadmin"
    ]
  },
  "scope": "openid microprofile-jwt email profile",
  "sid": "96c0fbe3-fed5-4806-afd8-a69d7f391961",
  "upn": "admin",
  "email_verified": false,
  "groups": [
    "bankadmin"
  ],
  "preferred_username": "admin"
}
```
| Claim | Name             | Description                                                   |
| ----- | ---------------- | ------------------------------------------------------------- |
| *iss* | issuer           | Issuer of the JWT                                             |
| *sub* | subject          | Subject of the JWT (the user)                                 |
| *aud* | audience         | Recipient for which the JWT is intended                       |
| *exp* | expiration time  | Time after which the JWT expires                              |
| *nbf* | not before time  | Time before which the JWT must not be accepted for processing |
| *jti* | JWT ID           | JWT unique identifier; to prevent a JWT from being replayed.  |
| *azp* | Authorized party | the party to which the ID Token was issued                    |


The full list is in https://www.iana.org/assignments/jwt/jwt.xhtml#claims

##### Microprofile JWT custom claims

* *upn*: A human-readable MicroProfile JWT custom claim that uniquely identifies the subject or user 
principal of the token across all services that will access the token. This claim is the user principal name in `java.security.Principal.JsonWebToken` extends `java.security.Principal`, so it can be used by existing frameworks that support `java.security.Principal`. If this claim is missing, MicroProfile JWT will fall back to the `preferred_
username` claim. If `preferred_username` is missing, the `sub` claim is used.

* *groups*: MicroProfile JWT custom claim that lists the groups the principal belongs to.  Its value determines method access
  
The remaining claims have been configured by the Keycloak administrator and are not 
directly relevant to MicroProfile JWT

#### Signature

Each JWT is signed using the algorithm defined in the header alg claim to ensure it has
not been tampered with.

If the signature in the token matches the following
```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  <PUBLIC_KEY>
)
```
then the token is verified. The public key is the one in the Keycloack certificate.

## Securing micro service using MicroProfile JWT

We will secure the `transaction-service` using a JWT token

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-jwt</artifactId>
</dependency>
```

The public key to verify the jwt signature is scoped wiht the realm and it is available at http://localhost:8080/realms/bank/ (http://localhost:8080/realms/<realm>/)

```properties
# Configure MicroProfile JWT
mp.jwt.verify.publickey=MIIBI...
# this has to match the iss claim in the JWT token
mp.jwt.verify.issuer=http://localhost:8080/realms/bank
```

```java
@GET
@RolesAllowed("customer")
@Path("/jwt-secure/{acctnumber}/balance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public Response jwtGetBalance(
        @PathParam("acctnumber") Long accountNumber) {
    return getBalance(accountNumber);
}
```

The token can be generated simulating the **implicit flow** with a POST request (the credentials in the content are for the `customer` role):

```
POST http://localhost:8080/realms/bank/protocol/openid-connect/token
content-type: application/x-www-form-urlencoded

username=jwt&password=jwt&grant_type=password&client_id=bank&client_secret=4DePVOuZxVKNGYD9FLQk9sQ2SCT0QMYZ
```

### Propagating the JWT

A request may travel across multiple secured microservices. The **security context can travel with the request using a JWT to enable access to those secured microservices**. The remainder of the JWT discussion switches from the OIDC Code Authorization Flow to the **OIDC Implicit Flow**, where the JWT is obtained directly from Keycloak and is propagated with HTTP requests.

#### Secure an Account service endpoint

On the `account-service`

We first add the same dependency `quarkus-smallrye-jwt` and the same properties as in the transaction service

Then we create the secured method:

```java
@RolesAllowed("customer")
@GET
@Path("/jwt-secure/{acctNumber}/balance")
public BigDecimal getBalanceJWT(
        @PathParam("acctNumber") Long accountNumber) {
    return getBalance(accountNumber);
}
```

#### Propagating JWT from the Transaction service to the Account service

Also the RestClient `AccountService` in the `transaction-service` will add a new method

```java
@GET
@Path("/jwt-secure/{acctNumber}/balance")
BigDecimal getBalanceJwtSecured(@PathParam("acctNumber") Long accountNumber);
```

and the `TransactionResource` 

```java
@GET
@RolesAllowed("customer")
@Path("/jwt-secure/{acctnumber}/balance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public Response jwtGetBalance(
        @PathParam("acctnumber") Long accountNumber) {
    BigDecimal balance = accountService.getBalanceJwtSecured(accountNumber);
    return Response.ok(balance).build();
}
```
One last step is required for the Transaction service to access the Account Service securely. The JWT contains the user identity information, including the user’s role. Therefore, the JWT must be propagated from the `transaction-service` to the `account-service` on each request, so the role is available to `AccountService.getBalanceJWT()`.

We have to configure the RestClient in the `transaction-service` to transfer the **Authorization Bearer** header to the call to the `account-service`

```properties
org.eclipse.microprofile.rest.client.propagateHeaders=Special-Header,Authorization
```
Now, if the called the JWT secured `TransactionResource::jwtGetBalance` which calls the `AccountService::getBalanceJwtSecured`, the call to the `account-service` will forward the headers from the incoming request, containing the `Authatization` one, authorizing the call on the `account-service` as well.




