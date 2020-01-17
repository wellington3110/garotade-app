package br.com.wgmacedo.core.account.projectors.balance

import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BalanceProjection(
    @Id var accountNumber: String? = "",
    var client: String = "",
    var cpf: String? = "",
    var balance: BigDecimal = BigDecimal.ZERO
)

interface BalanceProjectionRepository : JpaRepository<BalanceProjection, String>