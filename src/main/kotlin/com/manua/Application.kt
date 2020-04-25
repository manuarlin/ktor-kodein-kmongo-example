package com.manua

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.flow.toList
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(kodein: Kodein) {
    install(ContentNegotiation) {
        gson { }
        routing {
            get("/items") {
                val handler by kodein.instance<ItemHandler>()
                call.response.call.respond(HttpStatusCode.OK, handler.get().toList())
            }
        }
    }
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() = module(Kodein {
    bind<ItemHandler>() with singleton { ItemHandler(kodein) }
    bind<ItemRepository>() with singleton { ItemRepository(kodein) }
    bind<CoroutineCollection<Item>>() with singleton {
        KMongo.createClient().coroutine
            .getDatabase("test")
            .getCollection<Item>()
    }
})

