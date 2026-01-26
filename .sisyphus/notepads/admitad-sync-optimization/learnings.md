# Admitad Sync Optimization

## Task
Optimize Admitad sync with batch processing, deduplication, and idempotent writes.

## Changes Made

### 1. Mapper Changes (Batch Query Methods)

#### MerchantMapper.java
- Added `selectListByNetworkAndExternalIds(Long networkId, List<String> externalIds)` - Batch query merchants by networkId and externalIds

#### OfferMapper.java
- Added `selectListByMerchantAndExternalIds(Long merchantId, List<String> externalIds)` - Batch query offers by merchantId and externalIds

#### CouponMapper.java
- Added `selectListByNetworkAndExternalIds(Long networkId, List<String> externalIds)` - Batch query coupons by networkId and externalIds

#### DealMapper.java
- Added `selectListByNetworkAndExternalIds(Long networkId, List<String> externalIds)` - Batch query deals by networkId and externalIds

### 2. AdmitadSyncService.java - New Batch Methods

#### syncCampaignsBatch(Long networkId, List<AdmitadCampaign> campaigns)
**Deduplication:**
- Uses `Map<String, AdmitadCampaign>` to dedupe campaigns by externalId within a batch

**Preload existing data:**
- Batch queries existing merchants using `selectListByNetworkAndExternalIds()`
- Builds `Map<String, MerchantDO>` for O(1) lookup

**Idempotent write:**
- Separates entities into `toInsert` and `toUpdate` lists
- Uses `insertBatch()` for new records
- Uses `updateBatch()` for existing records

**Merchant caching:**
- Maintains map of merchants to avoid repeated queries

#### syncOffersBatch(Long networkId, Long merchantId, List<OfferDO> offers)
- Preloads existing offers by merchantId and externalIds
- Separates insert/update operations
- Uses batch insert/update

#### syncCouponsBatch(Long networkId, List<AdmitadCoupon> coupons)
- Dedupes coupons by externalId within batch
- Separates promocodes and deals
- Calls batch handlers for each type

#### syncPromoCodesBatch(Long networkId, List<AdmitadCoupon> promoCodes)
- Preloads existing coupons and merchants (batch query)
- Uses map-based caching for merchant lookups
- Batch insert/update with tracking link creation

#### syncDealsBatch(Long networkId, List<AdmitadCoupon> deals)
- Same pattern as syncPromoCodesBatch
- Preloads existing deals and merchants
- Batch insert/update with tracking link creation

### 3. Legacy Methods Preserved
- `syncSingleCampaign()` - For backward compatibility
- `syncSingleCoupon()` - For backward compatibility
- `syncSingleDeal()` - For backward compatibility

## Key Optimizations

1. **Batch Preloading**: Instead of querying database for each entity, batch query all existing entities by externalIds upfront
2. **Map-based Caching**: Use HashMap<String, Entity> for O(1) lookups during sync
3. **Deduplication**: Within each batch, dedupe by externalId before processing
4. **Batch Operations**: Use `insertBatch()` and `updateBatch()` from BaseMapperX instead of individual inserts/updates
5. **Idempotent Writes**: Check existence first, then decide insert vs update

## Results
- ✅ Compilation successful
- ✅ Same data no duplicate records on re-sync
- ✅ Batch processing reduces DB queries
- ✅ Map caching reduces repeated lookups
