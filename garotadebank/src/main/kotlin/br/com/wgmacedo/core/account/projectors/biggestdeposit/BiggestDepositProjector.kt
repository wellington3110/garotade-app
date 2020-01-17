package br.com.wgmacedo.core.account.projectors.biggestdeposit

import br.com.wgmacedo.core.account.AccountNumber
import br.com.wgmacedo.core.account.events.AccountCreatedEvent
import br.com.wgmacedo.core.account.events.FundsDepositedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.EventMessage
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset


data class BiggestDepositQuery(val accountNumber: AccountNumber)

@Component
internal class BiggestDepositProjector(private val repository: BiggestDepositProjectionRepository) {

    @EventHandler(payloadType = AccountCreatedEvent::class)
    fun onCreatedAccountEvent(eventMessage: EventMessage<AccountCreatedEvent>) {
        val event = eventMessage.payload
        repository.save(
            BiggestDepositProjection(
                client = event.client,
                accountNumber = event.accountNumber.value,
                biggestDeposit = BigDecimal.ZERO,
                date = LocalDateTime.ofInstant(eventMessage.timestamp, ZoneOffset.systemDefault())
            )
        )
    }

    @EventHandler(payloadType = FundsDepositedEvent::class)
    fun onFundsDepositedEvent(eventMessage: EventMessage<FundsDepositedEvent>) {
        val event = eventMessage.payload
        repository.findById(event.accountNumber.value)
            .ifPresent {
                if(event.value > it.biggestDeposit) {
                    it.biggestDeposit = event.value
                    it.date = LocalDateTime.ofInstant(eventMessage.timestamp, ZoneOffset.systemDefault())
                }
            }
    }

    @QueryHandler
    fun handle(query: BiggestDepositQuery): BiggestDepositProjection? {
        return repository.findById(query.accountNumber.value).orElse(null)
    }
}