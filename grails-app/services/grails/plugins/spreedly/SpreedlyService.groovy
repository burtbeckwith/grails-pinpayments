package grails.plugins.spreedly

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.XML

class SpreedlyService {

    boolean transactional = false

    String siteName = CH.config.spreedly.siteName
    String authToken = CH.config.spreedly.authToken

    RESTClient getRESTClient() {
        def http = new RESTClient("https://spreedly.com/api/v4/${siteName}/")
        http.auth.basic authToken, ''
        http.handler.failure = { resp ->
            def msg = "Error calling spreedly : ${resp.statusLine}"
            log.error(msg)
            throw new Exception(msg)
        }
        http
    }

    /**
     *  Reference : http://spreedly.com/manual/integration-reference/programatically-creating-a-subscriber/
     */
    def createSubscriber(long _customerId, String _email = '', String _screenName = '') {
        def http = getRESTClient()
        def resp = http.post(
            path:'subscribers.xml',
            requestContentType:XML,
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

    boolean deleteSubscriber(long customerId) {
        def http = getRESTClient()
        def resp = http.delete(path:"subscribers/${customerId}.xml", contentType:TEXT)
        resp.status == 200
    }

    boolean deleteAllSubscribers() {
        def http = getRESTClient()
        def resp = http.delete(path:'subscribers.xml', contentType:TEXT)
        resp.status == 200
    }

    /**
     * Reference : http://spreedly.com/manual/integration-guide/get-subscriber-info-from-spreedly/
     */
    def findSubscriber(long customerId) {
        def http = getRESTClient()
        def resp = http.get(path:"subscribers/${customerId}.xml")
        resp.data
    }

    def findAllSubscribers() {
        def http = getRESTClient()
        def resp = http.get(path:"subscribers.xml")
        resp.data
    }

    /**
     * Reference : http://spreedly.com/manual/integration-reference/update-subscriber/
     */
    boolean updateSubscriber(long customerId, Map args) {
        def http = getRESTClient()
        def resp = http.put(
            path:"subscribers/${customerId}.xml",
            contentType:TEXT,
            requestContentType:XML,
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
    *
    *  Sample xml returned:
    *
<?xml version="1.0" encoding="UTF-8"?>
<subscription-plans type="array">
  <subscription-plan>
    <amount type="decimal">24.0</amount>
    <charge-after-first-period type="boolean">false</charge-after-first-period>
    <charge-later-duration-quantity type="integer" nil="true"></charge-later-duration-quantity>
    <charge-later-duration-units nil="true"></charge-later-duration-units>
    <created-at type="datetime">2010-01-19T10:39:48Z</created-at>
    <currency-code>USD</currency-code>
    <description nil="true"></description>
    <duration-quantity type="integer">3</duration-quantity>
    <duration-units>months</duration-units>
    <enabled type="boolean">true</enabled>
    <feature-level>example</feature-level>
    <force-recurring type="boolean">false</force-recurring>
    <id type="integer">3765</id>
    <name>Example Plan</name>
    <needs-to-be-renewed type="boolean">true</needs-to-be-renewed>
    <plan-type>regular</plan-type>
    <return-url>http://spreedly.com/sample-return</return-url>
    <updated-at type="datetime">2010-01-19T10:39:48Z</updated-at>
    <terms type="string">3 months</terms>
    <price type="decimal">24.0</price>
  </subscription-plan>
</subscription-plans>
    */
    def findAllSubscriptionPlans() {
        def http = getRESTClient()
        def resp = http.get(path:'subscription_plans.xml')
        resp.data."subscription-plan"
    }

    def findSubscriptionPlan(long subscriptionId) {
        def plans = findAllSubscriptionPlans()
        plans.find { it.id.text().toLong() == subscriptionId }
    }

    def findSubscriptionPlanByName(String name) {
        def plans = findAllSubscriptionPlans()
        plans.find { it.name.text() == name }
    }

    /**
    *   Reference : http://spreedly.com/manual/integration-reference/programatically-comping-a-subscriber/
    */
    def giveComplimentarySubscription(long customerId, int quantity, String units) {
        def http = getRESTClient()
        def resp = http.post(
            path:"subscribers/${customerId}/complimentary_subscriptions.xml",
            requestContentType:XML,
            body: {
                complimentary_subscription {
                    duration_quantity quantity
                    duration_units units
                    feature_level 'Pro'
                }
            }
        )
        switch(resp.status) {
            case 201:
                // Success !
                return resp.data

            case 404:
                throw new Exception("Unknown subscriber")

            case 422:
                throw new Exception("Invalid format")

            case 403:
                throw new Exception("An active subscriber cannot receive a complementary subscription")
        }
    }

    /**
    *   Reference : http://spreedly.com/manual/integration-reference/programatically-comping-subscriber-time-extension/
    */
    def giveComplimentaryTimeExtension(long customerId, int quantity, String units) {
        def http = getRESTClient()
        def resp = http.post(
            path:"subscribers/${customerId}/complimentary_time_extensions.xml",
            requestContentType:XML,
            body: {
                complimentary_time_extension {
                    duration_quantity quantity
                    duration_units units
                }
            }
        )
        switch(resp.status) {
            case 201:
                // Success !
                return resp.data

            case 404:
                throw new Exception("Unknown subscriber")

            case 422:
                throw new Exception("Invalid format")

            case 403:
                throw new Exception("An inactive subscriber cannot receive a complementary time extension")
        }
    }

    def stopAutoRenew(long customerId) {

    }

    def activateFreeTrial(long customerId, long subscriptionId) {
        
    }
}
