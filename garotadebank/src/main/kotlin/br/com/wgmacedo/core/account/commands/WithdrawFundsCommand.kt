package br.com.wgmacedo.core.account.commands

import br.com.wgmacedo.core.account.AccountNumber
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal

data class WithdrawFundsCommand(@TargetAggregateIdentifier val accountNumber: AccountNumber, val value: BigDecimal)