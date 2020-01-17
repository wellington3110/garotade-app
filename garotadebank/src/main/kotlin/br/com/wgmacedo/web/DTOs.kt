package br.com.wgmacedo.web

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

data class CreateAccountDTO(val client: String, val cpf: String)
data class FundsDTO (val value: BigDecimal)
data class EventDTO (val payload: Any, val name: String, val timestamp: Instant)
data class BiggestDepositDTO(val value: BigDecimal, val date: LocalDateTime)
data class SummaryDTO(val cpf: String, val client: String, val balance: BigDecimal, val biggestDeposit: BiggestDepositDTO)
data class DashboardDTO(val summary: SummaryDTO, val events: List<EventDTO>)
data class AddCpfDTO (val cpf: String)
data class LoginDTO(val cpf: String)