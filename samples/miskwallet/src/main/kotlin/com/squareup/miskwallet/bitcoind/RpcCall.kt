package com.squareup.miskwallet.bitcoind

import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.net.URL
import java.net.HttpURLConnection
import java.util.ArrayList



// import wf.bitcoin.javabitcoindrpcclient;

fun main(args: Array<String>) {
  val rpcuser = "user"
  val rpcpassword = "password"
  Authenticator.setDefault(object : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
      return PasswordAuthentication(rpcuser, rpcpassword.toCharArray())
    }
  })

  System.out.println(curl(
      "http://localhost:18443",
      """{"method":"getblockchaininfo","params":[],"id":1,"jsonrpc":"1.0"}"""
  ))

  foo()
}

fun foo() {
  val createRetrofit = createRetrofit()
  println("foooo ")
  println()
  val body = createRetrofit.getBlockChainInfo(JsonRpcRequest(
      "1.0",
      "blockchaininfoData",
      "getblockchaininfo",
      emptyList())).execute().body()

  print(body)
}

fun createRetrofit() : BitcoinD {

  val okHttpClient = OkHttpClient.Builder().authenticator { _, response ->
    val credential = Credentials.basic("user", "password")
    response.request().newBuilder()
        .header("Authorization", credential)
        .build()
  }.build()

//
//  val gson = GsonBuilder()
//      .registerTypeAdapterFactory(AutoValueAdapterFactory())
//      .create()

  val retrofit = Retrofit.Builder()
      .baseUrl("http://localhost:18443")
      // .addConverterFactory(GsonConverterFactory.create(gson))
      .addConverterFactory(GsonConverterFactory.create())
      .client(okHttpClient)
      .build()

  return retrofit.create(BitcoinD::class.java)
}

interface BitcoinD {

  @POST("/")
  fun getBlockChainInfo(@Body jsonRpcRequest: JsonRpcRequest): Call<BlockChainInfo>
}


fun curl(url:String, jsonEncodedString:String): String {
  val httpcon = URL(url).openConnection() as HttpURLConnection
  httpcon.setDoOutput(true);
  // httpcon.setRequestProperty("Authorization", "Basic X19jb29raWVfXzo0NmQ0ZWJmNDExZTAxODNkZjhlZmM2MDU4NGM5MTMxYzk5N2FiMzc5MGM2MzhhODE5MDIyNzVlYTI2MDE0ZDFi");
  httpcon.setRequestProperty("Content-Type", "application/json")
  httpcon.setRequestProperty("Accept", "application/json")
  httpcon.setRequestMethod("POST")
  httpcon.connect()

  val outputBytes = jsonEncodedString.toByteArray();

  httpcon.getOutputStream().use {
    it.write(outputBytes)
  }
  val code = httpcon.getResponseCode()
  val isError = code in 400..500
  val text = (if (isError) httpcon.getErrorStream() else httpcon.getInputStream())
      ?.bufferedReader()?.use {
        it.readText()
      } ?: "no connection"
  if (isError) throw Exception(
      "Resp code $code. Error: ${text.take(200)}"
  )
  return text
}

class JsonRpcRequest(
  internal var jsonrpc: String,
  internal var id: String,
  internal var method: String,
  params: List<Any>
) {
  var params: List<Any> = ArrayList()

  init {
    this.params = params
  }
}