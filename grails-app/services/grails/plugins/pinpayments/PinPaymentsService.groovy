package grails.plugins.pinpayments

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.XML
import org.apache.http.params.HttpParams
import org.apache.http.params.HttpConnectionParams

class PinPaymentsService {

  boolean transactional = false

  String SITE_NAME = CH.config.spreedly?.siteName ?: 'yourSiteName'
  String AUTH_TOKEN = CH.config.spreedly?.authToken ?: 'yourAuthToken'

  private RESTClient getRESTClient(String siteName, String authToken) {
    def http = new RESTClient("https://subs.pinpayments.com/api/v4/${siteName}/")
    HttpParams httpParams = http.client.params
    HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
    HttpConnectionParams.setSoTimeout(httpParams, 30000);
    http.auth.basic authToken, ''
    http.handler.failure = { resp ->
      log.error "Error calling spreedly : ${resp.statusLine}"
      throw new Exception("Error calling spreedly : ${resp.statusLine}")
    }
    http
  }

  /**
   *  Reference : http://subs.pinpayments.com/manual/integration-reference/programatically-creating-a-subscriber/
   */
  def createSubscriber(long _customerId, String _email = '', String _screenName = '', String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.post(
        path: 'subscribers.xml',
        requestContentType: XML,
        body: {
          subscriber {
            'customer-id' _customerId
            if (_screenName) {
              'screen-name' _screenName
            }
            if (_email) {
              email _email
            }
          }
        }
    )
    resp.data
  }

  /**
   *  Test Api - Only works for test sites
   *
   *  Reference : http://subs.pinpayments.com/manual/integration-reference/deleting-one-test-subscriber/
   */
  boolean deleteSubscriber(long customerId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.delete(path: "subscribers/${customerId}.xml", contentType: TEXT)
    resp.status == 200
  }

  /**
   *  Test Api - Only works for test sites
   *
   *  Reference : http://subs.pinpayments.com/manual/integration-reference/deleting-all-test-subscribers/
   */
  boolean deleteAllSubscribers(String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.delete(path: 'subscribers.xml', contentType: TEXT)
    resp.status == 200
  }

  /**
   *  Reference : http://subs.pinpayments.com/manual/integration-guide/get-subscriber-info-from-spreedly/
   */
  def findSubscriber(long customerId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'404' = { resp ->
      // 404 means there is no subscriber with the given id, just return null later on
      resp
    }
    def resp = http.get(path: "subscribers/${customerId}.xml")
    resp.status == 200 ? resp.data : null
  }

  /**
   *  Reference : http://subs.pinpayments.com/manual/integration-guide/get-subscriber-info-from-spreedly/
   */
  def findAllSubscribers(String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.get(path: "subscribers.xml")
    resp.data
  }

  /**
   *  Reference : http://subs.pinpayments.com/manual/integration-reference/update-subscriber/
   */
  boolean updateSubscriber(long customerId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN, Map args) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.put(
        path: "subscribers/${customerId}.xml",
        contentType: TEXT,
        requestContentType: XML,
        body: {
          subscriber {
            args.each { key, value ->
              invokeMethod(key, [value])
            }
          }
        }
    )
    resp.status == 200
  }

  /*
  *   Subscription API
  *
  *   Sample xml returned:
  *
  *   <?xml version="1.0" encoding="UTF-8"?>
  *   <subscription-plans type="array">
  *     <subscription-plan>
  *       <amount type="decimal">24.0</amount>
  *       <charge-after-first-period type="boolean">false</charge-after-first-period>
  *       <charge-later-duration-quantity type="integer" nil="true"></charge-later-duration-quantity>
  *       <charge-later-duration-units nil="true"></charge-later-duration-units>
  *       <created-at type="datetime">2010-01-19T10:39:48Z</created-at>
  *       <currency-code>USD</currency-code>
  *       <description nil="true"></description>
  *       <duration-quantity type="integer">3</duration-quantity>
  *       <duration-units>months</duration-units>
  *       <enabled type="boolean">true</enabled>
  *       <feature-level>example</feature-level>
  *       <force-recurring type="boolean">false</force-recurring>
  *       <id type="integer">3765</id>
  *       <name>Example Plan</name>
  *       <needs-to-be-renewed type="boolean">true</needs-to-be-renewed>
  *       <plan-type>regular</plan-type>
  *       <return-url>http://subs.pinpayments.com/sample-return</return-url>
  *       <updated-at type="datetime">2010-01-19T10:39:48Z</updated-at>
  *       <terms type="string">3 months</terms>
  *       <price type="decimal">24.0</price>
  *     </subscription-plan>
  *   </subscription-plans>
  *
  *   Reference : http://subs.pinpayments.com/manual/integration-reference/programatically-pulling-subscription-plans/
  */

  def findAllSubscriptionPlans(String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.get(path: 'subscription_plans.xml')
    resp.data.'subscription-plan'
  }

  /**
   *  Subscription API
   *
   *  Convenience method
   */
  def findSubscriptionPlan(long subscriptionId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def plans = findAllSubscriptionPlans(siteName, authToken)
    plans.find { it.id.text().toLong() == subscriptionId }
  }

  /**
   *  Subscription API
   *
   *  Convenience method
   */
  def findSubscriptionPlanByName(String name, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def plans = findAllSubscriptionPlans(siteName, authToken)
    plans.find { it.name.text() == name }
  }

  /**
   *   Reference : http://subs.pinpayments.com/manual/integration-reference/programatically-comping-a-subscriber/
   */
  def giveComplimentarySubscription(long customerId, int quantity, String units, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'403' = {
      throw new Exception("An active subscriber cannot receive a complementary subscription")
    }
    http.handler.'404' = {
      throw new Exception("Unknown subscriber")
    }
    http.handler.'422' = {
      throw new Exception("Invalid format")
    }
    def resp = http.post(
        path: "subscribers/${customerId}/complimentary_subscriptions.xml",
        requestContentType: XML,
        body: {
          complimentary_subscription {
            duration_quantity quantity
            duration_units units
            feature_level 'Pro'
          }
        }
    )
    resp.status == 201 ? resp.data : null
  }

  /**
   *   Reference : http://subs.pinpayments.com/manual/integration-reference/programatically-comping-subscriber-time-extension/
   */
  def giveComplimentaryTimeExtension(long customerId, int quantity, String units, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'403' = {
      throw new Exception("An inactive subscriber cannot receive a complementary time extension")
    }
    http.handler.'404' = {
      throw new Exception("Unknown subscriber")
    }
    http.handler.'422' = {
      throw new Exception("Invalid format")
    }
    def resp = http.post(
        path: "subscribers/${customerId}/complimentary_time_extensions.xml",
        requestContentType: XML,
        body: {
          complimentary_time_extension {
            duration_quantity quantity
            duration_units units
          }
        }
    )
    resp.status == 201 ? resp.data : null
  }

  /**
   *   Reference : http://subs.pinpayments.com/manual/integration-reference/adding-lifetime-comp-to-a-subscriber/
   */
  def giveLifetimeComplimentarySubscription(long customerId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'404' = {
      throw new Exception("Unknown subscriber")
    }
    http.handler.'422' = {
      throw new Exception("Invalid format")
    }
    def resp = http.post(
        path: "subscribers/${customerId}/lifetime_complimentary_subscriptions.xml",
        requestContentType: XML,
        body: {
          lifetime_complimentary_subscription {
            feature_level 'Pro'
          }
        }
    )
    resp.status == 201 ? resp.data : null
  }

  /**
   * Reference : http://subs.pinpayments.com/manual/integration-reference/programatically-stop-auto-renew/
   */
  boolean stopAutoRenew(long customerId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.post(path: "subscribers/${customerId}/stop_auto_renew.xml", contentType: TEXT)
    resp.status == 200
  }

  /**
   *   Reference : http://subs.pinpayments.com/manual/integration-reference/programatically-subscribing-to-free-trial/
   */
  def subscribeToFreeTrial(long customerId, long subscriptionId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'403' = {
      throw new Exception("Invalid state, check that the subscriber is eligible and that the plan is a free plan")
    }
    http.handler.'404' = {
      throw new Exception("Unknown subscriber")
    }
    http.handler.'422' = {
      throw new Exception("Invalid format")
    }
    def resp = http.post(
        path: "subscribers/${customerId}/subscribe_to_free_trial.xml",
        requestContentType: XML,
        body: {
          subscription_plan {
            id subscriptionId
          }
        }
    )
    resp.status == 200 ? resp.data : null
  }

  /**
   *  Payment API
   *
   *   Reference : http://subs.pinpayments.com/manual/integration-reference/payments-api/create-invoice/
   */
  def createInvoice(long subscriptionId, long customerId, String screenName = '', String _email = '', String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'403' = {
      throw new Exception("Subscription plan is disabled")
    }
    http.handler.'404' = {
      throw new Exception("Unknown subscription plan")
    }
    http.handler.'422' = {
      throw new Exception("Invalid format")
    }
    def resp = http.post(
        path: "invoices.xml",
        requestContentType: XML,
        body: {
          invoice {
            subscription_plan_id subscriptionId
            subscriber {
              customer_id customerId
              if (screenName) {
                screen_name screenName
              }
              if (_email) {
                email _email
              }
            }
          }
        }
    )
    resp.status == 201 ? resp.data : null
  }

  /**
   *  Payment API
   *
   *  Reference : http://subs.pinpayments.com/manual/integration-reference/payments-api/pay-invoice/
   */
  def payInvoice(String invoiceToken, String cardNumber, String cardType, String verificationValue,
                 String _month, String _year, String firstName, String lastName, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'403' = {
      throw new Exception("Subscription plan is disabled")
    }
    http.handler.'404' = {
      throw new Exception("Unknown subscription plan")
    }
    http.handler.'422' = {
      throw new Exception("Invalid format")
    }
    def resp = http.put(
        path: "invoices/${invoiceToken}/pay.xml",
        requestContentType: XML,
        body: {
          payment {
            credit_card {
              number cardNumber
              card_type cardType
              verification_value verificationValue
              month _month
              year _year
              first_name firstName
              last_name lastName
            }
          }
        }
    )
    resp.status == 200 ? resp.data : null
  }

  /**
   * Adding Store Credit to a Subscriber
   * http://subs.pinpayments.com/manual/integration-reference/adding-store-credit-to-a-subscriber
   *
   */
  boolean addStoreCredit(long customerId, String _amount, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    http.handler.'404' = {
      throw new Exception("Unknown subscriber")
    }
    http.handler.'422' = {
      throw new Exception("Invalid amount : ${_amount}")
    }
    def resp = http.post(
        path: "subscribers/${customerId}/credits.xml",
        contentType: TEXT,
        requestContentType: XML,
        body: {
          credit {
            amount _amount
          }
        }
    )
    resp.status == 201
  }

  /**
   * Finds last 50 Transactions
   * http://subs.pinpayments.com/manual/integration-reference/show-transactions
   *
   */
  def findLastTransactions(String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.get(
        path:'transactions.xml',
        requestContentType: XML
    )
    resp.data
  }

  /**
   * Finds next 50 Transactions with id greater than transactionId
   * http://subs.pinpayments.com/manual/integration-reference/show-transactions
   *
   */
  def findTransactionsSince(long transactionId, String siteName = SITE_NAME, String authToken = AUTH_TOKEN) {
    def http = getRESTClient(siteName, authToken)
    def resp = http.get(
        path:'transactions.xml',
        query : [since_id : transactionId],
        requestContentType: XML
    )
    resp.data
  }
}
