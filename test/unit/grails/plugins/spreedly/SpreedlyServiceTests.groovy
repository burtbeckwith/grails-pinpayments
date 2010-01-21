package grails.plugins.spreedly

import grails.test.*
import groovy.xml.StreamingMarkupBuilder

class SpreedlyServiceTests extends GrailsUnitTestCase {

    void setUp() {
        super.setUp()
        mockLogging(SpreedlyService, true)
        mockConfig('''
spreedly.siteName = 'grails-spreedly-test'
spreedly.authToken = 'cefb1ace9595fb30d7e82777d64800ba9ad70cb5'
''')
    }

    void tearDown() {
        def service = new SpreedlyService()
        service.deleteAllSubscribers()
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
        def _customerId = new Date().time
        def _screenName = 'roger'
        def _email = 'roger@rabbit.com'
        def xml = new StreamingMarkupBuilder().bind {
            subscriber {
                'customer-id' _customerId
                if (_screenName) {
                    'screen-name' _screenName
                }
                if (_email) {
                    email _email
                }
            }
        }.toString()
        assertNotNull xml
        println xml
        assertEquals "<subscriber><customer-id>${_customerId}</customer-id><screen-name>${_screenName}</screen-name><email>${_email}</email></subscriber>", xml
    }
    void testCreateSubscriber() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def subscriber = service.createSubscriber(customerId)
        assertNotNull subscriber
        assertEquals customerId, subscriber.'customer-id'.text().toLong()
    }

    void testDeleteSubscriber() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        assertTrue service.deleteSubscriber(customerId)
    }

    void testFindAllSubscribers() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        service.createSubscriber(customerId + 1, 'danny@rabbit.com', 'danny')
        def subscribers = service.findAllSubscribers()
        assertNotNull subscribers
        assertEquals 2, subscribers.subscriber.size()
    }

    void testFindSubscriber() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        def subscriber = service.findSubscriber(customerId)
        assertNotNull subscriber
        assertEquals 'roger@rabbit.com', subscriber.email.text()
        assertEquals 'roger', subscriber.'screen-name'.text()
    }

    void testGiveComplimentarySubscription() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        assertFalse subscriber.active.text().toBoolean()
        subscriber = service.giveComplimentarySubscription(customerId, 2, 'months')
        assertNotNull subscriber
        assertTrue subscriber.active.text().toBoolean()
    }

    void testUpdateSubscriber() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def _subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        assertFalse _subscriber.active.text().toBoolean()
        assertTrue service.updateSubscriber(customerId, ['screen-name':'joe'])
    }

    void testGiveLifetimeComplimentarySubscription() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        subscriber = service.giveLifetimeComplimentarySubscription(customerId)
        assertNotNull subscriber
        assertTrue subscriber.'lifetime-subscription'.text().toBoolean()
    }

    void testStopAutoRenew() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        assertTrue service.stopAutoRenew(customerId)
    }

    void testSubscribeToFreeTrial() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
        subscriber = service.subscribeToFreeTrial(customerId, 3804)
        assertTrue subscriber.'on-trial'.text().toBoolean()
    }

    void testUnknownSubscriberSubscribeToFreeTrial() {
        def service = new SpreedlyService()
        long customerId = new Date().time
        def result = shouldFail(Exception.class) {
            service.subscribeToFreeTrial(customerId, 3804)
        }
        assertEquals 'Unknown subscriber', result
    }

//
//    void testGiveComplimentaryTimeExtension() {
//        def service = new SpreedlyService()
//        long customerId = new Date().time
//        def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
//        assertFalse subscriber.active.text().toBoolean()
//        subscriber = service.giveComplimentaryTimeExtension(customerId, 20, 'days')
//        assertNotNull subscriber
//        assertTrue subscriber.active.text().toBoolean()
//    }
}
