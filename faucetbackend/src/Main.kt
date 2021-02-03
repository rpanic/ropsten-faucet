package org.rpanic

import com.beust.klaxon.Klaxon
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigInteger

@Suppress("UNREACHABLE_CODE")
fun main() {

    val handler = RpcHandler()
    handler.getBalance()
    handler.sendEther("0xf71e1ba41f81825311e687003aa218129291e32c")

//    val http = OkHttpClient()
//    val infura = "https://ropsten.infura.io/v3/a6ffe81db45d4f4091967cee2578920f"
//    val klaxon = Klaxon()

//    val web3j: Web3j = Web3j.build(HttpService("https://ropsten.infura.io/v3/a6ffe81db45d4f4091967cee2578920f", true))
//    val credentials = KeyStoreSource.getCredentials()
//
//    val price = web3j.ethGasPrice().send()
//    val b = balance.join().balance
//    println(price)
//    return

    //GAS PRICE
//    val gas_payload = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_gasPrice\",\"params\":[],\"id\":73}"
//    val response = http.newCall(Request.Builder().url(infura).post(gas_payload.toRequestBody()).build()).execute().use { response ->
//        klaxon.parse<RpcResponse>(response.body!!.string())
//    }
//    val gasprice = BigInteger(response!!.result.substring(2), 16)
//    println("GasPrice: $gasprice")
//
//
//    val tx = RawTransaction.createEtherTransaction(
//        (4).toBigInteger(),
//        gasprice.multiply(2.toBigInteger()),
//        61000.toBigInteger(),
//        "0xf71e1bA41F81825311E687003aa218129291e32c",
//        Convert.toWei("1.337", Convert.Unit.ETHER).toBigInteger())
//
//    val signed = TransactionEncoder.signMessage(tx, credentials)
//
//    val payload = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendRawTransaction\",\"params\":[\"0x" +
//            signed.joinToString("") {
//                java.lang.String.format("%02x", it)
//            } + "\"],\"id\":1}"
//
//    val request = Request.Builder()
//        .url(infura)
//        .post(payload.toRequestBody())
//        .build()
//
//    http.newCall(request).execute().use { response -> println(response.body!!.string()) }

}


