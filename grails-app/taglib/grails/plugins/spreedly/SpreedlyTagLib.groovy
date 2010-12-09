package grails.plugins.spreedly

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class SpreedlyTagLib {
  static namespace = 'spreedly'

  def subscribeLink = { attrs, body ->
    if (!attrs.subscriber) {
      throwTagError("Tag [subscribeLink] must have a [subscriber] attribute that is a subscriber id.")
    }
    if (!attrs.plan) {
      throwTagError("Tag [subscribeLink] must have a [plan] attribute that is a valid plan id.")
    }
    out << "https://spreedly.com/${CH.config.spreedly.siteName}/subscribers/${attrs.subscriber}"
    if (attrs.token) out << "/${attrs.token}"
    out << "/subscribe/${attrs.plan}"
    if (attrs.name) out << "/${attrs.name.encodeAsURL()}"
    if (attrs.returnUrl) out << "?return_url=${attrs.returnUrl.encodeAsURL()}"
  }

  def subscriberAccountLink = { attrs, body ->
    if (!attrs.token) {
      throwTagError("Tag [subscriberAccountLink] must have a [token] attribute that is a spreedly subscriber token.")
    }
    out << "https://spreedly.com/${CH.config.spreedly.siteName}/subscriber_accounts/${attrs.token}"
    if (attrs.returnUrl) out << "?return_url=${attrs.returnUrl.encodeAsURL()}"
  }
}
