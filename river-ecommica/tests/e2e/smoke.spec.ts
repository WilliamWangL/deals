import { test, expect } from '@playwright/test';

test.describe('Smoke Tests', () => {
  
  test('Home page loads successfully with locale', async ({ page }) => {
    await page.goto('/en', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(500);
    const mainContent = page.locator('main, [role="main"], .main');
    await expect(mainContent).toBeVisible();
    const heroHeading = page.locator('h1, h2').first();
    await expect(heroHeading).toBeVisible();
    await expect(heroHeading).not.toBeEmpty();
    const pageContent = page.locator('body');
    await expect(pageContent).toHaveText(/[a-zA-Z]/);
  });

  test('Page loads without critical errors', async ({ page }) => {
    const consoleErrors: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text());
      }
    });
    page.on('pageerror', error => {
      consoleErrors.push(error.message);
    });

    await page.goto('/en', { waitUntil: 'domcontentloaded' });
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(500);
    
    expect(consoleErrors.filter(error => 
      error.includes('ReferenceError') || 
      error.includes('TypeError') ||
      error.includes('Uncaught')
    )).toHaveLength(0);
  });

  test('Navigation elements are present', async ({ page }) => {
    await page.goto('/en', { waitUntil: 'domcontentloaded' });
    const nav = page.locator('nav').first();
    await expect(nav).toBeVisible();
    const links = page.locator('nav a, nav button');
    const linkCount = await links.count();
    expect(linkCount).toBeGreaterThan(0);
  });

});
