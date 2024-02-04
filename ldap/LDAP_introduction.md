# LDAP

## What is LDAP

https://www.redhat.com/it/topics/security/what-is-ldap-authentication

https://medium.com/@nisha.narayanan/securing-your-api-connect-cloud-with-ldap-e214f6886d29
https://ldaptor.readthedocs.io/en/latest/ldap-intro.html


LDAP stands for Lightweight Directory Access Protocol. 
It is an industry standard application protocol (RFC [here](https://tools.ietf.org/html/rfc4511)) that serves to define an interface or language with which client applications can talk to a directory service (such as OpenLDAP, Active Directory etc.) to query or modify the information in the directory.

An **LDAP directory (or server)** typically stores information about _users, user credentials, groups, user memberships_ and so on. Since they act as a central repository for user information, they are commonly used for user authentication and authorization.

You can think of an **LDAP directory** as a _data store that supports client applications to communicate with it using the LDAP protocol_. That said, it is common to hear people using the term LDAP to refer to both the protocol and the directory.

### LDAP goals

LDAP has two main goals: 
* **to store data in the LDAP directory and authenticate users to access the directory**. It also provides the communication language that applications require to send and receive information from directory services. A directory service provides access to where information on organizations, individuals, and other data is located within a network.

* The most common LDAP use case is **providing a central location for accessing and managing directory services**. _LDAP enables organizations to store, manage, and secure information about the organization, its users, and assets–like usernames and passwords_. This helps simplify storage access by providing a hierarchical structure of information, and it can be critical for corporations as they grow and acquire more user data and assets.

* LDAP also functions as an **identity and access management (IAM)** solution that targets _user authentication_, including support for Kerberos and single sign-on (SSO), _Simple Authentication Security Layer_ (SASL), and _Secure Sockets Layer_ (SSL).

### LDAP directory structure DIT

An **LDAP directory** has a hierarchical _tree-like structure and consists of one or more entries_.
It is called **DIT** (_Directory Information Tree_).
A typical DIT has the following structure of different type of **entries**
* _main directory_ (**dc**)
*  _organization name_ (**o**) or the _country_ (**c**)
* _organizational unit_ (**ou**)
* _common names_ (**cn**):  is used to identify the name of a group or individual user account 
  (ex. `cn=developers, cn=Susan`). A user can belong to a group, so if Susan is a developer, they could also live under cn=developers.


The entries generally represent real world entities such as organizations, users and so on. 
For an enterprise, for example, the top or **root of the tree** could represent the _organization itself_. 
This can be followed by **child entries** that can be used to _represent organizational or business units_, say, by location or function. 
These can further support more entries representing _individual resources like users, groups etc_. like shown in the figure below.

<img src="images/ldap_structure.png" width="500">

### Names
* **Distinguished name (DN)**: contains a path through the directory information tree (DIT) for LDAP identify a specific entry (ex. `cn=Ample Exam, ou=Sales, ou=People, dc=example, dc=com`)
* **Relative Distinguished Name (RDN)**: each component in the path within the DN (ex. `cn=Ample Exam`)

### Attributes
Each entry typically has one or more attributes that are used to describe the object 
(such as first name, last name, email, business unit etc.). These are modeled as name/value pairs.
The LDAP specification defines a standard set of attributes (eg: _cn, sn, mail, objectClass_ etc.)

```
dn: cn=triddle,ou=users,dc=hogwarts,dc=com
objectClass: organizationalPerson
objectClass: person
objectClass: inetOrgPerson
objectClass: top
cn: triddle
sn: Riddle
displayName: Tom Riddle
givenName: Tom
mail: Tom.Riddle@hogwarts.edu
uid: 20007
userPassword: SHA hashed password
```

## User authentication using LDAP

To successfully authenticate a user against an LDAP server, you are required to do, what in LDAP terms is called a _bind_. 
This is simply the process of authentication and expects a username and password. 
The **username in this case will be the DN of the LDAP entry**.

```
username: cn=triddle,ou=users,dc=hogwarts,dc=com
password: <userPassword_attribute>
```

## Run OpenLDAP with Docker
Start an OpenLDAP container.

we can use the [docker-compose.yaml](docker-compose.yaml) file.

### Volume mounts
With the configuration

```yaml
volumes:
  - ./volumes/database:/var/lib/ldap
  - ./volumes/config:/etc/ldap/slapd.d
  - ./volumes/certs:/container/service/slapd/assets/certs
```
we mount the subfolders  in `./volumes` for the openLdap database, config, and certificates.

If the volume folders are empty, we can initialise them by starting the container and importing the [my-org.ldif](my-org.ldif) file from the php gui. 
Once the database has been initialised, it will be persisted in the host folder, so that, after container restart, data will be persisted.

### Login to PHP GUI

Once started we can login to http://localhost:8080 

using the user: `cn=admin,dc=springframework,dc=org`
and password: `<LDAP_ADMIN_PASSWORD>`

### Import DIT from ldif file
In the gui select the `Import` icon and past the content of the [my-org.ldif](my-org.ldif)
selecting the ignore errors option. The created DIT will be persisted in the volumes, 
therefore this is a one time operation.

### Command example - simple search

In the command, `ldap-openldap-1` is the container name from the compose file.

```bash
docker exec ldap-openldap-1 ldapsearch -x -H ldap://localhost -D "uid=bob,ou=people,dc=springframework,dc=org" -b uid=bob,ou=people,dc=springframework,dc=org -w bobspassword
```bash
$ docker exec ldap-openldap-1 ldapsearch -x -H ldap://localhost -b uid=bob,ou=people,dc=springframework,dc=org -D "cn=admin,dc=springframework,dc=org" -w password

...
# bob, people, springframework.org
dn: uid=bob,ou=people,dc=springframework,dc=org
objectClass: top
objectClass: person
objectClass: organizationalPerson
objectClass: inetOrgPerson
cn: Bob Hamilton
sn: Hamilton
uid: bob
userPassword:: Ym9ic3Bhc3N3b3Jk
```

### Command example - simple bind authentication
```bash
$ docker exec ldap-openldap-1 ldapsearch -x -H ldap://localhost -b uid=bob,ou=people,dc=springframework,dc=org -D "cn=admin,dc=springframework,dc=org" -w password

...
# bob, people, springframework.org
dn: uid=bob,ou=people,dc=springframework,dc=org
objectClass: top
objectClass: person
objectClass: organizationalPerson
objectClass: inetOrgPerson
cn: Bob Hamilton
sn: Hamilton
uid: bob
userPassword:: Ym9ic3Bhc3N3b3Jk
```

## Spring LDAP integration

Repo: [https://github.com/apedano/ldap-security-demo](https://github.com/apedano/ldap-security-demo)

With LDAP we can implement both authentication and authorization.
Sometimes those two functions are separated: user credentials are authenticated against LDAP server, 
while authorization can be handled via database (e.g. a `UserRepository`).

### Authentication

LDAP authentication in Spring Security can be roughly divided into the following stages.

* Obtaining the unique LDAP "Distinguished Name", or DN, from the login name. This will often mean performing a search in the directory, unless the exact mapping of usernames to DNs is known in advance. So a user might enter the name "joe" when logging in, but the actual name used to authenticate to LDAP will be the full DN, such as uid=joe,ou=users,dc=spring,dc=io.
* Authenticating the user, either by "binding" as that user or by performing a remote "compare" operation of the user’s password against the password attribute in the directory entry for the DN.
* Loading the list of authorities for the user.



We will look at some configuration scenarios below. For full information on available configuration options, please consult the security namespace schema (information from which should be available in your XML editor).

Spring Security’s LDAP support does not use the `UserDetailsService` because LDAP _bind authentication does not let clients read the password or even a hashed version of the password_.

For this reason, LDAP support is implemented through the LdapAuthenticator interface. The LdapAuthenticator interface is also responsible for retrieving any required user attributes. This is because the permissions on the attributes may depend on the type of authentication being used. For example, if binding as the user, it may be necessary to read the attributes with the user’s own permissions.

Spring Security supplies two `LdapAuthenticator` implementations:

#### Bind authentication

[Bind Authentication](https://ldap.com/the-ldap-bind-operation/) is the most common mechanism for authenticating users with LDAP. 
In bind authentication, _the user’s credentials_ (username and password) _are submitted to the LDAP server, which authenticates them_. 
The advantage to using bind authentication is that the **user’s secrets (the password) do not need to be exposed to clients**, which helps to protect them from leaking.

```java
@Bean
AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
	LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
	factory.setUserSearchFilter("(uid={0})");
	factory.setUserSearchBase("ou=people");
	return factory.createAuthenticationManager();
}
```

#### Password authentication
Password comparison is when the password supplied by the user is compared with the one stored in the repository. 
This can either be done by retrieving the value of the password attribute and checking it locally or by performing an LDAP “compare” operation, where the supplied password is passed to the server for comparison and **the real password value is never retrieved**. 
An LDAP compare cannot be done when the password is properly hashed with a random salt.

```java
@Bean
AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
  LdapPasswordComparisonAuthenticationManagerFactory factory = new LdapPasswordComparisonAuthenticationManagerFactory(
  contextSource, new BCryptPasswordEncoder());
  factory.setUserDnPatterns("uid={0},ou=people");
  factory.setPasswordAttribute("pwd");  
  return factory.createAuthenticationManager();
}
```

### Authorization

Spring Security’s `LdapAuthoritiesPopulator` is used to determine what authorities are returned for the user. 
The following example shows how configure LdapAuthoritiesPopulator

```java
@Bean
LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource) {
  String groupSearchBase = "";
  DefaultLdapAuthoritiesPopulator authorities =
  new DefaultLdapAuthoritiesPopulator(contextSource, groupSearchBase);
  authorities.setGroupSearchFilter("member={0}");
  return authorities;
}
```

```java
@Bean
AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource, LdapAuthoritiesPopulator authorities) {
  LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
  factory.setUserDnPatterns("uid={0},ou=people");
  factory.setLdapAuthoritiesPopulator(authorities);
  return factory.createAuthenticationManager();
}
```

https://medium.com/@im_gpd/ldap-and-jwt-authentication-in-spring-boot-a-comprehensive-guide-a0e6202655f

https://www.tutorialspoint.com/how-to-implement-simple-authentication-in-spring-boot
https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/ldap.html

Explains both authentication and authorization
https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/ldap.html


LDAP repositories for role extraction
https://docs.spring.io/spring-data/ldap/reference/repositories/introduction.html