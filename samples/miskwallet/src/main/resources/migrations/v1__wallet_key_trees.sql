CREATE TABLE wallet_key_trees (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  seed TEXT NOT NULL,
  -- active_account_id BIGINT NOT NULL,
  created_at timestamp(3) NOT NULL DEFAULT NOW(3),
  updated_at timestamp(3) NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;