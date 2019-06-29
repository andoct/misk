CREATE TABLE account_chains (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  wallet_seed_id BIGINT NULL,
  xpub VARCHAR(255) NOT NULL,

  pub_key_hash TEXT NOT NULL,
  purpose_code INT UNSIGNED NOT NULL,
  coin_code INT UNSIGNED NOT NULL,
  account_code INT UNSIGNED NOT NULL,
  account_chain_type VARCHAR(255) NOT NULL,

  married_group_id BIGINT NULL,
  external_id VARCHAR(255) NULL,

  created_at timestamp(3) NOT NULL DEFAULT NOW(3),
  updated_at timestamp(3) NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
