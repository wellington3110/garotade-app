package br.com.wgmacedo.core.account.commands

import br.com.wgmacedo.core.account.AccountNumber
import org.axonframework.commandhandling.RoutingKey

data class CreateAccountCommand(@RoutingKey var accountNumber: AccountNumber, val client: String, val cpf: String)