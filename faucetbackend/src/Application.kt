package org.rpanic

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.gson.*
import io.ktor.features.*
import org.web3j.utils.Convert
import java.io.File
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    install(CORS){
        method(HttpMethod.Options)
        header(HttpHeaders.XForwardedProto)
        anyHost()
        host("localhost")
        // host("my-host:80")
        // host("my-host", subDomains = listOf("www"))
        // host("my-host", schemes = listOf("http", "https"))
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }

    val redisWrapper = RedisWrapper(environment.config)
    redisWrapper.connect()

//    val web3Handler = Web3Handler()
    val rpcHandler = RpcHandler()

    routing {

        get("/balanceLeft") {
            val res = rpcHandler.getBalance()
            var str = Convert.fromWei(res.toBigDecimal(), Convert.Unit.ETHER).toString()
            str = str.substring(0, str.indexOf(".").run { if(this != -1) this else str.length - 1 } + 3)
            call.respond(str)
        }

        get("/clear"){
            val address = call.parameters["address"]!!
            val ip = call.request.origin.remoteHost
            redisWrapper.clear(ip, address)
            call.respond("Hello")
        }

        get("/txs"){
            call.respond(redisWrapper.getTxs())
        }

        get("/putTx"){
            val tx = call.receive<Transaction>()
            redisWrapper.pushTx(tx)
        }

        get("/requestEth") {
            val address = call.parameters["address"]!!
            val ip = call.request.origin.remoteHost

            if (address.isValidEthAddress()) {

                val limit = redisWrapper.requestLimit(ip, address)
                if (limit.first) {

                    try {
//                        val hash = "0x0b49e9eb95fbccf45dcd76176615b7306a20bcccf74afa4f9f94158225a89d1e"
                        val hash = rpcHandler.sendEther(address)
                        redisWrapper.pushTx(Transaction(hash, address, System.currentTimeMillis()))
                        call.respond(HttpStatusCode.OK, ResponseObject("OK", hash))
                    }catch(e : Exception){
                        e.printStackTrace()
                    }

                } else {
                    val time = limit.second
                    val text = when {
                        time < 60 -> {
                            "$time seconds"
                        }
                        time < 60*60 -> {
                            "${time / 60} minutes"
                        }
                        else -> {
                            "${time / 60 / 60} hours"
                        }
                    }
                    call.respond(HttpStatusCode.TooManyRequests, ResponseObject("Rate Limited", "Rate Limit reached, wait $text"))
                }

            } else {
                call.respond(HttpStatusCode.BadRequest, ResponseObject("Address invalid", "Given Address is invalid"))
            }
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/") {
            preCompressed {
                this.files(File(System.getProperty("user.dir") + "/static"))
                default(File(System.getProperty("user.dir") + "/static/index.html"))
            }
        }
    }
}

fun String.isValidEthAddress() =
    "^0x[a-fA-F0-9]{40}\$".toRegex().matches(this)

fun String.isValidEthTxHash() =
    "^0x[a-fA-F0-9]{64}\$".toRegex().matches(this)


data class ResponseObject(
    val status: String,
    val message: String
)

