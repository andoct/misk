CREATE TABLE married_groups (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  account_chain_type VARCHAR(255) NOT NULL,
  threshold INT NOT NULL,
  created_at timestamp(3) NOT NULL DEFAULT NOW(3),
  updated_at timestamp(3) NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
