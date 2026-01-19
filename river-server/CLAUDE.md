# CLAUDE.md - River Server

后端 API 服务，基于 Spring Boot 3.5 + Java 17 + PostgreSQL 17。

## 常用命令

```bash
# 编译整个项目
mvn clean compile

# 编译单个模块
cd river-module-{module} && mvn clean compile

# 运行服务器（默认端口 48080）
cd river-server && mvn spring-boot:run

# 跳过测试打包
mvn clean package -DskipTests

# 运行测试
mvn test -Dtest=TestClassName              # 单个测试类
mvn test -Dtest=TestClassName#methodName   # 单个测试方法
```

## 项目架构

### 模块结构

```
river-server/
├── river-framework/           # 技术组件（基础框架）
│   ├── river-common/          # 公共工具类、枚举、常量
│   ├── river-spring-boot-starter-mybatis/     # MyBatis Plus 扩展
│   ├── river-spring-boot-starter-redis/       # Redis/Redisson 封装
│   ├── river-spring-boot-starter-web/         # Web MVC 全局异常、日志
│   ├── river-spring-boot-starter-security/    # 安全认证
│   ├── river-spring-boot-starter-biz-tenant/  # 多租户
│   ├── river-spring-boot-starter-biz-data-permission/  # 数据权限
│   ├── river-spring-boot-starter-biz-ip/      # IP 地理位置
│   ├── river-spring-boot-starter-excel/       # Excel 导入导出
│   ├── river-spring-boot-starter-job/         # 定时任务
│   ├── river-spring-boot-starter-mq/          # 消息队列
│   ├── river-spring-boot-starter-protection/  # 保护限流
│   ├── river-spring-boot-starter-test/        # 测试工具
│   └── river-spring-boot-starter-websocket/   # WebSocket
├── river-server/              # 主启动模块
├── river-dependencies/        # 依赖管理 BOM
├── river-module-system/       # 系统模块（用户、角色、权限、字典）
├── river-module-infra/        # 基础设施模块（文件、任务、配置）
├── river-module-affiliate/    # 联盟营销模块（商家、Offer、分类）
├── river-module-tracking/     # 跟踪追踪模块（点击、转化）
├── river-module-coupon/       # 优惠券模块
├── river-module-blog/         # 博客模块
├── river-module-campaign/     # 营销活动模块（流量源、成本记录）
└── river-module-stats/        # 统计模块
```

### 业务模块结构

每个业务模块（如 river-module-affiliate）采用单模块结构：

```
river-module-{name}/
└── src/main/java/
    └── com/river/module/{name}/
        ├── api/                # 跨模块 API 接口（供其他模块调用）
        ├── controller/         # REST Controller（HTTP API）
        │   ├── admin/          # 管理后台接口（/admin-api/）
        │   └── app/            # 公开接口（/app-api/，需 @PermitAll）
        ├── convert/            # MapStruct 转换器
        ├── dal/                # 数据访问层
        │   ├── dataobject/     # DO (数据库实体)
        │   └── mysql/          # MyBatis Mapper
        ├── enums/              # 枚举定义
        ├── framework/          # 模块级配置
        └── service/            # 业务逻辑层
            ├── {Service}Service.java        # 服务接口
            ├── {Service}ServiceImpl.java    # 服务实现
            └── ...
```

## 代码规范

### 包命名约定

| 包 | 说明 |
|----|------|
| `controller` | HTTP API 层，返回 `CommonResult<T>` |
| `convert` | MapStruct 转换器，DO/VO/DTO 互转 |
| `dal.dataobject` | 数据库实体（DO），对应数据库表 |
| `dal.mysql` | MyBatis Mapper 接口 |
| `service` | 业务逻辑层 |

### API 路径约定

| 路径前缀 | 说明 |
|----------|------|
| `/admin-api/` | 管理后台接口（需要认证） |
| `/app-api/` | 公开接口（站点调用，部分需要认证） |

### 数据库表约定

- 新业务模块表前缀：`river_`
- 框架表前缀：`system_`、`infra_`
- 多租户：业务表必须包含 `tenant_id` 字段
- 软删除：使用 `deleted` 字段（0=未删除，1=已删除），禁止硬删除
- 字典：枚举类型优先使用系统字典 API

### 标准表结构

```sql
CREATE TABLE river_example (
    id int8 NOT NULL,
    name varchar(200) NOT NULL,
    status int2 NOT NULL DEFAULT 0,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
```

## 配置文件

| 文件 | 说明 |
|------|------|
| `application.yaml` | 主配置 |
| `application-local.yaml` | 本地开发环境（默认激活） |
| `application-dev.yaml` | 开发服务器环境 |

## 数据库

- **类型**: PostgreSQL 17
- **连接**: `localhost:5432/river`
- **账号**: `postgres / 123456`（本地）
- **MCP 查询**: 使用 `mcp__postgres__query` 工具执行 SQL

## 测试规范

- 单元测试使用 Spring Boot Test
- 测试配置文件：`application-unit-test.yaml`
- Mapper 测试需 `@SqlTest` 注解

## 重要注意事项

1. **遵循现有模式** - 创建新代码前先查看类似模块（如 river-module-affiliate）
2. **TDD 流程** - 先写测试 → 看失败 → 再实现
3. **未明确要求不得 commit** - 不要主动创建 Git 提交
4. **禁止硬删除** - 始终使用软删除
5. **多租户** - 业务表必须包含 `tenant_id` 字段
