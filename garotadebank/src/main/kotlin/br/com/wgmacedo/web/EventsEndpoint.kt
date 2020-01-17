package br.com.wgmacedo.web

import br.com.wgmacedo.core.account.AccountNumber
import org.axonframework.eventsourcing.eventstore.EventStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors

@RestController
@CrossOrigin
@RequestMapping("/events")
class EventsEndpoint @Autowired constructor(
    private val eventStore: EventStore
) {

    @GetMapping("/{aggregateIdentifier}")
    fun events(@PathVariable("aggregateIdentifier") aggregateIdentifier: String): MutableList<EventDTO>? {
        val accountNumber = AccountNumber(aggregateIdentifier);
        return eventStore.readEvents(accountNumber.value)
            .asStream()
            .map { EventDTO(it.payload, it.payloadType.simpleName, it.timestamp) }
            .sorted { a, b -> b.timestamp.compareTo(a.timestamp) }
            .collect(Collectors.toList())
    }
}