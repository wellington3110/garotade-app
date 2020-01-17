package br.com.wgmacedo.core.account.reactors

import br.com.wgmacedo.core.account.AccountNumber
import br.com.wgmacedo.core.account.events.AccountCreatedEvent
import br.com.wgmacedo.core.account.events.FundsDepositedEvent
import br.com.wgmacedo.core.account.events.FundsWithdrawnEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
internal class ControlBalanceReactor {

    val balances = HashMap<AccountNumber, BigDecimal>()
    val valueToSendEmail = BigDecimal(100)

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        balances[event.accountNumber] = BigDecimal.ZERO
    }

    @EventHandler
    fun on(event: FundsDepositedEvent) {
        balances.computeIfPresent(event.accountNumber) { _, balance -> balance.add(event.value) }
    }

    @EventHandler
    fun on(event: FundsWithdrawnEvent) {
        balances.computeIfPresent(event.accountNumber) { _, balance ->
            val newBalance = balance.minus(event.value)
            if (isLessThanValueToSendEmail(newBalance)) {
                println("Should send an email here warning the client about your balance")
            }
            newBalance
        }
    }

    private fun isLessThanValueToSendEmail(value: BigDecimal) = value.compareTo(valueToSendEmail) == -1

}