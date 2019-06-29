package com.squareup.miskwallet.bitj

import com.google.common.collect.ImmutableList
import org.bitcoinj.core.Address
import org.bitcoinj.core.Utils
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.script.Script
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.UnreadableWalletException

import com.google.common.base.Preconditions.checkNotNull

class AccountChain {
  private val accountIndex: ChildNumber = ChildNumber.ZERO_HARDENED // TODO: fix
  private val masterPrivateKey: DeterministicKey
  private val accountKey: DeterministicKey
  val receiveKey: DeterministicKey
  private val changeKey: DeterministicKey

  constructor(seed: DeterministicSeed) : this(
      HDKeyDerivation.createMasterPrivateKey(seed.seedBytes))

  constructor(masterPrivateKey: DeterministicKey) {
    this.masterPrivateKey = masterPrivateKey
    val purpose = HDKeyDerivation.deriveChildKey(masterPrivateKey, ChildNumber(44, true))

    val coinType = HDKeyDerivation.deriveChildKey(purpose, ChildNumber.ZERO_HARDENED)

    accountKey = HDKeyDerivation.deriveChildKey(coinType, accountIndex)

    receiveKey =
        HDKeyDerivation.deriveChildKey(accountKey, ChildNumber.ZERO) // External Receive Address
    changeKey =
        HDKeyDerivation.deriveChildKey(accountKey, ChildNumber.ONE)   // Internal Change Address
  }

  fun deriveLeaf(change: Boolean, leafIndex: Int): DeterministicKey {
    val parent = if (change) changeKey else receiveKey
    return HDKeyDerivation.deriveChildKey(parent, leafIndex)
  }
}

// m / purpose' / coin_type' / account' / change / address_index
// m / 44' / 0'
// private val CHAIN_PREFIX = ImmutableList.of(ChildNumber(44, true), ChildNumber.ZERO_HARDENED)

@Throws(UnreadableWalletException::class)
fun main(args: Array<String>) {
  // 677a0b4e35ed9afa7ea8308ac4a7a77c1e5024ea1e815f36f76d31a03e86fb33d200afe02b2e5dae76e276fcfbd15996e86fa10510601943859a7410e53ee36d
  //String[] mnemonic = {"window", "file", "exit", "moment", "demise", "borrow", "outdoor", "ranch",
  //    "daughter", "response", "merry", "layer"};
  val mnemonic =
      "window file exit moment demise borrow outdoor ranch daughter response merry layer"
  val passphrase = ""
  val deterministicSeed = DeterministicSeed(mnemonic, null, passphrase,
      Utils.currentTimeMillis())// TODO: plumb timestamp

  val bip44KeyChain = AccountChain(deterministicSeed)
  val firstKey = HDKeyDerivation.deriveChildKey(bip44KeyChain.receiveKey, ChildNumber.ZERO)
  println(
      Address.fromKey(MainNetParams(), firstKey, Script.ScriptType.P2PKH))

  println(
      Address.fromKey(MainNetParams(), bip44KeyChain.deriveLeaf(false, 0),
          Script.ScriptType.P2PKH))

  val rootPrv =
      "xprv9s21ZrQH143K2gcHPabUwFnN1PVuZJRq9Qp99P8aNWRrgyUnoawka8aAyGhEf7jnZ6A8NyCyUwBKcoePDoeZbFhsuEyRuecF56e1yMx1nWH"
  val masterPrvKey = DeterministicKey.deserializeB58(rootPrv, MainNetParams())
  val altChain = AccountChain(masterPrvKey)
  println(masterPrvKey.serializePubB58(MainNetParams()))

  println(
      Address.fromKey(MainNetParams(), altChain.deriveLeaf(false, 0),
          Script.ScriptType.P2PKH))

  val rootPub = "xpub661MyMwAqRbcFAgkVc8VJPj6ZRLPxm9gWdjjwmYBvqxqZmowM8G17vtepXFGEVYeZyWkwpGjbVZi3rhp6i4q42ueF2363dUdbPDVamjGZ4g"
  val masterAltPubKey = DeterministicKey.deserializeB58(rootPub, MainNetParams())
  val altPubChain = AccountChain(masterAltPubKey)

  println(
      Address.fromKey(MainNetParams(), altPubChain.deriveLeaf(false, 0),
          Script.ScriptType.P2PKH))

}


