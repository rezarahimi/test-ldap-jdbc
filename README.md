Test Ldap JDBC
==============
This is a test sample to use JDBC LDAP Driver based on following documents:

https://www.novell.com/developer/ndk/ldap_jdbc_driver.html

The testing this application needs a running docker for LDAP as follows:
```
docker run --name tmp-ldap-server -h ldapsrv.weave.local -p 10389:389 -e LDAP_DOMAIN=example.com -e LDAP_ORGANIZATION=com -d mcreations/fusiondirectory-ldap
```