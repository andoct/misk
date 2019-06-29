package com.squareup.miskwallet.model

import misk.hibernate.DbTimestampedEntity
import misk.hibernate.DbUnsharded
import misk.hibernate.Id
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Table

// TODO: DO NOT USE THIS CLASS TO STORE REAL SEEDS. We will use misk encryption.
@Entity
@Table(name = "wallet_seeds")
class DbWalletSeed() : DbUnsharded<DbWalletSeed>, DbTimestampedEntity {
  @javax.persistence.Id
  @GeneratedValue
  override lateinit var id: Id<DbWalletSeed>

  @Column(nullable = false, updatable = false)
  lateinit var mnemonic: String

  @Column(nullable = false, updatable = false)
  lateinit var entropy: ByteArray

  @Column(nullable = false, updatable = false)
  lateinit var seed: ByteArray

  @Column
  override lateinit var created_at: Instant

  @Column
  override lateinit var updated_at: Instant

  constructor(
    mnemonic: String,
    entropy: ByteArray,
    seed: ByteArray
  ) : this() {
    this.mnemonic = mnemonic
    this.entropy = entropy
    this.seed = seed
  }

//  fun toModel() = Customer(
//      id = id,
//      token = token
//  )
}
