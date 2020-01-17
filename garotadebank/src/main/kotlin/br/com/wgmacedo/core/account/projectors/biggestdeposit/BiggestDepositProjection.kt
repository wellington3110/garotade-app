package br.com.wgmacedo.core.account.projectors.biggestdeposit

import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BiggestDepositProjection(
    @Id var accountNumber: String? = "",
    var client: String = "",
    var date: LocalDateTime,
    @Column(name = "biggest_deposit") var biggestDeposit: BigDecimal = BigDecimal.ZERO
)

interface BiggestDepositProjectionRepository : JpaRepository<BiggestDepositProjection, String>