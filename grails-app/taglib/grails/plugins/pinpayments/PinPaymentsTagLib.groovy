package grails.plugins.pinpayments

class PinPaymentsTagLib {
  static namespace = 'spreedly'

  def pinPaymentsService

  def subscribeLink = { attrs, body ->
    if (!attrs.subscriber) {
      throwTagError("Tag [subscribeLink] must have a [subscriber] attribute that is a subscriber id.")
    }
    if (!attrs.plan) {
      throwTagError("Tag [subscribeLink] must have a [plan] attribute that is a valid plan id.")
    }
    String siteName = attrs.siteName ?: pinPaymentsService.SITE_NAME
    out << "https://subs.pinpayments.com/${siteName}/subscribers/${attrs.subscriber}"
    if (attrs.token) out << "/${attrs.token}"
    out << "/subscribe/${attrs.plan}"
    if (attrs.name) out << "/${attrs.name.encodeAsURL()}"
    if (attrs.returnUrl) out << "?return_url=${attrs.returnUrl.encodeAsURL()}"
  }

  def subscriberAccountLink = { attrs, body ->
    if (!attrs.token) {
      throwTagError("Tag [subscriberAccountLink] must have a [token] attribute that is a spreedly subscriber token.")
    }
    String siteName = attrs.siteName ?: pinPaymentsService.SITE_NAME
    out << "https://subs.pinpayments.com/${siteName}/subscriber_accounts/${attrs.token}"
    if (attrs.returnUrl) out << "?return_url=${attrs.returnUrl.encodeAsURL()}"
  }
}
