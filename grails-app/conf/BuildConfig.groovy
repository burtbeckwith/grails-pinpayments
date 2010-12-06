grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'

    // Workaround to deal with http-builder not loading nekohtml
    runtime ('xerces:xercesImpl:2.9.1'){
      excludes "xml-apis"
    }
    runtime ('net.sourceforge.nekohtml:nekohtml:1.9.14'){
      excludes "xercesImpl"
    }
    // End of workaround

    compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.1') {
      excludes 'xercesImpl', 'groovy', 'nekohtml'
    }
    runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.1') {
      excludes 'xercesImpl', 'groovy', 'nekohtml'
    }
    }

}
