package com.squareup.miskwallet.bitcoind

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.URL
import java.net.HttpURLConnection
import java.util.ArrayList



// import wf.bitcoin.javabitcoindrpcclient;

fun main(args: Array<String>) {
//  val rpcuser = "user"
//  val rpcpassword = "password"
//  Authenticator.setDefault(object : Authenticator() {
//    override fun getPasswordAuthentication(): PasswordAuthentication {
//      return PasswordAuthentication(rpcuser, rpcpassword.toCharArray())
//    }
//  })
//
//  System.out.println(curl(
//      "http://localhost:18443",
//      """{"method":"getblockchaininfo","params":[],"id":1,"jsonrpc":"1.0"}"""
//  ))

  foo()
}

fun foo() {
  val createRetrofit = createRetrofit()
  println("foooo ")
  println()
  val getBlockChainInfoResponse = createRetrofit.getBlockChainInfo(JsonRpcRequest(
      "1.0",
      "blockchaininfoData",
      "getblockchaininfo")).execute()
  val blockChainInfo : BlockChainInfo = getBlockChainInfoResponse.body()!!
  val bestHash = blockChainInfo.result!!.bestBlockHash
  println("Best Hash: ${bestHash}")

  val getBestBlockHashResponse = createRetrofit.getBestBlockHash(JsonRpcRequest(
      "1.0",
      "getbestblockhash",
      "getbestblockhash")).execute()

  val bestBlockHash = getBestBlockHashResponse.body()!!.result

  println("The foo foo ${bestBlockHash}")




  // Walk back


  var previousHash = bestBlockHash

  do {
    println("Prev Hash: $previousHash")

    val getBlockResponse = createRetrofit.getBlock(JsonRpcRequestWithParams(
        "1.0",
        "getblock",
        "getblock",
        listOf(previousHash!!))).execute()

    previousHash = getBlockResponse.body()!!.result!!.previousblockhash
  } while (previousHash != null)

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

  @POST("/")
  fun getBestBlockHash(@Body jsonRpcRequest: JsonRpcRequest): Call<BestBlockHash>

  @POST("/")
  fun getBlock(@Body jsonRpcRequest: JsonRpcRequestWithParams<String>): Call<Block>
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

// FIXME!
class JsonRpcRequest(
  internal val jsonrpc: String,
  internal val id: String,
  internal val method: String)

// FIXME!
class JsonRpcRequestWithParams<E>(
  internal val jsonrpc: String,
  internal val id: String,
  internal val method: String,
  internal val params: List<E>
)