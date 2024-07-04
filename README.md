# Farmácia Online (Fármio)


#### Bibliotecas
- Spring Boot
- Spring JPA Data
- Spring Web
- Spring Validation
- Flyway
- Postgres
- Docker, docker-compose
- Lombok
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

```
@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
```

#### POST Controller
Controller tem métodos do Spring MVC "não tem nada a ver" com o Boot por isso não esta linkado com o void main.

```
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

Usamos aqui **record** uma classe implementada no Java 16 que facilita esta operação.

```
public record AddressData(String street, String apartment, String number, String city, String state) {

	// String numero ou int numero?
}

public record CustomerData(String name, String email, AddressData address) {

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
```
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
```
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
```
# Não precisamos criar um Dao, mapear o repository, Spring nos ajuda nisso.
public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {
	// <Bean of repository, type of identifiers>  <Pharmacist, Long>
}
```

#### Install Lombok no Eclipse ou STS
```
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

```
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
 
```
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

```
import org.springframework.transaction.annotation.Transactional;

	@PostMapping
	@Transactional
	public void recorder(@RequestBody CustomerData json) {
		repository.save(new Pharmacist(json));
	}

```



### Bean Validation

#### Certifique que tem esse plugin que fornece o bean validation

Bean validation é uma especificação do java para validar campos.

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### Deletar direto do banco para recriar com FlyWay.

- Flyway cria os campos com lowercase!!!
- Depois de rodado a migration/inicializado, não pode alterar nada no arquivo sql, nem comentários.

```
psql -h localhost -U postgres
postgres=# \c principal
principal=# SELECT * FROM pharmacist;
principal=# DROP TABLE pharmacist;
principal=# SELECT * FROM flyway_schema_history;
principal=# DELETE FROM flyway_schema_history where installed_rank = 1;
principal=# \d+ pharmacist
```

#### @Valid Adicione em todos os campos de classe

```
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

```
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
```
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

```
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

```
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

```
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

```
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


#### 
```
Categoria de códigos
Os códigos HTTP (ou HTTPS) possuem três dígitos, sendo que o primeiro dígito significa a classificação dentro das possíveis cinco categorias.

1XX: Informativo – a solicitação foi aceita ou o processo continua em andamento;

2XX: Confirmação – a ação foi concluída ou entendida;

3XX: Redirecionamento – indica que algo mais precisa ser feito ou precisou ser feito para completar a solicitação;

4XX: Erro do cliente – indica que a solicitação não pode ser concluída ou contém a sintaxe incorreta;

5XX: Erro no servidor – o servidor falhou ao concluir a solicitação.

Principais códigos de erro
Como dito anteriormente, conhecer os principais códigos de erro HTTP vai te ajudar a identificar problemas em suas aplicações, além de permitir que você entenda melhor a comunicação do seu navegador com o servidor da aplicação que está tentando acessar.

Error 403
O código 403 é o erro “Proibido”. Significa que o servidor entendeu a requisição do cliente, mas se recusa a processá-la, pois o cliente não possui autorização para isso.

Error 404
Quando você digita uma URL e recebe a mensagem Error 404 , significa que essa URL não te levou a lugar nenhum. Pode ser que a aplicação não exista mais, a URL mudou ou você digitou a URL errada.

Error 500
É um erro menos comum, mas de vez em quando ele aparece. Esse erro significa que há um problema com alguma das bases que faz uma aplicação rodar. Esse erro pode ser, basicamente, no servidor que mantém a aplicação no ar ou na comunicação com o sistema de arquivos, que fornece a infraestrutura para a aplicação.

Error 503
O erro 503 significa que o serviço acessado está temporariamente indisponível. Causas comuns são um servidor em manutenção ou sobrecarregado. Ataques maliciosos, como o DDoS, causam bastante esse problema.

Uma dica final: dificilmente conseguimos guardar em nossa cabeça o que cada código significa, portanto, existem sites na internet que possuem todos os códigos e os significados para que possamos consultar quando necessário. Existem dois sites bem conhecidos e utilizados por pessoas desenvolvedoras, um para cada preferência: se você gosta de gatos, pode utilizar o HTTP Cats ; já, se prefere cachorros, utilize o HTTP Dogs .
```
































<!--
####
```

```
!-->
