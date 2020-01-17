export default class AccountAPI {

    constructor(httpClient) {
        this.httpClient = httpClient;
    }

    create(body) {
        return this.httpClient.post('/account', body)
            .then(r => r.data.accountNumber);
    }

    dashboard(accountNumber) {
        return this.httpClient.get(`/account/${accountNumber}`).then(r => r.data);
    }

    addCpf(accountNumber, cpf) {
        return this.httpClient.post(`/account/${accountNumber}/add-cpf`, { cpf });
    }

    deposit(accountNumber, value) {
        return this.httpClient.post(`/account/${accountNumber}/deposit`, { value });
    }

    withdraw(accountNumber, value) {
        return this.httpClient.post(`/account/${accountNumber}/withdraw`, { value })
    }
}
