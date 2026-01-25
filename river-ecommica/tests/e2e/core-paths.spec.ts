import { test, expect } from '@playwright/test';

test.describe('Core Paths', () => {
  test('Deals list to detail CTA', async ({ page }) => {
    await page.goto('/en/deals', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(500);

    const dealLinks = page.locator('article a[href^="/en/deals/"]');
    const dealCount = await dealLinks.count();

    if (dealCount === 0) {
      test.skip(true, 'No deals available to validate');
    }

    await dealLinks.first().click();
    await page.waitForLoadState('domcontentloaded');

    await expect(page).toHaveURL(/\/en\/deals\/.+/);

    const ctaLink = page.locator('a[href^="/api/go/"]');
    if (await ctaLink.count()) {
      await expect(ctaLink.first()).toBeVisible();
    } else {
      await expect(page.locator('a', { hasText: /get deal|shop|redeem/i }).first()).toBeVisible();
    }
  });

  test('Coupons list copy flow', async ({ page, context }, testInfo) => {
    if (testInfo.project.name === 'chromium') {
      await context.grantPermissions(['clipboard-read', 'clipboard-write']);
    }
    await page.goto('/en/coupons', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(500);

    const copyButtons = page.locator('button', { hasText: /^Copy$/i });
    const copyCount = await copyButtons.count();

    if (copyCount === 0) {
      test.skip(true, 'No coupons available to validate');
    }

    await copyButtons.first().click();
    await expect(page.locator('button', { hasText: /copied/i }).first()).toBeVisible();
  });
});
