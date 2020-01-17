package br.com.wgmacedo

import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
open class GarotadeBankApp {}

fun main(args: Array<String>) {
    runApplication<GarotadeBankApp>(*args)
}

@Bean
fun eventStorageEngine(): EventStorageEngine? {
    return InMemoryEventStorageEngine()
}

