package grails.plugins.spreedly

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import groovyx.net.http.RESTClient

class SpreedlyService {

    boolean transactional = false

    String siteName = CH.config.spreedly.siteName
    String authToken = CH.config.spreedly.authToken

    def createSubscriber(Long customerId, String email = '', String screenName = '') {

    }

    def deleteSubscriber(Long customerId) {

    }

    def findSubscriber(Long customerId) {

    }

    def deleteAllSubscribers() {
        
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
        def http = new RESTClient("https://spreedly.com/api/v4/${siteName}/")
        http.auth.basic(authToken, '')
        http.handler.failure = { resp ->
            def msg = "Error calling findAllSubscriptionPlans : ${resp.statusLine}"
            log.error(msg)
            throw new Exception(msg)
        }
        def resp = http.get (path:'subscription_plans.xml')
        resp.data."subscription-plan"
    }

    def findSubscriptionPlan(Long subscriptionId) {
        def plans = findAllSubscriptionPlans()
        plans.find { it.id.text().toLong() == subscriptionId }
    }

    def findSubscriptionPlanByName(String name) {
        def plans = findAllSubscriptionPlans()
        plans.find { it.name.text() == name }
    }

    def giveComplementarySubscription(Long customerId, String quantity, String units) {
        
    }

    def stopAutoRenew(Long customerId) {

    }

    def activateFreeTrial(Long customerId, Long subscriptionId) {
        
    }
}
