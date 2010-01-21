package grails.plugins.spreedly

import grails.test.*
import groovy.xml.StreamingMarkupBuilder

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

    void testFindSubscriptionPlan() {
        def service = new SpreedlyService()
        def plan = service.findSubscriptionPlan(3765)
        assertNotNull plan
    }

    void testFindSubscriptionPlanByName() {
        def service = new SpreedlyService()
        def plan = service.findSubscriptionPlanByName('Example Plan')
        assertNotNull plan
    }

    void testDeleteAllSubscribers() {
        def service = new SpreedlyService()
        assertTrue service.deleteAllSubscribers()
    }

    void testCreateSubscriberXML() {
        def customerId = new Date().time
        def screenName = ''
        def email = ''
        def xml = new StreamingMarkupBuilder().bind {
            subscriber {
                'customer-id' customerId
                if (screenName) {
                    'screen-name' screenName
                }
                if (email) {
                    'email' email
                }
            }
        }.toString()
        assertNotNull xml
    }
    void testCreateSubscriber() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def subscriber = service.createSubscriber(new Date().time)
        assertNotNull subscriber
        assertEquals customerId, subscriber.'customer-id'.text().toLong()
    }
}
