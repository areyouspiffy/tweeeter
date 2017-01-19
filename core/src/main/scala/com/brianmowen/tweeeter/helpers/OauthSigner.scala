package com.brianmowen.tweeeter.helpers

import java.net.URLEncoder
import javax.crypto
import javax.crypto.spec.SecretKeySpec

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.RawHeader

import scala.collection.immutable.TreeMap

object OauthSigner {
  protected object Implicits {
    implicit class SignerStringOps(val before: String) extends AnyVal {
      def &(after: String) = before + "&" + after
      def \(after: String) = before + "\"" + after
    }
  }
}

abstract class OauthSigner(consumerKey: String, consumerSecret: String, token: String, tokenSecret: String) {

  import OauthSigner.Implicits._

  val SHA1 = "HmacSHA1"
  val key  = consumerSecret + "&" + tokenSecret

  private[tweeeter] def encode(k: String) = URLEncoder.encode(k, "UTF-8").replace("+", "%20")
  private[tweeeter] def encodeMap(m: TreeMap[String, String]) = m.map { case (k,v) => encode(k) -> encode(v) }

  protected def nonce = System.nanoTime.toString
  protected def timestamp = (System.currentTimeMillis / 1000).toString

  def signRequest(request: HttpRequest): HttpRequest = request.addHeader(RawHeader("Authorization", genHeader(request)))

  def genHeader(request: HttpRequest) = {
    val ouathBaseString = oauthParamteters(request)
    val params          = signingParameters(request)
    val bs              = baseString(request, signableParamterString(ouathBaseString ++ params))
    val signature       = encode(genSHASignature(bs, key))
    "OAuth " + (ouathBaseString + ("oauth_signature" -> signature)).foldLeft("") {
      case (s, (k, v)) => s + k + "=" \ v \ ", "
    }.dropRight(2)
  }

  def oauthParamteters(request: HttpRequest): TreeMap[String, String] = {
    val baseurl = request.uri.copy(rawQueryString = None).toString()
    TreeMap(
      "oauth_consumer_key"     -> consumerKey,
      "oauth_nonce"            -> nonce,
      "oauth_signature_method" -> "HMAC-SHA1",
      "oauth_timestamp"        -> timestamp,
      "oauth_token"            -> token,
      "oauth_version"          -> "1.0"
    )
  }

  protected def bodyParams(request: HttpRequest): Map[String, String] = Map.empty

  def signingParameters(request: HttpRequest): TreeMap[String, String] =
    TreeMap[String,String]() ++ request.uri.query().toMap ++ bodyParams(request)

  def signableParamterString(s: TreeMap[String, String]): String = encodeMap(s).map { case (a, b) => a + "=" + b }.mkString("&")

  def baseString(request: HttpRequest, baseString: String) =
    request.method.value + "&" + encode(request.uri.copy(rawQueryString = None).toString()) & encode(baseString)

  def genSHASignature(s: String, signingKey: String): String = {
    val mac = crypto.Mac.getInstance(SHA1)
    mac.init(new SecretKeySpec(signingKey.getBytes, SHA1))
    val sigBytes = mac.doFinal(s.getBytes)
    val fin      = java.util.Base64.getEncoder.encodeToString(sigBytes)
    fin
  }
}

class OauthSignerImpl(consumerKey: String, consumerSecret: String, token: String, tokenSecret: String) extends OauthSigner(consumerKey, consumerSecret, token, tokenSecret) {
  override def nonce = System.nanoTime.toString
  override def timestamp = (System.currentTimeMillis / 1000).toString
}

