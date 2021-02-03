package org.rpanic

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.websocket.WebSocketService
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

class Web3Handler {

    val web3j: Web3j = Web3j.build(HttpService("https://ropsten.infura.io/v3/a6ffe81db45d4f4091967cee2578920f", true))

    val amount = "2.5"

    val credentials = KeyStoreSource.getCredentials()

    init {
//        web3j.
    }

    fun sendEther(address: String) : String{
        val amount = Convert.fromWei(amount, Convert.Unit.ETHER);

        if(getBalance() > amount.toBigInteger()) {
            val tx = Transfer.sendFunds(
                web3j,
                credentials,
                address,
                BigDecimal.valueOf(amount.toDouble()),
                Convert.Unit.ETHER
            )
            val receipt = tx.send()
            return receipt.transactionHash
        }else{
            return "error"
        }
    }

    fun getBalance(): BigInteger {
        val balance = web3j.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).sendAsync()
        return balance.join().balance
    }


}
