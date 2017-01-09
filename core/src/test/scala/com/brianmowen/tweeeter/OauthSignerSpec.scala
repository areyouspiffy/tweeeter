package com.brianmowen.tweeeter

import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import com.brianmowen.tweeeter.helpers.OauthSigner
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike}

class OauthSignerSpec extends WordSpecLike with Matchers  {
  "Signer" must {
    "create a valid oauth string" in {
      val settings = TweeeterSettings()
    }
    "create a valid oauth string from twitter example" in {
      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = "https://api.twitter.com/1/statuses/update.json?include_entities=true",
        entity = "Hello Ladies + Gentlemen, a signed OAuth request!"
      )
      val settings = TweeeterSettings(ConfigFactory.parseString(
        """
          |api.twitter {
          |    auth {
          |      consumer-key = "xvz1evFS4wEEPTGEFPHBog"
          |      consumer-secret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw"
          |      token = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb"
          |      token-secret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"
          |    }
          |    endpoints {
          |      auth = ""
          |      public-stream = ""
          |    }
          |  }
        """.stripMargin))

      val signer = new OauthSigner(consumerKey = settings.consumerKey,
        consumerSecret = settings.consumerSecret,
        token = settings.token,
        tokenSecret = settings.tokenSecret)

      // Param

      val appParamString = signer.signableParamterString(signer.oauthParamteters(request) ++ signer.signingParameters(request))
      val twitterParamString = "include_entities=true&oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1318622958&oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&oauth_version=1.0&status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21"
      appParamString shouldEqual twitterParamString

      // Base

      val sigBaseString = signer.baseString(request, appParamString)
      val twitterBaseString = "POST&https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521"
      sigBaseString shouldEqual twitterBaseString

      // Signed
      val appSigningKey = signer.key
      val twitterSigningKey = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"
       appSigningKey shouldEqual twitterSigningKey

      val appSig = signer.genSHASignature(sigBaseString, appSigningKey)
      val twitterSig = "tnnArxj06cWHq44gCs1OSKk/jLY="
      appSig shouldEqual twitterSig

      signer.genSHASignature(
        sigBaseString,
        appSigningKey
      ) shouldEqual "tnnArxj06cWHq44gCs1OSKk/jLY="

      val twitterHeaderString = """OAuth oauth_consumer_key="xvz1evFS4wEEPTGEFPHBog", oauth_nonce="kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg", oauth_signature="tnnArxj06cWHq44gCs1OSKk%2FjLY%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1318622958", oauth_token="370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb", oauth_version="1.0""""
      val appHeaderString = signer.genHeader(request)
      appHeaderString shouldEqual twitterHeaderString


    }
  }
}
