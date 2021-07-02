package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.statement.SqlQuery


val jdbi = Jdbi.create("jdbc:postgresql://localhost:5435/sample","sample","sample")
    .installPlugin(SqlObjectPlugin())
    .installPlugin(KotlinPlugin())

interface ShohinDao {
    @SqlQuery("SELECT name FROM shohin")
    fun selectNameList(): List<String>
    @SqlQuery("SELECT * FROM shohin")
    fun selectAll(): List<Shohin>
}

data class Shohin(
    @ColumnName("shohin_id")
    val id: String,
    val name: String,
    val price: Int
)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            val shohinDao: ShohinDao = jdbi.onDemand(ShohinDao::class.java)
            val shohin = shohinDao.selectAll()
            call.respond(shohin)
        }
    }
}

