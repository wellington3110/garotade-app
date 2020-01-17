package br.com.wgmacedo.web

import br.com.wgmacedo.core.account.AccountNumber
import br.com.wgmacedo.core.account.commands.AddCpfCommand
import br.com.wgmacedo.core.account.commands.CreateAccountCommand
import br.com.wgmacedo.core.account.commands.DepositFundsCommand
import br.com.wgmacedo.core.account.commands.WithdrawFundsCommand
import br.com.wgmacedo.core.account.events.AccountCreatedEvent
import br.com.wgmacedo.core.account.events.FundsOperationEvent
import br.com.wgmacedo.core.account.projectors.balance.BalanceProjection
import br.com.wgmacedo.core.account.projectors.balance.ClientBalanceQuery
import br.com.wgmacedo.core.account.projectors.biggestdeposit.BiggestDepositProjection
import br.com.wgmacedo.core.account.projectors.biggestdeposit.BiggestDepositQuery
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

var lastAccountNumber = 1

@RestController
@CrossOrigin
@RequestMapping("/account")
class AccountEndpoint @Autowired constructor(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway,
    private val eventStore: EventStore
) {

    @PostMapping
    fun createAccount(@RequestBody dto: CreateAccountDTO): ResponseEntity<Map<String, String>> {
        val accountNumber = AccountNumber(lastAccountNumber)
        val createAccountCommand = CreateAccountCommand(accountNumber, dto.client, dto.cpf)
        commandGateway.sendAndWait<Unit>(createAccountCommand)
        return ok(mapOf("accountNumber" to accountNumber.value))
    }

    @PostMapping("/{accountNumber}/deposit")
    fun deposit(@PathVariable("accountNumber") accountNumber: String, @RequestBody dto: FundsDTO): CompletableFuture<Unit>? {
        val fundsDepositCommand = DepositFundsCommand(AccountNumber(accountNumber), dto.value)
        return commandGateway.send<Unit>(fundsDepositCommand)
    }

    @PostMapping("/{accountNumber}/withdraw")
    fun withdraw(@PathVariable("accountNumber") accountNumber: String, @RequestBody dto: FundsDTO): CompletableFuture<Unit>? {
        val fundsWithdrawCommand = WithdrawFundsCommand(AccountNumber(accountNumber), dto.value)
        return commandGateway.send<Unit>(fundsWithdrawCommand)
    }

    @PostMapping("/{accountNumber}/add-cpf")
    fun addCpf(@PathVariable("accountNumber")  accountNumber: String, @RequestBody dto: AddCpfDTO): CompletableFuture<Unit>? {
        return commandGateway.send<Unit>(AddCpfCommand(cpf = dto.cpf, accountNumber = AccountNumber(accountNumber)))
    }

    @GetMapping("/{accountNumber}")
    fun dashboard(@PathVariable("accountNumber") number: String): DashboardDTO {
        val accountNumber = AccountNumber(number)
        val biggestDepositAsync = getBiggestDepositAsync(accountNumber)
        val balanceAsync = getBalanceAsync(accountNumber)
        val accountOperationsAsync = getAccountOperationsEventsAsync(accountNumber)
        val biggestDeposit = biggestDepositAsync.join()
        val balance = balanceAsync.join()
        val accountOperations = accountOperationsAsync.join()
        return DashboardDTO(events = accountOperations, summary = SummaryDTO(
            cpf = balance.cpf.let { it ?: "" },
            client = balance.client,
            balance = balance.balance,
            biggestDeposit = BiggestDepositDTO(
                value = biggestDeposit.biggestDeposit,
                date = biggestDeposit.date
            )
        ))
    }

    @GetMapping("/{accountNumber}/balance")
    fun balance(@PathVariable("accountNumber") accountNumber: String): CompletableFuture<BalanceProjection> {
        return getBalanceAsync(AccountNumber(accountNumber))
    }

    @GetMapping("/{accountNumber}/biggest_deposit")
    fun biggestDeposit(@PathVariable("accountNumber") accountNumber: String): CompletableFuture<BiggestDepositProjection> {
        return getBiggestDepositAsync(AccountNumber(accountNumber))
    }

    @EventHandler
    fun getLastAccountNumber(event: AccountCreatedEvent) {
        // Quick solution to increment the client account number, but this is not recommended to do in production.
        lastAccountNumber = event.accountNumber.value.toInt() + 1
    }

    fun getBalanceAsync(accountNumber: AccountNumber): CompletableFuture<BalanceProjection> {
        return fetchOrThrowsNotFound(queryGateway.query(
            ClientBalanceQuery(accountNumber),
            ResponseTypes.instanceOf(BalanceProjection::class.java)
        ))
    }

    fun getBiggestDepositAsync(accountNumber: AccountNumber): CompletableFuture<BiggestDepositProjection> {
        return fetchOrThrowsNotFound(queryGateway.query(
            BiggestDepositQuery(accountNumber),
            ResponseTypes.instanceOf(BiggestDepositProjection::class.java)
        ))
    }

    fun getAccountOperationsEventsAsync(accountNumber: AccountNumber): CompletableFuture<List<EventDTO>> {
        val accountOperationsAsync = CompletableFuture.supplyAsync {
            val events = eventStore.readEvents(accountNumber.value)
            events.asStream()
                .filter { it.payload is FundsOperationEvent }
                .map { EventDTO(it.payload, it.payloadType.simpleName, it.timestamp) }
                .sorted { a, b -> b.timestamp.compareTo(a.timestamp) }
                .collect(Collectors.toList())
        }
        return fetchOrThrowsNotFound(accountOperationsAsync)
    }

    private fun <T> fetchOrThrowsNotFound(promise: CompletableFuture<T>): CompletableFuture<T> {
        return promise.thenApplyAsync {
            if (it == null) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }
            it
        }
    }
}