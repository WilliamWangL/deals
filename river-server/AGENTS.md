# 后端规范 - Java 17 + Spring Boot 3.5

基于 ruoyi-vue-pro 框架开发。

## 构建命令

```bash
# 构建所有模块
mvn clean compile

# 构建指定模块
mvn clean compile -pl river-server -am

# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=TenantServiceImplTest -pl river-module-system

# 运行单个测试方法
mvn test -Dtest=TenantServiceImplTest#testCreateTenant -pl river-module-system

# 跳过测试构建
mvn clean package -DskipTests

# 启动应用
mvn spring-boot:run -pl river-server
```

## 包结构

```
com.river.module.{module}/
├── controller/
│   ├── admin/          # 管理后台 API（需要认证）
│   └── app/            # 公开 API
├── service/
│   ├── {Name}Service.java      # 接口
│   └── {Name}ServiceImpl.java  # 实现
├── dal/
│   ├── dataobject/     # DO 类（数据库实体）
│   └── mysql/          # Mapper 接口
├── convert/            # MapStruct 转换器
└── enums/              # 模块专用枚举
```

## 命名规范

| 类型 | 模式 | 示例 |
|------|------|------|
| DO（实体） | `{Name}DO` | `DealDO`, `BrandDO` |
| VO（请求） | `{Name}SaveReqVO`, `{Name}PageReqVO` | `DealSaveReqVO` |
| VO（响应） | `{Name}RespVO` | `DealRespVO` |
| Service | `{Name}Service` / `{Name}ServiceImpl` | `DealService` |
| Controller | `{Name}Controller` | `DealController` |
| Mapper | `{Name}Mapper` | `DealMapper` |
| Convert | `{Name}Convert` | `DealConvert` |

## 类注解模板

```java
// DO 类
@TableName("river_deal")
@KeySequence("river_deal_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealDO extends BaseDO { }

// Service
@Service
@Validated
@Slf4j
public class DealServiceImpl implements DealService { }

// Controller
@Tag(name = "管理后台 - 优惠管理")
@RestController
@RequestMapping("/admin-api/affiliate/deal")
@Validated
public class DealController { }
```

## 依赖注入

- 使用 `@Resource` 进行字段注入（Jakarta 标准）
- 必需依赖使用构造函数注入

## 返回类型

- Controller 返回 `CommonResult<T>`
- 使用 `CommonResult.success(data)` 和 `CommonResult.error(code, message)`

## 参数校验

- 在请求体上使用 `@Valid`
- 使用 Jakarta 校验注解：`@NotNull`, `@NotBlank`, `@Size` 等

## 错误处理

```java
// 在 ErrorCodeConstants 中定义
ErrorCode DEAL_NOT_FOUND = new ErrorCode(1_001_000_001, "优惠不存在");

// 抛出异常
throw exception(DEAL_NOT_FOUND);
```

业务逻辑中禁止使用原始异常，必须使用错误码。

## 测试

- 数据库测试继承 `BaseDbUnitTest`
- Mock 测试继承 `BaseMockitoUnitTest`
- 使用 `@MockitoBean` 模拟依赖
- 使用 `randomPojo()` 生成测试数据
