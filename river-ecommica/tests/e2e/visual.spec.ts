import { test, expect } from '@playwright/test';

test.describe('Visual Regression', () => {
  test.describe.configure({ timeout: 60000 });
  
  test('Home page visual snapshot', async ({ page }) => {
    await page.goto('/en', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(200);
    await page.addStyleTag({ path: './tests/e2e/screenshot.css' });
    
    await expect(page).toHaveScreenshot('home-page-full.png', {
      fullPage: true,
      animations: 'disabled'
    });
  });

  test('Hero section visual snapshot', async ({ page }) => {
    await page.goto('/en', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(200);
    await page.addStyleTag({ path: './tests/e2e/screenshot.css' });
    
    const hero = page.locator('section').first();
    await expect(hero).toBeVisible();
    
    await expect(hero).toHaveScreenshot('hero-section.png', {
      animations: 'disabled'
    });
  });

  test('Navigation bar visual snapshot', async ({ page }) => {
    await page.goto('/en', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(200);
    await page.addStyleTag({ path: './tests/e2e/screenshot.css' });
    
    const nav = page.locator('nav').first();
    await expect(nav).toBeVisible();
    
    await expect(nav).toHaveScreenshot('nav-bar.png', {
      animations: 'disabled'
    });
  });

});
