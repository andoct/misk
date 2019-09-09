package com.squareup.miskwallet.bitcoind

import com.google.gson.annotations.SerializedName

class BestBlockHash {
  @SerializedName("result")
  var result: String? = null
    internal set

  // FIXME: create shared error class
  @SerializedName("error")
  var error: Error? = null
    internal set

  @SerializedName("id")
  var id: String? = null
    internal set
}
