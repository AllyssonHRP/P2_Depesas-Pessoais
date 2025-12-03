# 💰 Sistema de Controle de Despesas Pessoais

Este é um aplicativo desktop desenvolvido em **Java** com interface gráfica **JavaFX**, projetado para gerenciar despesas pessoais. O sistema utiliza o framework **Hibernate (JPA)** para persistência de dados em um banco **PostgreSQL**.

## 📋 Funcionalidades

* **Adicionar Despesa:** Registro de novas despesas com descrição, valor, data e categoria.
* **Listar Despesas:** Visualização de todas as despesas cadastradas em uma tabela.
* **Atualizar Despesa:** Edição de registros existentes.
* **Excluir Despesa:** Remoção de despesas do banco de dados.
* **Cálculo de Total:** Exibição dinâmica do valor total das despesas.

## 🛠️ Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes tecnologias e versões:

* **Java JDK:** 25 (Configurado no `pom.xml`, mas compatível com JDK 21+)
* **JavaFX:** 21.0.6 (Interface Gráfica)
* **Hibernate Core:** 6.x (JPA / ORM)
* **PostgreSQL Driver:** 42.7.8
* **Lombok:** 1.18.42 (Para redução de código boilerplate)
* **Maven:** Gerenciamento de dependências e build

## ⚙️ Pré-requisitos e Configuração

### 1. Banco de Dados
O sistema está configurado para conectar a um banco de dados PostgreSQL local. Antes de executar, você precisa garantir que o banco exista.

1.  Tenha o **PostgreSQL** instalado e rodando.
2.  Crie um banco de dados vazio chamado `db-java` (conforme definido no seu `persistence.xml`).
3.  As credenciais padrões configuradas são:
    * **Usuário:** `postgres`
    * **Senha:** `postgres`

> **Nota:** Se o seu usuário ou senha forem diferentes, altere o arquivo `src/main/resources/META-INF/persistence.xml`.

### 2. Comportamento da Tabela
Atualmente, o projeto está configurado com a propriedade `hbm2ddl.auto` como `create`.
* Isso significa que **toda vez que o app for reiniciado, o banco será recriado do zero (dados perdidos)**.
* Para manter os dados salvos entre as execuções, altere a linha no `persistence.xml` para:
    ```xml
    <property name="jakarta.persistence.schema-generation.database.action" value="update" />
    <property name="hibernate.hbm2ddl.auto" value="update" />
    ```

## 🚀 Como Executar

### Via IntelliJ IDEA
1.  Aguarde o Maven baixar todas as dependências.
2.  Localize a classe `org.p2_despesas.Principal.Launcher`.
3.  Execute o método `main` desta classe (`Launcher`).
    * *Dica:* Executar pelo `Launcher` evita erros de módulos do JavaFX que ocorrem ao rodar diretamente o `App.java`.

### Via Linha de Comando (Maven)
Na raiz do projeto, execute:

```bash
mvn clean javafx:run
