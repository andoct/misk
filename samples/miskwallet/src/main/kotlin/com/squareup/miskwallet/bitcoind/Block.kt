package com.squareup.miskwallet.bitcoind

import com.google.gson.annotations.SerializedName

class Block {
  @SerializedName("result")
  var result: Result? = null
    internal set

  // FIXME: create shared error class
  @SerializedName("error")
  var error: Error? = null
    internal set

  @SerializedName("id")
  var id: String? = null
    internal set

  inner class Result {
    @SerializedName("hash")
    var hash: String? = null
      internal set

    @SerializedName("confirmations")
    var confirmations: Long = 0
      internal set

    @SerializedName("previousblockhash")
    var previousblockhash: String? = null
      internal set
  }
}
