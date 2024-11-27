# Farmácia Online (Fármio)

<img alt="banner fármio" width="400" src="./src/main/resources/static/banner.jpeg" title="Fármio Farmácia Online"/>

#### Bibliotecas
- Spring Boot
- Spring JPA Data
- Spring Web
- Spring Validation
- Flyway
- Postgres
- Docker, docker-compose
- Lombok [👎](https://www.danvega.dev/blog/no-lombok)
- Java 17
- Spring na versão 4


### Versão 1
- Spring Web expondo endpoints Restfull
- CRUD Completo (CREATE, READ, UPDATE, DELETE)
- Validação com Bean Validation
- Paginação e ordenação de pesquisas

### Versão 2
- Boas práticas de Rest
- Tratamento de erros ao usuário
- Controle de acesso com JWT
- Regras de negócios específicas (usam o crud)
- Documentação da API
- Testes unitários e automatizados
- Build do projeto para um servidor cloud


## Passo a passo da implementação

#### void main
Serve apenas para subir o Tomcat embutido no Spring

```java
@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
```

#### POST Controller
Controller tem métodos do Spring MVC "não tem nada a ver" com o Boot por isso não esta linkado com o void main.

```java
@RestController
@RequestMapping("save")
public class CustomerController {

	@PostMapping
	public void cadastrar(@RequestBody CustomerData json) {
		System.out.println(json);
	}
}
```


#### DTO Data Tranfer Object
DTO para guardar os campos corretos do JSON, muito usado no spring

Usamos aqui **record** uma classe implementada no **Java 16** que facilita esta operação.

- Record ou Lombok para criar as entidades?
- ["Qual o melhor, Lombok ou Records? - Giuliana Bezerra"](https://www.youtube.com/watch?v=95TFOpIIz_I)
- Como dica fica records para DTOS e lombok para as entidades


```java
public record AddressData(String street, String apartment, String number, String city, String state) {

	// String numero ou int numero?
}

public record CustomerData(String name, String email, AddressData address) {

	/*
	 -- construtor compacto
	public CustomerData {
		
	
	}
	*/
}

# Json
{
    "name": "Leo Dias",
    "email": "leo@dias.com",
    "address": {
        "street": "Rua XV",
        "number": "123",
        "city": "São Paulo",
        "state": "SP"
    }
}
```

### Database with docker-compose

#### PostgreSQL application.properties
```properties
spring.application.name=api
spring.datasource.url=jdbc:postgresql://localhost:5432/principal
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driverClassName: org.postgresql.Driver
```

#### PostgreSQL Dependecies
```xml
<dependency>
	<groupId>org.flywaydb</groupId>
	<artifactId>flyway-core</artifactId>
</dependency>
<dependency>
	<groupId>org.flywaydb</groupId>
	<artifactId>flyway-database-postgresql</artifactId>
</dependency>
<dependency>
	<groupId>org.postgresql</groupId>
	<artifactId>postgresql</artifactId>
	<scope>runtime</scope>
</dependency>		
```

#### PostgreSQL docker-compose.yml
```yml
version: "3.9"

services:
  postgres:
    container_name: postgres
    image: postgres:latest
    environment:
      POSTGRES_PASSWORD: postgres
      POSTGRES_USER: postgres
      POSTGRES_DB: principal
    volumes:
      - ./devops/postgresql/data:/var/lib/postgresql/data # create local folder if not exists
    ports:
      - "5432:5432"
    networks:
      - app-network
      
networks:
  app-network:
    driver: bridge
```

#### PostgreSQL docker comands to run terminal
```bash
#>   Run local
# docker-compose up -d
# docker-compose logs -f
# docker exec -it <docker-container-id> /bin/bash
# psql -h localhost -U postgres
# postgres=# 	\l+ 		#show databases
# postgres=# 	SELECT datname FROM pg_database;
# postgres=# 	\c principal 		# switching databases 
# principal=# 	CREATE TABLE leads (id INTEGER PRIMARY KEY, name VARCHAR);
# principal=# 	\dt # listing tables
# principal=# \d+ pharmacist   # DESCRIBE TABLES
# principal=# 	INSERT INTO leads VALUES (1, 'Joe'), (2,'Rebecca'), (3,'Smith');
# principal=# 	SELECT * FROM leads;
```

### Spring JPA Data + Lombok

#### JPA Mapping
```java
// Jpa methods
@Table(name = "pharmacist")
@Entity(name = "Pharmacist")
// Lombok metods
@Getter
@NoArgsConstructor // jpa exige
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pharmacist {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	private int licenseNumber;
	
	@Enumerated(EnumType.STRING)
	private Type type;
	
	@Embedded
	private Address address;
}

```

#### Spring JPA Data - Mapping repository
```java
# Não precisamos criar um Dao, mapear o repository, Spring nos ajuda nisso.
public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {
	// <Bean of repository, type of identifiers>  <Pharmacist, Long>
}
```

#### Install Lombok no Eclipse ou STS
```text
# Modo 1
Eclipse > Menu Superior > Help > Install New Software
Put url Exported Repository - https://projectlombok.org/p2 and ADD.
Selecione o Lombok e mande instalar, reinicie o sistema.


# Modo 2
Instalando o lombok no eclipse
Abra o cmd como administrador.
Digite: cd C:\Users\seu_nome_de_usuario\.m2\repository\org\projectlombok\lombok\1.18.24
Digite: java -jar lombok-1.18.24.jar
Na tela de instalação do lombok selecione specify location vá até a pasta do eclipse e selecione eclipse.ini.
No arquivo eclipse.ini digite no fim da lista -vmargs javaagent:C:\caminho-do-eclipse-na-sua-máquina\lombok.jar , salve a alteração.
Dê um quit na tela de instalação do lombok. OK! lombok instalado com sucesso!

Avoid this error:
Error:
g.hibernate.InstantiationException: Unable to locate constructor for embeddable 'com.farmio.api.usuario.Address'
	at org.hibernate.metamodel.internal.EmbeddableInstantiatorPojoStandard.instantiate(EmbeddableInstantiatorPojoStandard.java:58) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
	at org.hibernate.type.ComponentType.deepCopy(ComponentType.java:483) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
```


### Flyway

Flyway possuiu uma nomenclatura específica para rodar automaticamente no build do Spring Boot

**DUPLO UNDERLINES: V1__.SQL !!**
> /api/src/main/resources/db/migration/V1__create-table-pharmacist.sql

- *POSTGRES SQL*

```sql
-- Comando para criar a tabela Pharmacist
CREATE TABLE Pharmacist (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    licenseNumber INT NOT NULL,
    type VARCHAR(20) NOT NULL, -- Alterado para VARCHAR, pois ENUM não é diretamente suportado no PostgreSQL
    address_street VARCHAR(255) NOT NULL,
    address_number VARCHAR(20) NOT NULL,
    address_city VARCHAR(100) NOT NULL,
    address_state CHAR(2) NOT NULL,
    CONSTRAINT type_constraint CHECK (type IN ('SIMPLE', 'PRESCRIPTION', 'OVERTHECOUNTER')) -- Restrição de tipo
);

-- Comando para inserir os dados na tabela Pharmacist
INSERT INTO Pharmacist (name, licenseNumber, type, address_street, address_number, address_city, address_state)
VALUES ('Nome do Farmacêutico', 123456, 'SIMPLE', 'Rua Exemplo', '123', 'Cidade Exemplo', 'UF');
```

#### Test in *{docker-compose logs -f}*
 
```text
docker ps -a
docker exec -it <docker-container-id> /bin/bash
psql -h localhost -U postgres
postgres=# \c principal     # USE/ENTER INTO DATABASE
You are now connected to database "principal" as user "postgres".
principal-# \dt					# LIST TABLES
                 List of relations
 Schema |         Name          | Type  |  Owner   
--------+-----------------------+-------+----------
 public | flyway_schema_history | table | postgres
 public | pharmacist            | table | postgres
(2 rows)

principal=# SELECT * FROM pharmacist;
 id |         name         | licensenumber |  type  | address_street | address_number |  address_city  | address_state 
----+----------------------+---------------+--------+----------------+----------------+----------------+---------------
  1 | Nome do Farmacêutico |        123456 | SIMPLE | Rua Exemplo    | 123            | Cidade Exemplo | UF
(1 row)
```

#### @Transactional

Adicione ao seu método save/recorder/cadastrar a anotação Transactional do Spring para identificar que ele deve gerar transacionar/alterar/enviar dados ao banco.

```java
import org.springframework.transaction.annotation.Transactional;

	@PostMapping
	@Transactional
	public void recorder(@RequestBody CustomerData json) {
		repository.save(new Pharmacist(json));
	}

```

### Deletar hardcoded manualmente no banco, sem recriar com Flyway.

- **Não se deve deletar manualmente**, e sim criar versões de alterações usando Flyway.
- Flyway cria os campos com lowercase no banco de dados!!!
- Depois de rodado a migration/inicializado, não pode alterar nada no arquivo sql, nem comentários (crie versões!).

```bash
psql -h localhost -U postgres
postgres=# \c principal
principal=# SELECT * FROM pharmacist;
principal=# DROP TABLE pharmacist;
principal=# SELECT * FROM flyway_schema_history;
principal=# DELETE FROM flyway_schema_history where installed_rank = 1;
principal=# \d+ pharmacist
```


### Bean Validation

#### Certifique que tem esse plugin que fornece o bean validation

Bean validation é uma especificação do java para validar campos.

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### @Valid Adicione em todos os campos de classe

```java
public void recorder(@RequestBody @Valid CustomerData json) {
	repository.save(new Pharmacist(json));
}

public record CustomerData(
		@NotBlank // notblank only for string
		String name,
		....	
		@Valid  // valid with class java validation!
		TypeMedication type_medication,
		
		@NotNull
		@Valid
		AddressData address
) {
	
```

#### ALTER TABLE with FlyWay

- Uma vez criado o banco não podemos alterar a versão 1
- Crie outro arquivo que inicie com V2 e faça alter tables.

```sql
ALTER TABLE pharmacist ADD COLUMN phone VARCHAR(20);
ALTER TABLE pharmacist RENAME COLUMN phone TO phone_number;

```

#### Json Example

```json
{
    "name": "Leo Dias",
    "email": "leo@dias.com",
    "license_work": "12ab",
    "type_medication": "SIMPLE",
    "address": {
        "street": "Rua XV",
        "apt": null,
        "num": 123,
        "city": "São Paulo"
    }
}

```


#### Get filtering fields
```java
@GetMapping
public List<PharmacistDataList> listData() {
	// Converte stream to Pharmacist Object
	return repository.findAll().stream()
			.map(ph -> new PharmacistDataList(ph.getId(), ph.getName(), ph.getLicense_work()))
			.collect(Collectors.toList());
}

//New file record class
public record PharmacistDataList(
		Long id,
		String name, String license_work
) {
}	
```

#### Adding Pageable to GET
- Pageable já tem stream, map e toList não precisa ser adicionado!!
- Altere o retorno de List para Page

```java
import org.springframework.data.domain.Pageable;

//Full edit of controller
@GetMapping
public Page<PharmacistDataList> listData(Pageable pages) {
	// Converte stream to Pharmacist Object
	return repository.findAll(pages)
			.map(ph -> new PharmacistDataList(ph.getId(), ph.getName(), ph.getLicense_work()));
}

```

#### Test Pageable
- Param são opcionais e podem ser configurados por @PageableDefault
- Se quiser renomear sort, size para portugues edite no arquivo application.properties
- Ative log to Pageable

```java
// http://localhost:8080/pharmacist?sort=name,desc&size=2
// app.properties can be edit size to tamanho or sort to filtro
public Page<PharmacistDataList> listData(@PageableDefault(size = 5) Pageable pages) ....

// Add to properties
# Show log for each sql query (Pageable)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```


### PUT and DELETE operations

#### PUT

```java
// Mapping controller
@PutMapping
@Transactional
public void updateRec(@RequestBody @Valid PharmacistDataUpdate json) {
	var ref = repository.getReferenceById(json.id());
	ref.updateRec(json);
}

// Novo Record
public record PharmacistDataUpdate(
	@NotNull Long id, String name, String license_work, AddressData address) {}

// Novo método no Bean Pharmacist.java
public void updateRec(PharmacistDataUpdate dto) {
	System.out.println(dto.name());
	if (dto.name() != null) {
		this.name = dto.name();
	}
	this.address = new Address(dto.address());
}
```


#### DELETE
- Exclusão lógica, não apagamos realmente do banco apenas mudamos para desativado.

```java
# your controller
@DeleteMapping("/{id}")
@Transactional
public void removeRec(@PathVariable Long id) {
	// repository.deleteById(id); // Hard Delete
	
	var ref = repository.getReferenceById(id);
	ref.deleteRec();// Soft Delete
}

# inside your bean
public void deleteRec() {
	this.active = false;

}
```


#### FIND BY (custom funcion jparepository)

- Se queremos filtrar apenas os ativos que são True podemos reimplementar a classe findAll, seguindo um padrão!
- *findAllBy <my field is> <expected value>*

```java
public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {

	// findAllBy <my field is> <expected value>
	Page<Pharmacist> findAllByActiveTrue(Pageable pages);
	// <Bean of repository, type of identifiers>  <Pharmacist, Long>
}

# use in controller
@GetMapping("/active")
public Page<PharmacistDataList> listTrueRec(@PageableDefault Pageable pages) {
	// Converte stream to Pharmacist Object
	return repository.findAllByActiveTrue(pages).map(ph -> new PharmacistDataList(ph.getId(), ph.getName(),
			ph.getLicense_work(), ph.getActive(), ph.getAddress()));
}
```

# Versão 2 - Continuação


#### Plugins úteis no Eclipse

**Eclipse > Help > Install new software > Add url**
- [Lombok](https://projectlombok.org/p2)
- [SmartSave](updatesite/master/com.laboki.eclipse.updatesite.smartsave)
- [Egit](https://download.eclipse.org/egit/updates)


## Tratamento de erros

**Categoria de códigos - Verbos HTTP**

Os códigos HTTP possuem três dígitos, sendo que o primeiro dígito indica a classificação dentro de cinco categorias.

1XX: Informativo – solicitação aceita ou em andamento;

2XX: Confirmação – ação concluída ou entendida;

3XX: Redirecionamento – algo mais precisa ser feito para completar a solicitação;

4XX: Erro do cliente – solicitação não pode ser concluída ou contém sintaxe incorreta;

5XX: Erro no servidor – servidor falhou ao concluir a solicitação.

[HTTP Cats](site1) e [HTTP Dogs](site2).


#### Lidando com erros no HTTP

```java
// Retornar 404 para id não encontrado e não 500 erro de servidor
// Não precisa alterar nada nos controllers
// No Build o Spring vai carregar essa classe e notfoundexception será retornado como notfound
package com.farmio.api.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorCustomizer {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tryError404() {
        return ResponseEntity.notFound().build();
    }
}
```


#### Se tentar inserir um dado, retornar erros com quais campos não foram preenchidos
```java
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tryError400(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        var listErrors = errors.stream().map(DataErrorCustomizer::new).toList();
        return ResponseEntity.badRequest().body(listErrors);
    }

    private record DataErrorCustomizer(String field, String message) {
        private DataErrorCustomizer(FieldError er) {
            this(er.getField(), er.getDefaultMessage());
        }
    }
```

## Spring Security
- Autenticação
- Autorização e Controle de Acesso
- Proteção contra ataques
- Web autentica no modo Stateful (sessão única), API Rest é Stateless (não guarda sessão, memória)
- JWT tokens para gerenciar a permissão de acesso

**Apenas adicionando ao pom.xml as dependências do Spring Security faz BLOQUEAR TODAS AS REQUISIÇÕES
retornando sempre 401 Unauthorized para todas as chamadas!**

````text
Quando acessar via navegador ele mostra uma tela de login
Login: http://localhost:8080/pharmacist?continue
User: user
Senha: gerada pelo console da IDE
Bcrypt: $2a$12$3pvLYdnNDSRwz3cS2pFIZ.1fXJ1Be2pHaH4L0Cio2cVl2rSdViC4q

Quando roda pela primeira vez ele gera uma senha de desenvolvimento!
# Using generated security password:    32cebb8e-27f1-49f6-8cd4-209f8722c132
# This generated password is for development use only.
````

```xml
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
    </dependency>
```

### Nova migration/tabela no banco

Nova migration no banco para guardar usuario e senha de login.

Mexeu na migration precisa parar o projeto e rodar novamente.
```java
@Table(name = "logins")
@Entity(name = "logins")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Logins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String user;
    String pass;
}
```
    
```sql
# resources\db\migration\V3__create-table-logins.sql
CREATE TABLE Logins (
    id SERIAL PRIMARY KEY,```
    login VARCHAR(255) NOT NULL,
    pass VARCHAR(255) NOT NULL
);

INSERT INTO Logins (login, pass) VALUES ('admin', 'admin');

# Logs
# ... restartedMain] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "3 - create-table-logins"
# ... restartedMain] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema "public", now at version v3 (execution time 00:00.307s)
```

#### Config de auth
- *UserDetailsService* é a classe do Spring Security que o spring chama para fazer o login baseado no formulario.
                                                                                                                       
```java
@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private LoginsRepository repository; //injeta o repository para consulta

    // S.Security tem esse metodo padrao para login e senha, quando se acessa aquela página html
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }
}
```

- *@Configuration* classe de configurações de segurança

```java
// Mapping Table
public class Logins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String pass;
}

// Config to security
@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    // Stateless
    // Cross Site Request Forge - Disable porque nosso token já verifica isso
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
         return httpSecurity.csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}

// Service de login no Spring é identificado por UserDetailsService
@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private LoginsRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }
}
```


#### Pagina para gerar token

- *AuthenticationManager* classe que permite acessar o service, já que não devemos acessá-lo diretamente.

```java
// Se tentar injetar assim ele vai dar erro é necessário configurar
//numa classe @Configuration
@Autowired
private AuthenticationManager manager;
//Inclua este método e o spring sabera fazer o autowired
@Bean
public AuthenticationManager managerConfig(AuthenticationConfiguration config) throws Exception {```
    return config.getAuthenticationManager();
}
```
   
#### Consulte e insira uma senha no estilo bcrypt
```sql
\l+
\c principal
\dt
\d+ logins
SELECT * FROM logins; 
INSERT INTO Logins (login, pass) VALUES  ('admin', '$2a$12$3pvLYdnNDSRwz3cS2pFIZ.1fXJ1Be2pHaH4L0Cio2cVl2rSdViC4q');
```
    
- Alterações nas classes para criptografia

```java
// Logins.java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {```
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
}

@Override
public String getPassword() {
    return pass;
}

@Override
public String getUsername() {
    return login;
}

// Ativa o decrypt ao receber uma senha simples e verificar no banco uma senha criptada
@Bean
public PasswordEncoder encoder(){
    return new BCryptPasswordEncoder();
}

// Record simples
public record DataAuth(String login, String pass) { }
```

- Controller de acesso via API

```java
@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @PostMapping
    public ResponseEntity goLogin(@RequestBody @Valid DataAuth dataAuth) {
        var token = new UsernamePasswordAuthenticationToken(dataAuth.login(), dataAuth.pass());
        var authon = manager.authenticate(token);

        return ResponseEntity.ok().build();
    }
}
```
