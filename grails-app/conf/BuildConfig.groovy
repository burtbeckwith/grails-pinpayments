grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.release.scm.enabled = false
grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  legacyResolve false
  repositories {
    grailsCentral()
    mavenCentral()
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

//        // Workaround to deal with http-builder not loading nekohtml
//        runtime ('xerces:xercesImpl:2.9.1'){
//          excludes "xml-apis"
//        }
//        runtime ('net.sourceforge.nekohtml:nekohtml:1.9.14'){
//          excludes "xercesImpl"
//        }
//        // End of workaround

    compile('org.codehaus.groovy.modules.http-builder:http-builder:0.6') {
      excludes 'xercesImpl', 'groovy', 'nekohtml'
    }
    runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.6') {
      excludes 'xercesImpl', 'groovy', 'nekohtml'
    }
  }
  plugins {
    build(":release:3.0.0") {
      export = false
    }
  }
}
