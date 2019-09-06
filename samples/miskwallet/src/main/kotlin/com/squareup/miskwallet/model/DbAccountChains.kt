package com.squareup.miskwallet.model

import misk.hibernate.DbTimestampedEntity
import misk.hibernate.DbUnsharded
import misk.hibernate.Id
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "account_chains")
class DbAccountChain() : DbUnsharded<DbAccountChain>, DbTimestampedEntity {
  @javax.persistence.Id
  @GeneratedValue
  override lateinit var id: Id<DbAccountChain>

  @ManyToOne
  @JoinColumn(name = "wallet_seed_id", nullable = false)
  lateinit var walletSeed: DbWalletSeed

  @Column(nullable = false, updatable = false, insertable = false)
  lateinit var wallet_seed_id: Id<DbWalletSeed>

  @Column
  override lateinit var created_at: Instant

  @Column
  override lateinit var updated_at: Instant

  lateinit var xpub: String

//  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
//  wallet_seed_id BIGINT NULL,
//  xpub VARCHAR(255) NOT NULL,
//
//  pub_key_hash TEXT NOT NULL,
//  purpose_code INT UNSIGNED NOT NULL,
//  coin_code INT UNSIGNED NOT NULL,
//  account_code INT UNSIGNED NOT NULL,
//  account_chain_type VARCHAR(255) NOT NULL,
//
//  married_group_id BIGINT NULL,
//  external_id VARCHAR(255) NULL,
//
//  created_at timestamp(3) NOT NULL DEFAULT NOW(3),
//  updated_at timestamp(3) NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3)

  constructor(
    walletSeed: DbWalletSeed
  ) : this() {
    this.walletSeed = walletSeed
  }

//  fun toModel() = Customer(
//      id = id,
//      token = token
//  )
}
