jcodemodel
==========

A fork of the com.sun.codemodel 2.7-SNAPSHOT.
The classes in this project use a different package name `com.helger.jcodemodel` to avoid conflicts 
with other `com.sun.codemodel` instances that might be floating around in the classpath.
That of course implies, that this artefact cannot directly be used with JAXB, since the configuration of 
this would be very tricky.

A site with the links to the [API docs](http://phax.github.io/jcodemodel/) etc. is available.

News and noteworthy:

* 2014-09-17: Release 2.7.7 - mainly API extensions
* 2014-09-02: Release 2.7.6 - Extended annotation parameter handling API
* 2014-08-14: Release 2.7.5 - Support for multiple boundaries added (like `T extends AnyClass & Serializable`)
* 2014-06-12: Release 2.7.4 - Bugfix release
* 2014-05-23: Release 2.7.3 - Bugfix release
* 2014-05-21: Release 2.7.2 - now on Maven Central
* 2014-05-19: Release 2.7.1 - now as OSGi bundle
* 2014-05-16: Release 2.7.0 - API extensions
* 2014-04-10: Release 2.6.4
* 2013-09-23: Changes from https://github.com/UnquietCode/JCodeModel have been incorporated.

#Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>jcodemodel</artifactId>
  <version>2.7.7</version>
</dependency>
```

---

On Twitter: <a href="https://twitter.com/philiphelger">Follow @philiphelger</a>
