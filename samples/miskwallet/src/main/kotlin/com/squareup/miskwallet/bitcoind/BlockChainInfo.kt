package com.squareup.miskwallet.bitcoind

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.ArrayList

class BlockChainInfo {
  @SerializedName("result")
  var result: Result? = null
    internal set

  @SerializedName("error")
  var error: Error? = null
    internal set

  @SerializedName("id")
  var id: String? = null
    internal set

  inner class Result {
    @SerializedName("chain")
    var chain: String? = null
      internal set

    @SerializedName("blocks")
    var blocks: Long = 0
      internal set

    @SerializedName("headers")
    var headers: Long = 0
      internal set

    @SerializedName("bestblockhash")
    var bestBlockHash: String? = null
      internal set

    @SerializedName("difficulty")
    var difficulty: BigDecimal? = null
      internal set

    @SerializedName("mediantime")
    var medianTime: Long = 0
      internal set

    @SerializedName("verificationprogress")
    var verificationProgress: Long = 0
      internal set

    @SerializedName("initialblockdownload")
    var isInitialBlockDownload: Boolean = false
      internal set

    @SerializedName("chainwork")
    var chainwork: String? = null
      internal set

    @SerializedName("size_on_disk")
    var sizeOnDisk: Long = 0
      internal set

    @SerializedName("pruned")
    var isPruned: Boolean = false
      internal set

    @SerializedName("softforks")
    var softforks: List<SoftForks> = ArrayList()
      internal set

    @SerializedName("bip9_softforks")
    var bip9Softforks: Bip9Softforks? = null
      internal set

    @SerializedName("warnings")
    var warnings: String? = null
      internal set

    inner class SoftForks {
      @SerializedName("id")
      var id: String? = null
        internal set

      @SerializedName("version")
      var version: Int = 0
        internal set

      @SerializedName("reject")
      var reject: Reject? = null
        internal set

      inner class Reject {
        @SerializedName("status")
        var isStatus: Boolean = false
          internal set
      }

      override fun toString(): String {
        return "SoftForks(id=$id, version=$version, reject=$reject)"
      }

    }

    inner class Bip9Softforks {
      @SerializedName("csv")
      var csv: Csv? = null
        internal set

      @SerializedName("segwit")
      var segwit: Segwit? = null
        internal set

      inner class Csv {
        @SerializedName("status")
        var status: String? = null
          internal set

        @SerializedName("startTime")
        var startTime: Long = 0
          internal set

        @SerializedName("timeout")
        var timeOut: Long = 0
          internal set

        @SerializedName("since")
        var since: Long = 0
          internal set
      }

      inner class Segwit {
        @SerializedName("status")
        var status: String? = null
          internal set

        @SerializedName("startTime")
        var startTime: Long = 0
          internal set

        @SerializedName("timeout")
        var timeOut: Long = 0
          internal set

        @SerializedName("since")
        var since: Long = 0
          internal set
      }
    }
  }

  override fun toString(): String {
    return "BlockChainInfo(result=$result, error=$error, id=$id)"
  }

  // inner class Error : JsonRpcErrorResponse()


}