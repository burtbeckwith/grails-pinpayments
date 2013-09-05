package grails.plugins.pinpayments

import grails.test.GrailsUnitTestCase
import groovy.xml.StreamingMarkupBuilder

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication

class PinPaymentsServiceTests extends GrailsUnitTestCase {

  private PinPaymentsService service = new PinPaymentsService()

  protected void setUp() {
    super.setUp()
    mockLogging(PinPaymentsService, true)
    def config = mockConfig('''
spreedly.siteName = 'grails-spreedly-test'
spreedly.authToken = 'cefb1ace9595fb30d7e82777d64800ba9ad70cb5'
''')
    service.grailsApplication = new DefaultGrailsApplication()
    service.grailsApplication.config = config
    service.afterPropertiesSet()
  }

  protected void tearDown() {
    service.deleteAllSubscribers()
  }

  void testConfigOk() {
    assertNotNull service.SITE_NAME
    assertNotNull service.AUTH_TOKEN
  }

  void testFindAllSubscriptionPlans() {
    def plans = service.findAllSubscriptionPlans()
    assertNotNull plans
  }

  void testFindSubscriptionPlan() {
    def plan = service.findSubscriptionPlan(3765)
    assertNotNull plan
  }

  void testFindSubscriptionPlanByName() {
    def plan = service.findSubscriptionPlanByName('Example Plan')
    assertNotNull plan
  }

  void testDeleteAllSubscribers() {
    assertTrue service.deleteAllSubscribers()
  }

  void testCreateSubscriberXML() {
    def _customerId = System.currentTimeMillis()
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
    String expected = "<subscriber><customer-id>${_customerId}</customer-id><screen-name>${_screenName}</screen-name><email>${_email}</email></subscriber>"
    assertEquals expected, xml
  }

  void testCreateSubscriber() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId)
    assertNotNull subscriber
    assertEquals customerId, subscriber.'customer-id'.text().toLong()
  }

  void testDeleteSubscriber() {
    long customerId = System.currentTimeMillis()
    service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    assertTrue service.deleteSubscriber(customerId)
  }

  void testFindAllSubscribers() {
    long customerId = System.currentTimeMillis()
    service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    service.createSubscriber(customerId + 1, 'danny@rabbit.com', 'danny')
    def subscribers = service.findAllSubscribers()
    assertNotNull subscribers
    assertEquals 2, subscribers.subscriber.size()
  }

  void testFindSubscriber() {
    long customerId = System.currentTimeMillis()
    service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    def subscriber = service.findSubscriber(customerId)
    assertNotNull subscriber
    assertEquals 'roger@rabbit.com', subscriber.email.text()
    assertEquals 'roger', subscriber.'screen-name'.text()
  }

  void testGiveComplimentarySubscription() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    assertFalse subscriber.active.text().toBoolean()
    subscriber = service.giveComplimentarySubscription(customerId, 2, 'months')
    assertNotNull subscriber
    assertTrue subscriber.active.text().toBoolean()
  }

  void testUpdateSubscriber() {
    long customerId = System.currentTimeMillis()
    def _subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    assertFalse _subscriber.active.text().toBoolean()
    assertTrue service.updateSubscriber(customerId, ['screen-name': 'joe'])
  }

  void testGiveLifetimeComplimentarySubscription() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    subscriber = service.giveLifetimeComplimentarySubscription(customerId)
    assertNotNull subscriber
    assertTrue subscriber.'lifetime-subscription'.text().toBoolean()
  }

  void testStopAutoRenew() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    assertTrue service.stopAutoRenew(customerId)
  }

  void testSubscribeToFreeTrial() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    subscriber = service.subscribeToFreeTrial(customerId, 3804)
    assertTrue subscriber.'on-trial'.text().toBoolean()
  }

  void testUnknownSubscriberSubscribeToFreeTrial() {
    long customerId = System.currentTimeMillis()
    def result = shouldFail(Exception) {
      service.subscribeToFreeTrial(customerId, 3804)
    }
    assertEquals 'Unknown subscriber', result
  }

  void testCreateInvoice() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    def invoice = service.createInvoice(3765, customerId)
    assertNotNull invoice
  }

  void testPayInvoice() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    assertNotNull subscriber
    def invoice = service.createInvoice(3765, customerId)
    assertNotNull invoice
    def token = invoice.token.text()
    Calendar expCal = Calendar.instance
    expCal.add(Calendar.MONTH, 3)
    invoice = service.payInvoice(token, '4222222222222', 'visa', '123', expCal.get(Calendar.MONTH).toString().padLeft(2, '0'), expCal.get(Calendar.YEAR).toString(), 'Roger', 'Rabbit')
    assertNotNull invoice
    assertEquals token, invoice.token.text()
    assertTrue invoice.closed.text().toBoolean()
  }

//
//    void testGiveComplimentaryTimeExtension() {
//        long customerId = System.currentTimeMillis()
//        def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
//        assertFalse subscriber.active.text().toBoolean()
//        subscriber = service.giveComplimentaryTimeExtension(customerId, 20, 'days')
//        assertNotNull subscriber
//        assertTrue subscriber.active.text().toBoolean()
//    }

  void testFindLastTransactions() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'roger@rabbit.com', 'roger')
    subscriber = service.subscribeToFreeTrial(customerId, 3804)
    def transactions = service.findLastTransactions()
    assertFalse transactions.isEmpty()
    assertFalse transactions.transaction.isEmpty()
    assertEquals transactions.transaction.size(), 1
    assertEquals transactions.transaction.getAt(0)."subscriber-customer-id".text().toLong(), customerId
  }

  def testFindTransactionsSince() {
    long customerId = System.currentTimeMillis()
    def subscriber = service.createSubscriber(customerId, 'jessica@rabbit.com', 'jessica')
    subscriber = service.subscribeToFreeTrial(customerId, 3804)
    long customerId2 = System.currentTimeMillis()
    def subscriber2 = service.createSubscriber(customerId2, 'roger@rabbit.com', 'roger')
    subscriber2 = service.subscribeToFreeTrial(customerId2, 3804)
    def transactions = service.findLastTransactions()
    assertEquals transactions.transaction.size(), 2
    long lastId = transactions.transaction.getAt(1).id.text().toLong()
    transactions = service.findTransactionsSince(lastId)
    assertFalse transactions.isEmpty()
    assertFalse transactions.transaction.isEmpty()
    assertEquals transactions.transaction.size(), 1
    assertEquals transactions.transaction.getAt(0)."subscriber-customer-id".text().toLong(), customerId2
  }
}
