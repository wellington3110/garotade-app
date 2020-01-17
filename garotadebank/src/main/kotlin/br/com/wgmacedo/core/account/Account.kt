package br.com.wgmacedo.core.account

import br.com.wgmacedo.core.account.commands.AddCpfCommand
import br.com.wgmacedo.core.account.commands.CreateAccountCommand
import br.com.wgmacedo.core.account.commands.DepositFundsCommand
import br.com.wgmacedo.core.account.commands.WithdrawFundsCommand
import br.com.wgmacedo.core.account.events.AccountCreatedEvent
import br.com.wgmacedo.core.account.events.AddedCpfEvent
import br.com.wgmacedo.core.account.events.FundsDepositedEvent
import br.com.wgmacedo.core.account.events.FundsWithdrawnEvent
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal


@Aggregate
class Account{

    @AggregateIdentifier
    private lateinit var accountNuber: AccountNumber
    private lateinit var client: String
    private lateinit var balance: BigDecimal
    private lateinit var cpf: String

    constructor()

    @CommandHandler
    constructor(command: CreateAccountCommand) {
        AggregateLifecycle.apply(AccountCreatedEvent(accountNumber = command.accountNumber, client = command.client, cpf = command.cpf))
    }

    @CommandHandler
    fun handle(command: AddCpfCommand) {
        AggregateLifecycle.apply(AddedCpfEvent(accountNumber = command.accountNumber, cpf = command.cpf))
    }

    @CommandHandler
    fun handle(command: DepositFundsCommand) {
        AggregateLifecycle.apply(FundsDepositedEvent(accountNumber = command.accountNumber, value = command.value))
    }

    @CommandHandler
    fun handle(command: WithdrawFundsCommand) {
        val value = command.value
        if(isBiggerThanBalance(value)) throw CommandExecutionException("value must be to withdraw must be equal or greater than actual balance", null)
        AggregateLifecycle.apply(FundsWithdrawnEvent(accountNumber = command.accountNumber, value = command.value))
    }

    private fun isBiggerThanBalance(value: BigDecimal) = value.compareTo(balance) == 1

    @EventSourcingHandler
    fun on(event: AccountCreatedEvent) {
        accountNuber = event.accountNumber
        client = event.client
        balance = BigDecimal.ZERO
    }

    @EventSourcingHandler
    fun on(event: FundsDepositedEvent) {
        balance = balance.add(event.value)
    }

    @EventSourcingHandler
    fun on(event: FundsWithdrawnEvent) {
        balance = balance.subtract(event.value)
    }

    @EventSourcingHandler
    fun on(event: AddedCpfEvent) {
        cpf = event.cpf
    }


}