// 카드뉴스 HTML → 장별 PNG (1080x1350)
// 사용: node render.js <cards.html 경로>
const { chromium } = require('playwright');
const path = require('path');

(async () => {
  const file = path.resolve(process.argv[2]);
  const outDir = path.dirname(file);
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1080, height: 1350 }, deviceScaleFactor: 1 });
  await page.goto('file://' + file, { waitUntil: 'networkidle' });
  await page.evaluate(() => document.fonts.ready);
  const slides = await page.$$('.slide');
  for (let i = 0; i < slides.length; i++) {
    const out = path.join(outDir, `card${String(i + 1).padStart(2, '0')}.png`);
    await slides[i].screenshot({ path: out });
    console.log(out);
  }
  await browser.close();
})();
