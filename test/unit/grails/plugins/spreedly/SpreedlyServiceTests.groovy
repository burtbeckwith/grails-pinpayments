package grails.plugins.spreedly

import grails.test.*

class SpreedlyServiceTests extends GrailsUnitTestCase {

    void setUp() {
        super.setUp()
        mockLogging(SpreedlyService, true)
        mockConfig('''
spreedly.siteName = 'grails-spreedly-test'
spreedly.authToken = '7970a60046d945f520fc9be915b71c86c7de4560'
''')
    }

    void testConfigOk() {
        def service = new SpreedlyService()
        assertNotNull service.siteName
        assertNotNull service.authToken
    }

    void testFindAllSubscriptionPlans() {
        def service = new SpreedlyService()
        def plans = service.findAllSubscriptionPlans()
        assertNotNull plans
    }
}
