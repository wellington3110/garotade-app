package br.com.wgmacedo.core.account.events

import br.com.wgmacedo.core.account.AccountNumber

data class AddedCpfEvent(val accountNumber: AccountNumber, val cpf: String
)