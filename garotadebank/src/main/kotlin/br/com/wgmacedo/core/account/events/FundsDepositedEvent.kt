package br.com.wgmacedo.core.account.events

import br.com.wgmacedo.core.account.AccountNumber
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal

data class FundsDepositedEvent(@TargetAggregateIdentifier val accountNumber: AccountNumber, val value: BigDecimal) :
    FundsOperationEvent