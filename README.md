# Error mapping

Маппинг ошибок


### Настройки

Добавить в `pom.xml` в зависимости

```
<dependency>
    <groupId>dev.vality</groupId>
    <artifactId>error-mapping-java</artifactId>
    <version>${error-mapping-java.version}</version>
</dependency>
```

и в `application.yml`

```
error-mapping:
  file: classpath:fixture/errors.json
  patternReason: "'%s' - '%s'" # 'code' - 'description'
```

`file` - путь к файлу с необходимой структурой для маппинга ошибок

`patternReason` - формат шаблона, по умолчанию '%s' - '%s'


### Настройки перед использованием

Добавляем в приложение конфигурационный файл:
Если хотим, чтобы при запуске проверялись ошибки на корректность, то добавляем `validateMapping()`

```
@Configuration
public class ErrorMappingConfiguration {

    @Value("${error-mapping.file}")
    private Resource filePath;

    @Value("${error-mapping.patternReason:\"'%s' - '%s'\"}")
    private String patternReason;

    @Bean
    ErrorMapping errorMapping() throws IOException {
        ErrorMapping errorMapping = new ErrorMapping(filePath.getInputStream(), patternReason);
        errorMapping.validateMapping();
        return errorMapping;
    }

}
```


### Использование

Подключаем как зависимость

```
@Autowired
private final ErrorMapping errorMapping;
```

в коде вызываем:
```
errorMapping.mapFailure(code)
errorMapping.mapFailure(code, description)
errorMapping.mapFailure(code, description, state)
```
и получаем трифтовую структуру Failure

### Пример структуры файла маппинга ошибок (errors.json)

```
[
  {
    "codeRegex": "001",
    "mapping": "authorization_failed:operation_blocked"
  },
  {
    "codeRegex":"002",
    "descriptionRegex":"Invalid .*",
    "mapping":"authorization_failed:operation_blocked"
  },
  {
    "codeRegex":"003",
    "descriptionRegex":"Invalid cardholder",
    "mapping":"authorization_failed:operation_blocked",
    "state": "payment"
  },
  {
    "codeRegex":"004",
    "descriptionRegex":"Timeout",
    "mapping":"ResultUnknown"
  }
]

```


### Возможные ошибки:

```
preauthorization_failed
rejected_by_inspector

authorization_failed:unknown
authorization_failed:merchant_blocked
authorization_failed:operation_blocked
authorization_failed:account_not_found
authorization_failed:account_blocked
authorization_failed:account_stolen
authorization_failed:insufficient_funds

authorization_failed:security_policy_violated
authorization_failed:temporarily_unavailable
authorization_failed:rejected_by_issuer

authorization_failed:account_limit_exceeded:amount
authorization_failed:account_limit_exceeded:number
authorization_failed:account_limit_exceeded:unknown

authorization_failed:provider_limit_exceeded:amount
authorization_failed:provider_limit_exceeded:number
authorization_failed:provider_limit_exceeded:unknown

authorization_failed:payment_tool_rejected:unknown
authorization_failed:payment_tool_rejected:bank_card_rejected:card_expired
authorization_failed:payment_tool_rejected:bank_card_rejected:card_number_invalid
authorization_failed:payment_tool_rejected:bank_card_rejected:card_holder_invalid
authorization_failed:payment_tool_rejected:bank_card_rejected:cvv_invalid
authorization_failed:payment_tool_rejected:bank_card_rejected:card_unsupported
authorization_failed:payment_tool_rejected:bank_card_rejected:issuer_not_found

ResourceUnavailable
ResultUnknown
ResultUnexpected
```
