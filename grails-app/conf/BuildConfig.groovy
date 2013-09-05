grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

  inherits 'global'
  log 'warn'

  repositories {
    grailsCentral()
    mavenLocal()
    mavenCentral()
  }

  dependencies {
//    // Workaround to deal with http-builder not loading nekohtml
//    runtime ('xerces:xercesImpl:2.9.1'){
//      excludes "xml-apis"
//    }
//    runtime ('net.sourceforge.nekohtml:nekohtml:1.9.14'){
//      excludes "xercesImpl"
//    }
//    // End of workaround

    compile('org.codehaus.groovy.modules.http-builder:http-builder:0.6') {
      excludes 'xercesImpl', 'groovy', 'nekohtml'
    }

    runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.6') {
      excludes 'xercesImpl', 'groovy', 'nekohtml'
    }
  }

  plugins {
    build ':release:2.2.1', ':rest-client-builder:1.0.3', {
      export = false
    }
  }
}
