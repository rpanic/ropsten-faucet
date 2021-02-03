package org.rpanic

import com.beust.klaxon.Klaxon
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Convert
import java.math.BigInteger

class RpcConnection {

    val http = OkHttpClient()
    var nonce = 0
    val credentials = KeyStoreSource.getCredentials()
    val infura = "https://ropsten.infura.io/v3/a6ffe81db45d4f4091967cee2578920f"
    val ownAddress = "0xd46f58e357f2e44a7272ec9bf4c6789b7808c727"

    val klaxon = Klaxon()

    init{
        nonce = call("eth_getTransactionCount", listOf(ownAddress, "latest")).result!!.run { BigInteger(this.substring(2), 16) }.toInt()
    }

    fun call(method: String, params: List<String> = listOf()) : RpcResponse{

        val paramsEncoded = if(params.isEmpty()) "" else params.map { "\"$it\"" }.joinToString(",")
        val gasPayload = "{\"jsonrpc\":\"2.0\",\"method\":\"$method\",\"params\":[$paramsEncoded],\"id\":73}"
        val response = http.newCall(Request.Builder().url(infura).post(gasPayload.toRequestBody()).build()).execute().use { response ->
            parseResponse(response.body!!.string())
        }
        println("Call: $method -> ${klaxon.toJsonString(response)}")
        return response

    }

    fun sendEther(ether: String, gasPrice: BigInteger, to: String) : RpcResponse{

        println("Sending $ether Ether to $to")

        val tx = RawTransaction.createEtherTransaction(
            nonce.toBigInteger(),
            gasPrice.div(10.toBigInteger()).multiply(11.toBigInteger()),
            300000.toBigInteger(),
            to,
            Convert.toWei(ether, Convert.Unit.ETHER).toBigInteger())

        val signed = TransactionEncoder.signMessage(tx, credentials)

        val payload = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendRawTransaction\",\"params\":[\"0x" +
                signed.joinToString("") {
                    java.lang.String.format("%02x", it)
                } + "\"],\"id\":1}"

        val request = Request.Builder()
            .url(infura)
            .post(payload.toRequestBody())
            .build()

        val response = http.newCall(request).execute().use { response ->
            parseResponse(response.body!!.string())
         }
        println("Response: ${klaxon.toJsonString(response)}")

        if(response.result != null && response.result.isValidEthTxHash()){
            nonce++
        }
        return response

    }

    private fun parseResponse(s: String) : RpcResponse{
        var x = s
        if(s.contains("\"error\":")){
            x = s.replace("\"error\":", "\"result\":")
        }
        println(x)
        return klaxon.parse<RpcResponse>(x)!!
    }

}

class RpcResponse(val id: Int, val jsonrpc: String, val result: String?){
//    var error: Error? = null
//    constructor(id: Int, jsonrpc: String, error: Error?) : this(id, jsonrpc, result = null){
//        this.error = error;
//    }
}
class Error(val code: Int, message: String)
