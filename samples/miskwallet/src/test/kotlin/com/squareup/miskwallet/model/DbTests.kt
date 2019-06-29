package com.squareup.miskwallet.model

import com.google.common.base.Splitter
import com.google.inject.Module
import com.google.inject.util.Modules
import com.squareup.miskwallet.scaffolding.MiskWallet
import com.squareup.miskwallet.scaffolding.MiskWalletTestingModule
import misk.hibernate.Id
import misk.hibernate.Query
import misk.hibernate.Transacter
import misk.testing.MiskTest
import misk.testing.MiskTestModule
import org.assertj.core.api.Assertions.assertThat
import org.bitcoinj.core.Utils
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.wallet.DeterministicSeed
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@MiskTest(startService = true)
class RealCategoryStoreTest {
  @Suppress("unused")
  @MiskTestModule
  val module: Module = Modules.combine(MiskWalletTestingModule())

  // @Inject lateinit var categoryStore: CategoryStore
  @Inject lateinit var queryFactory: Query.Factory
  @Inject @MiskWallet
  lateinit var transacter: Transacter

  @Test
  fun `fooBar`() {
    val mnemonic =
        "window file exit moment demise borrow outdoor ranch daughter response merry layer"
    val passphrase = ""
    val entropy = MnemonicCode().toEntropy(mnemonic.split(' '))
    val deterministicSeed = DeterministicSeed(mnemonic, null, passphrase,
        Utils.currentTimeMillis())

    val dbWalletSeed = DbWalletSeed(mnemonic, entropy, deterministicSeed.seedBytes!!)

    val theId: Id<DbWalletSeed> = transacter.transaction { session ->
      session.save(dbWalletSeed)
    }

    val restoredDbWalletSeed = testQuery(theId)
    val restoredMnemonic = MnemonicCode().toMnemonic(restoredDbWalletSeed.entropy)
    val restored =
        DeterministicSeed(restoredMnemonic, restoredDbWalletSeed.seed, "", Utils.currentTimeMillis())

    assertThat(restored.seedBytes).isEqualTo(deterministicSeed.seedBytes)
    assertThat(restored.mnemonicCode).isEqualTo(deterministicSeed.mnemonicCode)
  }

  fun testQuery(id: Id<DbWalletSeed>): DbWalletSeed {
//    transacter.transaction { session ->
//      queryFactory.newQuery<CategoryQuery>().token(category.getToken())
//          .uniqueResult(session).let { it?.toModel() }!!

    return transacter.transaction { session ->
      session.load(id, DbWalletSeed::class)
    }

  }
}