package grails.plugins.spreedly

class SpreedlyService {

    boolean transactional = false

    String siteName
    String authToken

    def createSubscriber(Long customerId, String email = '', String screenName = '') {

    }

    def deleteSubscriber(Long customerId) {

    }

    def findSubscriber(Long customerId) {

    }

    def deleteAllSubscribers() {
        
    }

    def findAllSubscriptionPlans() {

    }

    def findSubscriptionPlan(Long subscriptionId) {
        
    }

    def findSubscriptionPlanByName(String name) {
        
    }

    def giveComplementarySubscription(Long customerId, String quantity, String units) {
        
    }

    def stopAutoRenew(Long customerId) {

    }

    def activateFreeTrial(Long customerId, Long subscriptionId) {
        
    }
}
