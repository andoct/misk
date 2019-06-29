package com.squareup.miskwallet.scaffolding

import com.squareup.miskwallet.MiskWalletConfig
import misk.MiskTestingServiceModule
import misk.clustering.fake.lease.FakeLeaseModule
import misk.config.MiskConfig
import misk.environment.Environment
import misk.environment.EnvironmentModule
import misk.hibernate.HibernateTestingModule
import misk.inject.KAbstractModule
import misk.logging.LogCollectorModule

internal class MiskWalletTestingModule : KAbstractModule() {
  override fun configure() {
    val config = MiskConfig.load<MiskWalletConfig>("miskwallet", Environment.TESTING)
    install(LogCollectorModule())
    install(MiskTestingServiceModule())
    install(EnvironmentModule(Environment.TESTING))
    install(StorageModule(config))
    install(HibernateTestingModule(MiskWallet::class))
    install(FakeLeaseModule())
  }
}
