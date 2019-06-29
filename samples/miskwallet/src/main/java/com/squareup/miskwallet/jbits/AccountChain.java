package com.squareup.miskwallet.jbits;

import com.google.common.collect.ImmutableList;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountChain {
  // m / purpose' / coin_type' / account' / change / address_index
  // m / 44' / 0'
  private static final ImmutableList<ChildNumber>
      CHAIN_PREFIX = ImmutableList.of(new ChildNumber(44, true), ChildNumber.ZERO_HARDENED);

  private final ChildNumber accountIndex;
  private final DeterministicKey masterPrivateKey;
  private final DeterministicKey accountKey;
  public final DeterministicKey receiveKey;
  private final DeterministicKey changeKey;

  public AccountChain(DeterministicSeed seed) {
    this.accountIndex = ChildNumber.ZERO_HARDENED;

    // TODO: master private stored ad PersistentKeyTree level
    masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(checkNotNull(seed.getSeedBytes()));

    DeterministicKey purpose =
        HDKeyDerivation.deriveChildKey(masterPrivateKey, new ChildNumber(44, true));

    DeterministicKey coinType =
        HDKeyDerivation.deriveChildKey(purpose, ChildNumber.ZERO_HARDENED);

    accountKey = HDKeyDerivation.deriveChildKey(coinType, accountIndex);

    receiveKey = HDKeyDerivation.deriveChildKey(accountKey, ChildNumber.ZERO); // External Receive Address
    changeKey = HDKeyDerivation.deriveChildKey(accountKey, ChildNumber.ONE);   // Internal Change Address
  }

  public DeterministicKey deriveLeaf(boolean change, int leafIndex) {
    DeterministicKey parent = change ? changeKey : receiveKey;
    return HDKeyDerivation.deriveChildKey(parent, leafIndex);
  }

  public static void main(String[] args) throws UnreadableWalletException {
    // 677a0b4e35ed9afa7ea8308ac4a7a77c1e5024ea1e815f36f76d31a03e86fb33d200afe02b2e5dae76e276fcfbd15996e86fa10510601943859a7410e53ee36d
    //String[] mnemonic = {"window", "file", "exit", "moment", "demise", "borrow", "outdoor", "ranch",
    //    "daughter", "response", "merry", "layer"};
    String mnemonic =
        "window file exit moment demise borrow outdoor ranch daughter response merry layer";
    String passphrase = "";
    DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonic, null, passphrase,
        Utils.currentTimeMillis());// TODO: plumb timestamp

    AccountChain bip44KeyChain = new AccountChain(deterministicSeed);
    DeterministicKey firstKey =
        HDKeyDerivation.deriveChildKey(bip44KeyChain.receiveKey, ChildNumber.ZERO);
    System.out.println(
        Address.fromKey(new MainNetParams(), firstKey, Script.ScriptType.P2PKH));
  }
}
