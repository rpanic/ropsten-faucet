package org.rpanic

import java.math.BigInteger

class RpcHandler {

    val rpc = RpcConnection()

    val amount = "2.5"

    fun sendEther(address: String) : String{
        return rpc.sendEther(amount, getGasPrice(), address).result!!
    }

    fun getBalance() = getBalance(rpc.ownAddress)

    fun getBalance(address: String): BigInteger {

        val response = rpc.call("eth_getBalance", listOf(address, "latest"))
        val balance = BigInteger(response.result!!.substring(2), 16)
        println("Balance: $balance")
        return balance
    }

    fun getGasPrice(): BigInteger {

        val response = rpc.call("eth_gasPrice")
        val gasprice = BigInteger(response.result!!.substring(2), 16)
        println("GasPrice: $gasprice")
        return gasprice
    }

}
