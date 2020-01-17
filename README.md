# Garotade APP - Event Sourcing Exemplo

Essa aplicação é utilizada como exemplo na minha [apresentação Event Sourcing](https://pt.slideshare.net/WellingtonGustavoMac/event-sourcing-221002578).

## Artefatos
- `docker-compose.yml`: Arquivo responsável por orquestrar o deploy e execução das aplicações 
- `garotadebank` (diretório): Projeto Kotlin que utiliza Spring Boot para implementar uma API rest e o AXON que toma conta do core do sistema para implementar o Event Sourcing em si.
- `garotadebank-frontend` (diretório): Frontend da aplicação desenvolvida em React. 

## Tecnologias utilizadas no exemplo (não são todas necessárias para execução do exemplo)

- [Kotlin](https://kotlinlang.org/)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com)
- [Docker Compose](https://docs.docker.com/compose/)
- [Spring](https://spring.io)
- [Spring Boot](https://projects.spring.io/spring-boot/)
- [AXON](https://axoniq.io/)

## Como executar

**Obs:** Necessário ter apenas **Docker** e **Docker Compose** instalados para execução.

```shell
docker-compose up -d --build
```

### Clonar o projeto

```shell
git clone https://github.com/wellington3110/garotade-app.git
```

### Acesso

Para utilizar a aplicação a forma mais fácil é utilizando o frontend da aplicação:

- **Front end**
    - [http://localhost:3000/account](http://localhost:3000/account): Entrypoint para a aplicação frontend