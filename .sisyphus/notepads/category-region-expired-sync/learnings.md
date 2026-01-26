# Learnings - Category Region Filter Implementation

## PostgreSQL Array Operations for Regions

The existing codebase uses PostgreSQL-specific array operations for region filtering:

```java
// Pattern from DealMapper/CouponMapper
"string_to_array(regions, ',') @> ARRAY['US']"
```

This converts comma-separated string to array, then uses `@>` (contains operator) to check if target region is included.

## MyBatis @Select with Dynamic SQL

For complex conditional SQL, use `<script>` tags with MyBatis annotations:

```java
@Select("<script>" +
        "SELECT ... " +
        "WHERE ... " +
        "<if test='regions != null and regions.size() > 0'>" +
        "AND (" +
        "<foreach collection='regions' item='region' separator=' OR '>" +
        "string_to_array(..., ',') @> ARRAY[#{region}]" +
        "</foreach>" +
        ") " +
        "</if>" +
        "</script>")
```

## Status Enums Across Modules

- Deal uses `CommonStatusEnum.ENABLE(0)` for active status
- Coupon uses `CouponStatusEnum.ACTIVE(1)` for active status
- Must check both when querying active data across tables

## endTime NULL Semantics

In this codebase:
- `endTime IS NULL` = not expired / ongoing
- `endTime < NOW()` = expired
- Use `(endTime IS NULL OR endTime > NOW())` to filter for active items

## Parent Preservation Pattern

When filtering tree-structured data:
1. Find leaf nodes that match criteria
2. Recursively add all ancestor IDs
3. Filter final result by the combined ID set

This ensures parent categories remain visible if any child has data.
# DealCouponExpirationJob Implementation

## JobHandler Pattern
- Use `@Component("jobName")` for bean registration
- Implement `JobHandler` interface
- Use `@TenantJob` annotation on `execute()` method for multi-tenant support
- Return formatted result string from `execute()` method

## Status Enum Values
- Deal: CommonStatusEnum.ENABLE=0, DISABLE=1
- Coupon: CouponStatusEnum.ACTIVE=1, EXPIRED=2, DISABLED=0

## Batch Update SQL Pattern
- Use `@Update` annotation on Mapper interface methods
- PostgreSQL: `NOW()` for current timestamp
- Always include `deleted = 0` for soft-delete safety
- Pattern: `UPDATE table SET status=X WHERE status=Y AND condition AND deleted=0`

## Service Layer Pattern
- Add method to Service interface first
- Implement in ServiceImpl by delegating to Mapper
- Return int (affected rows count)
