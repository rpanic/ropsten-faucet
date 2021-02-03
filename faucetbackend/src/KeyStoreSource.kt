package org.rpanic

import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import java.io.File
import java.math.BigInteger
import java.nio.file.Files

object KeyStoreSource {

    fun getCredentials(): Credentials {

        val envkey: String? = System.getenv("PRIVATEKEY")
        println("Private Key: $envkey")
        val priv = if(!envkey.isNullOrEmpty()){
            envkey
        }else{
            Files.readAllLines(File(System.getProperty("user.dir") + "/config/keys.txt").toPath())[1]
        }
        val key = BigInteger(priv, 16)
        val pair = ECKeyPair.create(key)

        return Credentials.create(pair)
    }

}
