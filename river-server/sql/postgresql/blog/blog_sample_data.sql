-- 插入作者数据
INSERT INTO river_blog_author (id, name, slug, avatar_url, bio, status, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES
    (1, '张三', 'zhang-san', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan', '全栈开发者，热爱开源技术', 0, 'system', NOW(), 'system', NOW(), 0, 0),
    (2, '李四', 'li-si', 'https://api.dicebear.com/7.x/avataaars/svg?seed=lisi', 'AI 领域专家，专注于大模型应用', 0, 'system', NOW(), 'system', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

-- 插入标签数据
INSERT INTO river_blog_tag (id, name, slug, post_count, status, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES
    (1, 'Java', 'java', 2, 0, 'system', NOW(), 'system', NOW(), 0, 0),
    (2, 'Spring Boot', 'spring-boot', 2, 0, 'system', NOW(), 'system', NOW(), 0, 0),
    (3, 'React', 'react', 1, 0, 'system', NOW(), 'system', NOW(), 0, 0),
    (4, 'AI', 'ai', 1, 0, 'system', NOW(), 'system', NOW(), 0, 0),
    (5, '数据库', 'database', 1, 0, 'system', NOW(), 'system', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

-- 插入文章数据
INSERT INTO river_blog_post (id, author_id, title, slug, content, excerpt, cover_image, type, status, published_at, meta_title, meta_description, canonical_url, view_count, featured, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES
    (1, 1, 'Spring Boot 3.x 快速入门指南', 'spring-boot-3-quick-start',
     '# Spring Boot 3.x 快速入门

Spring Boot 3.x 是 Spring 框架的最新主要版本，带来了许多新特性和改进。

## 新特性

1. **原生编译支持**：通过 GraalVM 原生镜像，显著提升应用启动速度
2. **Jakarta EE 10**：全面支持最新的企业级 Java 标准
3. **依赖升级**：所有主要依赖项均已升级到最新稳定版本

## 快速开始

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

## 总结

Spring Boot 3.x 为现代云原生应用开发提供了强大的支持。',
     '本文介绍 Spring Boot 3.x 的核心特性和快速入门方法，包括原生编译、Jakarta EE 10 等新功能。',
     'https://picsum.photos/seed/springboot/800/400',
     1, 0, NOW(), 'Spring Boot 3.x 快速入门指南 | River Blog',
     '学习 Spring Boot 3.x 的核心特性和快速入门方法',
     NULL, 100, TRUE, 'system', NOW(), 'system', NOW(), 0, 0),
    (2, 1, '深入理解 Java 泛型', 'java-generics-deep-dive',
     '# 深入理解 Java 泛型

泛型是 Java 中非常重要的特性，本文将深入探讨其原理和使用技巧。

## 什么是泛型

泛型允许在编译时检测类型错误，提供类型安全的代码。

## 核心概念

### 类型参数

```java
public class Box<T> {
    private T content;
    public void set(T content) { this.content = content; }
    public T get() { return content; }
}
```

### 泛型方法

```java
public <T> T[] toArray(T... items) {
    return items;
}
```

## 通配符

- `<? extends T>`：上界通配符
- `<? super T>`：下界通配符
- `<?>`：无界通配符',
     '本文深入探讨 Java 泛型的核心概念，包括类型参数、泛型方法和通配符的使用技巧。',
     'https://picsum.photos/seed/java/800/400',
     1, 0, NOW(), '深入理解 Java 泛型 | River Blog',
     '学习 Java 泛型的核心概念和使用技巧，包括类型参数、通配符和类型推断',
     NULL, 80, FALSE, 'system', NOW(), 'system', NOW(), 0, 0),
    (3, 2, 'React 19 新特性抢先看', 'react-19-new-features',
     '# React 19 新特性抢先看

React 19 引入了许多令人兴奋的新特性，让我们一起来探索。

## 主要特性

1. **Actions**：简化异步操作处理
2. **use hook**：新的原生 hook
3. **资源预加载**：自动预加载资源以提升性能

## 代码示例

```jsx
function SearchForm() {
    const [query, setQuery] = useState('');

    return (
        <form action={async (formData) => {
            const result = await search(formData);
        }}>
            <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
            />
        </form>
    );
}
```',
     '探索 React 19 的新特性，包括 Actions、新的 hook 和性能优化。',
     'https://picsum.photos/seed/react/800/400',
     1, 0, NOW(), 'React 19 新特性抢先看 | River Blog',
     '了解 React 19 的最新特性，包括 Actions 和新的 hook API',
     NULL, 120, TRUE, 'system', NOW(), 'system', NOW(), 0, 0),
    (4, 2, 'AI 大模型应用开发实战', 'ai-llm-development-guide',
     '# AI 大模型应用开发实战

本文介绍如何基于大语言模型构建实用的应用程序。

## 技术栈

- OpenAI GPT-4 / Claude API
- LangChain 框架
- Vector Database (Pinecone, Milvus)

## 开发流程

1. **需求分析**：明确应用场景
2. **Prompt 工程**：设计高效提示词
3. **应用架构**：设计系统流程
4. **集成测试**：确保输出质量

## 实践案例

构建一个智能客服系统，自动回答用户问题并提供解决方案。',
     '学习如何基于大语言模型构建实用的 AI 应用，包括技术选型、开发流程和实践案例。',
     'https://picsum.photos/seed/ai/800/400',
     1, 0, NOW(), 'AI 大模型应用开发实战 | River Blog',
     '学习 AI 大模型应用开发，包括 LangChain、Prompt 工程和实际案例',
     NULL, 150, FALSE, 'system', NOW(), 'system', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

-- 插入文章标签关联数据
INSERT INTO river_blog_post_tag (id, post_id, tag_id, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES
    (1, 1, 1, 'system', NOW(), 'system', NOW(), 0, 0),
    (2, 1, 2, 'system', NOW(), 'system', NOW(), 0, 0),
    (3, 2, 1, 'system', NOW(), 'system', NOW(), 0, 0),
    (4, 3, 3, 'system', NOW(), 'system', NOW(), 0, 0),
    (5, 4, 4, 'system', NOW(), 'system', NOW(), 0, 0),
    (6, 4, 1, 'system', NOW(), 'system', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

-- 更新标签的文章数量
UPDATE river_blog_tag SET post_count = (
    SELECT COUNT(*) FROM river_blog_post_tag pt
    WHERE pt.tag_id = river_blog_tag.id AND pt.deleted = 0
);
