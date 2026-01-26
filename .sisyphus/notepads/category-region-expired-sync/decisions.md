# Decisions - Category Region Filter Implementation

## Why MyBatis @Select Instead of XML Mapper

Although XML mappers are more maintainable for complex queries, the codebase convention for this module uses annotation-based SQL (see DealMapper, CouponMapper patterns). Using `@Select` with `<script>` tags maintains consistency with existing code patterns.

## Why Single Combined Query for Deal + Coupon

Instead of separate queries for Deal and Coupon tables, used a UNION in a single query:
- Single database round-trip
- Deduplication is automatic via DISTINCT
- Simplifies service layer logic (just get Set<Long>)

## Parent Preservation Logic Placement

Placed ancestor-walking logic in `CategoryServiceImpl` as private helper methods rather than in SQL:
- More maintainable (logic is visible in Java code)
- Easier to test
- Database-independent (could switch DB without rewriting complex recursive queries)

## API Parameter Design: Comma-Separated String

Accepted `regions` as comma-separated string in controller, parsed to List<String>:
- Matches REST conventions (e.g., `?regions=US,CA,GB`)
- Simpler than array parameter syntax (`?regions=US&regions=CA`)
- Consistent with how regions are stored (comma-separated strings in DB)

## Null/Empty Regions Means "All Categories"

When `regions` is null or empty, the filtering is bypassed to return all categories:
- Preserves existing API behavior
- Allows frontends to call without regions parameter
- No breaking changes to existing consumers
