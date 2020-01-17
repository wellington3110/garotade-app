package br.com.wgmacedo.core.account.projectors.balance

import br.com.wgmacedo.core.account.AccountNumber
import br.com.wgmacedo.core.account.events.AccountCreatedEvent
import br.com.wgmacedo.core.account.events.AddedCpfEvent
import br.com.wgmacedo.core.account.events.FundsDepositedEvent
import br.com.wgmacedo.core.account.events.FundsWithdrawnEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.math.BigDecimal

data class ClientBalanceQuery(val accountNumber: AccountNumber)

@Component
internal class BalanceProjector(private val repository: BalanceProjectionRepository) {

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        repository.save(
            BalanceProjection(
                client = event.client,
                accountNumber = event.accountNumber.value,
                balance = BigDecimal.ZERO,
                cpf = event.cpf
            )
        )
    }

    @EventHandler
    fun on(event: AddedCpfEvent) {
        repository.findById(event.accountNumber.value)
            .ifPresent {
                it.cpf = event.cpf
                repository.save(it)
            }
    }


    @EventHandler
    fun on(event: FundsDepositedEvent) {
        repository.findById(event.accountNumber.value).ifPresent { it.balance = it.balance.add(event.value) }
    }

    @EventHandler
    fun on(event: FundsWithdrawnEvent) {
        repository.findById(event.accountNumber.value).ifPresent { it.balance = it.balance.subtract(event.value) }
    }

    @QueryHandler
    fun handle(query: ClientBalanceQuery): BalanceProjection? {
        return repository.findById(query.accountNumber.value).orElse(null)
    }

}