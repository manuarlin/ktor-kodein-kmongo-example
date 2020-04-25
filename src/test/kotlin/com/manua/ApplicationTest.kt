package com.manua

import com.google.gson.Gson
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.testcontainers.containers.GenericContainer

class ApplicationTest : AnnotationSpec() {

    private val mongoContainer = MongoContainer().withExposedPorts(27017)
    private lateinit var client: CoroutineClient

    private val items = listOf(
        Item("Sporting Goods", "\$49.99", true, "Football"),
        Item("Sporting Goods", "\$9.99", true, "Baseball"),
        Item("Sporting Goods", "\$29.99", false, "Basketball"),
        Item("Electronics", "\$99.99", true, "iPod Touch"),
        Item("Electronics", "\$399.99", false, "iPhone 5"),
        Item("Electronics", "\$199.99", true, "Nexus 7")
    )

    @BeforeAll
    fun `launch database`() {
        println("Instantiate mongo db")
        mongoContainer.start()
        client = KMongo.createClient("mongodb://localhost:${mongoContainer.firstMappedPort}").coroutine
    }

    @BeforeEach
    fun `save items in database`() {
        println("Begin insertion test")
        val database = client.getDatabase("test")
        val col = database.getCollection<Item>()
        runBlocking {
            col.insertMany(items)
        }
    }

    @AfterEach
    fun `clean database`() {
        val database = client.getDatabase("test")
        val col = database.getCollection<Item>()
        runBlocking {
            col.deleteMany()
        }
    }

    @Test
    fun testRequest() = withTestApplication({
        module()
    }) {
        with(handleRequest(HttpMethod.Get, "/items")) {
            response.status() shouldBe HttpStatusCode.OK
            val itemsInResponse = Gson().fromJson(response.content, Array<Item>::class.java).toList()
            itemsInResponse shouldContainExactly items
        }
    }
}

class MongoContainer : GenericContainer<MongoContainer>("mongo:4.0")
