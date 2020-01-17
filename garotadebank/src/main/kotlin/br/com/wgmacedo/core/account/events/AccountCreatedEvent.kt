package br.com.wgmacedo.core.account.events

import br.com.wgmacedo.core.account.AccountNumber

data class AccountCreatedEvent(val accountNumber: AccountNumber, val client: String, val cpf: String)