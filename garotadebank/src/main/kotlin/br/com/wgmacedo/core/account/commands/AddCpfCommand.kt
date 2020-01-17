package br.com.wgmacedo.core.account.commands

import br.com.wgmacedo.core.account.AccountNumber
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class AddCpfCommand(@TargetAggregateIdentifier var accountNumber: AccountNumber, val cpf: String)