package org.rpanic

import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.WalletUtils
import java.io.File
import java.math.BigInteger
import java.nio.file.Files

object KeyStoreSource {

    fun getCredentials(): Credentials {

        val (_, priv) = Files.readAllLines(File(System.getProperty("user.dir") + "/config/keys.txt").toPath())
        val key = BigInteger(priv, 16)
        val pair = ECKeyPair.create(key)

        return Credentials.create(pair)
    }

}
