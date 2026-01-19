# AGENTS.md - river-server

本文件为 river-server（Java 后端）开发指南，供 AI Agent 使用。

## 常用命令

```bash
cd river-server

# 编译项目
mvn clean compile

# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=ClassName

# 运行单个测试方法
mvn test -Dtest=ClassName#methodName

# 跳过测试打包
mvn clean package -DskipTests

# 运行服务器（默认端口 48080）
mvn spring-boot:run

# 仅编译某个模块
cd river-module-xxx && mvn clean compile
```

## 测试规范

### 测试基类

| 基类 | 用途 |
|------|------|
| `BaseDbUnitTest` | 需要数据库的单元测试 |
| `BaseMockitoUnitTest` | 使用 Mockito 的纯单元测试 |
| `BaseRedisUnitTest` | 需要 Redis 的单元测试 |
| `BaseDbAndRedisUnitTest` | 需要数据库和 Redis 的测试 |

### 测试类命名

- 命名规范：`{ServiceName}ImplTest.java`
- 位置：`src/test/java/` 目录

### 测试示例

```java
public class ExampleServiceImplTest extends BaseDbUnitTest {

    @InjectMocks
    private ExampleServiceImpl service;

    @Mock
    private ExampleMapper mapper;

    @Test
    void testMethod() {
        // given
        // when
        // then
    }
}
```

## 代码风格

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | PascalCase | `UserServiceImpl` |
| 方法名/变量名 | camelCase | `getUserById` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 包名 | 全小写 | `com.river.module.affiliate` |

### 导入顺序

1. 静态导入（import static）
2. JDK 包（java.*, javax.*）
3. Spring 包（org.springframework.*）
4. Lombok/其他库
5. 项目内部（com.river.*）

**禁止**：使用 `*` 通配符导入

### 类注释

- 每个类必须添加类注释（DocComment）
- 字段注释使用 Javadoc `@param`、`@return`、`@see`

### 类型安全（硬性禁止）

```java
// 禁止
as any
@ts-ignore
@ts-expect-error
catch(e) {}  // 空捕获块

// 必须
try {
    // 处理逻辑
} catch (SpecificException e) {
    // 记录或处理异常
}
```

### 异常处理

- 使用 `ServiceException` 抛出业务异常
- 禁止吞掉异常而不处理
- 集成测试使用 `@SpringBootTest` + `@Transactional`

## 模块结构

```
river-module-xxx/
└── src/main/java/com/river/module/xxx/
    ├── api/           # 跨模块 API 接口（供其他模块调用）
    ├── controller/    # REST Controller
    │   ├── admin/     # 管理后台接口（/admin-api/）
    │   └── app/       # 公开接口（/app-api/，需 @PermitAll）
    ├── convert/       # MapStruct 转换器
    ├── dal/           # 数据访问层
    │   ├── dataobject/ # DO (数据库实体)
    │   └── mysql/     # MyBatis Mapper
    ├── enums/         # 枚举定义
    ├── framework/     # 模块级配置
    └── service/       # 业务逻辑层
```

## 重要约定

| 规则 | 说明 |
|------|------|
| API 路径 | `/admin-api/` 管理后台，`/app-api/` 公开接口 |
| 多租户 | 业务表必须包含 `tenant_id` 字段 |
| 软删除 | 使用 `deleted` 字段，禁止硬删除 |
| 表前缀 | 新模块 `river_`，框架 `system_`/`infra_` |

## 组件扫描

- `${river.info.base-package}.server`
- `${river.info.base-package}.module`
