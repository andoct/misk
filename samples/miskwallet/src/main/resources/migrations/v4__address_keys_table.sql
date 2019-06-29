CREATE TABLE address_keys (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  account_chain_id BIGINT NOT NULL,
  ks_hash VARBINARY(255) NOT NULL,
  is_change BOOLEAN NOT NULL,
  address VARCHAR(255) NOT NULL,
  address_encoding VARCHAR(255) NOT NULL,
  address_type VARCHAR(255) NOT NULL,
  -- redeem_script or just redeem_script_type TODO: not really needed but worth considering.

  created_at timestamp(3) NOT NULL DEFAULT NOW(3),
  updated_at timestamp(3) NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
