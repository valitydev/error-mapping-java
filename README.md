# Error mapping

Маппинг ошибок


### Настройки

Добавить в `pom.xml` в зависимости

```
<dependency>
    <groupId>com.rbkmoney</groupId>
    <artifactId>error-mapping-java</artifactId>
    <version>1.0.0</version>
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
Если хотим, чтобы при запуске проверялись ошибки на корректность, то добавляем `validateMappingFormat()`

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
        errorMapping.validateMappingFormat();
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
errorMapping.getFailureByCodeAndDescription(code, description)
```
и получаем трифтовую структуру Failure



### Пример структуры файла маппинга ошибок (errors.json)

```
[
  {
    "code":"001",
    "description":"Unsupported version",
    "regexp":"001|Unsupported version",
    "mapping":"ResultUnexpected"
  },
  {
    "code":"008",
    "description":"Timeout",
    "regexp":"008|Timeout",
    "mapping":"ResultUnknown"
  },
  {
    "code":"203",
    "description":"Invalid amount",
    "regexp":"203|Invalid amount",
    "mapping":"authorization_failed:operation_blocked"
  },
  {
    "code":"204",
    "description":"Cannot process transaction",
    "regexp":"204|Cannot process transaction",
    "mapping":"authorization_failed:unknown"
  },
  {
    "code":"205",
    "description":"Insufficient funds",
    "regexp":"205|Insufficient funds",
    "mapping":"authorization_failed:insufficient_funds"
  },
  {
    "code":"301",
    "description":"Expired card",
    "regexp":"301|Expired card",
    "mapping":"authorization_failed:payment_tool_rejected:bank_card_rejected:card_expired"
  },
  {
    "code":"304",
    "description":"Transaction not supported by institution",
    "regexp":"304|Transaction not supported by institution",
    "mapping":"authorization_failed:operation_blocked"
  },
  {
    "code":"305",
    "description":"Lost or Stolen card",
    "regexp":"305|Lost or Stolen card",
    "mapping":"authorization_failed:account_stolen"
  },
  {
    "code":"310",
    "description":"Amount over maximum",
    "regexp":"310|Amount over maximum",
    "mapping":"authorization_failed:account_limit_exceeded:amount"
  },
  {
    "code":"311",
    "description":"Number of PIN tries exceeded",
    "regexp":"311|Number of PIN tries exceeded",
    "mapping":"preauthorization_failed"
  },
  {
    "code":"334",
    "description":"Transaction timeout",
    "regexp":"334|Transaction timeout",
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
