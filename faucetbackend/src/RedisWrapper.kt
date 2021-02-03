package org.rpanic

import io.ktor.config.*
import com.lambdaworks.redis.RedisConnection
import com.lambdaworks.redis.RedisURI
import com.lambdaworks.redis.RedisClient
import com.lambdaworks.redis.protocol.SetArgs
import kotlin.math.max

class RedisWrapper(val config: ApplicationConfig) {

    lateinit var connection: RedisConnection<String, String>

    val ip_timeout = 60*60.toLong() //1h
    val address_timeout = 24*60*60.toLong() //1d

    fun connect(){

        val host = config.property("redis.host").getString()
        val port = config.property("redis.port").getString()
        val redisClient = RedisClient(
            RedisURI.create("redis://$host:$port")
        )
        connection = redisClient.connect()

    }

    //Checks if ip is valid for request
    fun requestLimit(ip: String, address: String) : Pair<Boolean, Long>{
        val ipV = connection.get(ip)
        val addrV = connection.get(address)

        //Check limits
        var limits = true
        if(ipV != null){
            limits = limits && ipV != "2"
        }
        if(addrV != null){
            limits = limits && addrV != "1"
        }

        if(limits) {
            val newVal = if(ipV != null && ipV == "1") {
                "2"
            }else {
                "1"
            }
            connection.set(ip, newVal, SetArgs.Builder.ex(ip_timeout))
            connection.set(address, "1", SetArgs.Builder.ex(address_timeout))
            return true to 0L
        }else{
            var ttl = if(ipV != null) connection.ttl(ip) else 0L
            ttl = max(ttl, if(addrV != null) connection.ttl(addrV) else 0L)
            return false to ttl
        }
    }

    fun clear(ip: String, address: String){
        connection.set(ip, "0")
        connection.set(address, "0")
    }

    val queueSize = 10

    val txKeys = (0 until queueSize).map { "tx_$it" }

    fun getTxs() : List<Transaction>{
        return connection.mget(*txKeys.toTypedArray()).filterNotNull().map { Transaction.fromCsv(it) }.sortedByDescending { it.date }
    }

    fun pushTx(tx: Transaction){
        val index = (connection.get("tx_counter") ?: "0").toInt()
        connection.set("tx_counter", ((index + 1) % queueSize).toString())
        connection.set("tx_$index", tx.toCsv())
    }

}

data class Transaction(val id: String, val address: String, val date: Long){
    fun toCsv() = "$id;$address;$date"
    companion object{
        fun fromCsv(s: String) : Transaction{
            val (id, adr, date) = s.split(";")
            return Transaction(id, adr, date.toLong())
        }
    }
}
