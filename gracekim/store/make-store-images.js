// 사주사랑 Play 스토어 스크린샷·그래픽 이미지 생성기
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const SP = '/tmp/claude-0/-home-user-gracekim1/3650f048-5195-587a-91dc-5c50b5158b86/scratchpad';
const OUT = '/home/user/gracekim1/gracekim/store';
const F = SP + '/fonts';
const ICON = 'data:image/svg+xml;base64,' + fs.readFileSync(OUT + '/icon-C.svg').toString('base64');

const EL = { W: '#3E9B4F', F: '#D9433B', E: '#C59A2B', M: '#8A8F98', A: '#2B4C8C' };

const css = `
<link rel="stylesheet" href="file://${F}/fontsource-noto-sans-kr-5.1.1/package/400.css">
<link rel="stylesheet" href="file://${F}/fontsource-noto-sans-kr-5.1.1/package/500.css">
<link rel="stylesheet" href="file://${F}/fontsource-noto-sans-kr-5.1.1/package/700.css">
<link rel="stylesheet" href="file://${F}/fontsource-noto-sans-kr-5.1.1/package/900.css">
<link rel="stylesheet" href="file://${F}/fontsource-noto-serif-kr-5.1.1/package/700.css">
<style>
*{box-sizing:border-box;margin:0;padding:0}
body{font-family:'Noto Sans KR',sans-serif;width:1080px;height:1920px;overflow:hidden;
  background:radial-gradient(ellipse at 30% 0%,#8F5DB8 0%,#4A2668 45%,#1D0F2E 100%);color:#fff;position:relative}
.star{position:absolute;border-radius:50%;background:#FFF3D6}
.head{position:absolute;top:110px;left:0;right:0;text-align:center}
.head h1{font-size:76px;font-weight:900;letter-spacing:-2px;line-height:1.25}
.head h1 em{font-style:normal;color:#F6CF8A}
.head p{margin-top:22px;font-size:36px;color:#E5D6F2;font-weight:500}
.phone{position:absolute;left:120px;top:470px;width:840px;height:1640px;border-radius:72px;
  background:#0c0a0f;padding:22px;box-shadow:0 40px 90px rgba(0,0,0,.55), 0 0 0 3px #5a4570}
.screen{width:100%;height:100%;border-radius:52px;overflow:hidden;background:#16131A;position:relative}
.ui{width:398px;transform:scale(2);transform-origin:0 0;color:#ECE6F2}
.sb{height:28px;display:flex;justify-content:space-between;align-items:center;padding:0 22px;font-size:12px;font-weight:500;background:#CDB4F0;color:#2E1146}
.bar{background:#CDB4F0;color:#2E1146;height:56px;display:flex;align-items:center;padding:0 14px;font-size:20px;font-weight:700;gap:14px}
.bar small{font-size:15px;font-weight:500}
.wrap{padding:12px}
.card{background:#2A2530;border-radius:14px;padding:14px;margin-bottom:12px}
.card h3{font-size:15px;font-weight:700;margin-bottom:8px}
.t{font-size:12.5px;line-height:1.6}
.m{color:#A9A0B2}
.grid{display:flex;gap:6px}
.col{flex:1;text-align:center}
.lab{font-size:12px;font-weight:500}
.god{font-size:10.5px;color:#A9A0B2;margin:3px 0}
.box{aspect-ratio:1;border-radius:10px;display:flex;flex-direction:column;align-items:center;justify-content:center;margin-bottom:5px}
.box b{font-family:'Noto Serif KR',serif;font-size:30px;font-weight:700;line-height:1.1}
.box span{font-size:10px;margin-top:2px}
.btn{background:#CDB4F0;color:#2E1146;border-radius:26px;height:48px;display:flex;align-items:center;justify-content:center;font-size:16px;font-weight:700;margin-bottom:12px}
.row{display:flex;align-items:center;gap:8px;margin:6px 0}
.track{flex:1;height:16px;border-radius:8px;background:#1F1B25;overflow:hidden}
.fill{height:100%}
.chip{border:1px solid #6b6272;border-radius:8px;padding:3px 12px;font-size:12px}
.chip.on{background:#4a4057;border-color:#4a4057}
.score{width:66px;height:66px;border-radius:50%;background:#CDB4F0;color:#2E1146;display:flex;flex-direction:column;align-items:center;justify-content:center}
.dy{display:flex;gap:6px;overflow:hidden}
.dc{min-width:58px;background:#16131A;border-radius:12px;padding:8px 0;text-align:center}
.dc.now{background:#E8B04B;color:#2E1146}
.dc b{font-family:'Noto Serif KR',serif;font-size:19px;display:block;line-height:1.2}
.field{border:1px solid #6b6272;border-radius:6px;height:46px;display:flex;align-items:center;padding:0 12px;font-size:14px;position:relative}
.field label{position:absolute;top:-8px;left:9px;background:#2A2530;padding:0 4px;font-size:10.5px;color:#CDB4F0}
.radio{display:inline-flex;align-items:center;gap:6px;margin-right:18px;font-size:13.5px}
.radio i{width:18px;height:18px;border-radius:50%;border:2px solid #CDB4F0;display:inline-flex;align-items:center;justify-content:center}
.radio i.on::after{content:'';width:9px;height:9px;border-radius:50%;background:#CDB4F0}
.check{display:inline-flex;align-items:center;gap:6px;font-size:13.5px}
.check i{width:18px;height:18px;border-radius:3px;background:#CDB4F0;color:#2E1146;font-style:normal;font-size:13px;display:inline-flex;align-items:center;justify-content:center;font-weight:900}
</style>`;

const stars = () => [[90,60,5],[980,90,4],[160,380,3],[930,330,5],[60,250,3],[1010,250,3],[520,40,3],[300,300,2],[800,40,2]]
  .map(([x,y,r]) => `<div class="star" style="left:${x}px;top:${y}px;width:${r*2}px;height:${r*2}px;opacity:.75"></div>`).join('');

const page = (h1, sub, screenInner) => `<!doctype html><html><head><meta charset="utf-8">${css}</head><body>${stars()}
<div class="head"><h1>${h1}</h1><p>${sub}</p></div>
<div class="phone"><div class="screen"><div class="ui">
<div class="sb"><span>9:41</span><span>▴ ◆ ▮</span></div>${screenInner}</div></div></div></body></html>`;

const resultBar = `<div class="bar"><small>〈 다시</small>김하늘의 사주</div>`;
const birth = `<div class="t" style="margin-bottom:10px">양력 1995년 3월 15일 · 음력 1995년 2월 15일<br><span class="m">14:30 · 여성 · 돼지띠</span></div>`;

// 실제 앱 계산 결과 (김하늘, 여, 1995-03-15 14:30 양력)
const pillars = [
  ['시주','편인',['癸','계수',EL.A],['未','미토',EL.E],'편재'],
  ['일주','일간(나)',['乙','을목',EL.W],['巳','사화',EL.F],'상관'],
  ['월주','편재',['己','기토',EL.E],['卯','묘목',EL.W],'비견'],
  ['년주','비견',['乙','을목',EL.W],['亥','해수',EL.A],'정인'],
];
const pillarCard = `<div class="card"><h3>사주팔자 (四柱八字)</h3><div class="grid">${pillars.map(([l,g,s,b,bg]) => `
  <div class="col"><div class="lab">${l}</div><div class="god">${g}</div>
  <div class="box" style="background:${s[2]}"><b>${s[0]}</b><span>${s[1]}</span></div>
  <div class="box" style="background:${b[2]}"><b>${b[0]}</b><span>${b[1]}</span></div>
  <div class="god">${bg}</div></div>`).join('')}</div></div>`;

const dmCard = `<div class="card"><h3>나의 본성 · 을목(乙木) · 부드러운 풀과 덩굴</h3>
<p class="t">바람에 휘어도 꺾이지 않는 풀처럼 유연하고 적응력이 뛰어납니다. 섬세하고 친화력이 좋아 사람들과 잘 어울립니다.</p>
<p class="t" style="font-weight:700;margin-top:6px">강점: 생활력, 인내심, 사교성, 현실 감각</p></div>`;

const fortuneCard = `<div class="card" style="background:#2F2639">
<div style="display:flex;align-items:center;margin-bottom:10px"><h3 style="flex:1;margin:0">오늘의 운세</h3><span class="chip on">오늘</span>&nbsp;<span class="chip">내일</span></div>
<div style="display:flex;gap:12px;align-items:center;margin-bottom:10px"><div class="score"><b style="font-size:24px;line-height:1">95</b><span style="font-size:10px">점</span></div>
<div><div class="t m" style="font-size:11.5px">11월 19일 · 정유(丁酉)일 · 식신</div><div style="font-size:20px;font-weight:900">여유롭고 즐거운 날</div></div></div>
<p class="t">마음이 편안하고 재능이 자연스럽게 드러나는 날입니다. 맛있는 음식, 취미, 창작 활동에 행운이 따릅니다.</p>
<p class="t" style="margin-top:6px">• 오늘의 지지와 내 일지가 삼합을 이룹니다. 협력하면 일이 술술 풀립니다.</p>
<p class="t" style="margin-top:6px">💰 재물&nbsp; 작은 즐거움에 쓰는 돈은 아깝지 않은 날.</p>
<p class="t">💗 애정&nbsp; 다정한 말 한마디가 큰 호감을 부릅니다.</p>
<p class="t">🍀 건강&nbsp; 잘 먹고 잘 쉬는 날, 과식만 조심하세요.</p>
<p class="t" style="margin-top:6px;font-weight:700"><span style="display:inline-block;width:11px;height:11px;border-radius:50%;background:${EL.M};vertical-align:-1px"></span> 행운의 색 흰색 · 숫자 4, 9 · 방향 서쪽</p></div>`;

const counts = [['木 목',3,EL.W],['火 화',1,EL.F],['土 토',2,EL.E],['金 금',0,EL.M],['水 수',2,EL.A]];
const elemCard = `<div class="card"><h3>오행 분포</h3>${counts.map(([n,c,col]) => `
<div class="row"><span class="t" style="width:44px">${n}</span><div class="track"><div class="fill" style="width:${c/8*100}%;background:${col}"></div></div><span class="t" style="width:30px;text-align:right">${c}개</span></div>`).join('')}
<p class="t" style="margin-top:8px">일간은 을목(乙木)으로, 음(陰)의 기운을 지닌 목(木)의 사람입니다. 사주에서 가장 많은 오행은 목(木)이고, 금(金) 기운은 보이지 않습니다.</p>
<p class="t" style="margin-top:4px">• 금(金) 기운이 부족합니다. 결단과 정리의 기운을 보충하세요. 흰색·금색, 서쪽, 정리정돈, 원칙 세우기가 도움이 됩니다.</p></div>`;

const daeun = [[7,'庚','辰','경진','정관',EL.M,EL.E],[17,'辛','巳','신사','편관',EL.M,EL.F],[27,'壬','午','임오','정인',EL.A,EL.F],[37,'癸','未','계미','편인',EL.A,EL.E],[47,'甲','申','갑신','겁재',EL.W,EL.M],[57,'乙','酉','을유','비견',EL.W,EL.M]];
const daeunCard = `<div class="card"><h3>대운 (10년 운의 흐름)</h3><p class="t m" style="font-size:11px;margin-bottom:8px">대운수 7 · 순행 (나이는 세는나이 기준)</p>
<div class="dy">${daeun.map(([a,s,b,k,g,cs,cb],i) => `<div class="dc ${i==2?'now':''}"><div style="font-size:11px">${a}세</div>
<b style="color:${i==2?'#2E1146':cs}">${s}</b><b style="color:${i==2?'#2E1146':cb}">${b}</b><div style="font-size:10.5px">${k}</div><div style="font-size:10.5px">${g}</div></div>`).join('')}</div>
<p class="t" style="margin-top:8px">지금은 임오(壬午) 대운(27~36세)입니다. 정인의 기운 — 나를 돕는 바른 기운. 학문, 자격, 어머니, 후원</p></div>
<div class="card"><h3>2026년 병오(丙午)년 운세</h3><p class="t">아이디어와 표현력이 빛나는 해입니다. 다만 말실수나 윗사람과의 마찰을 조심하면 성과가 커집니다.</p></div>`;

const compatScreen = `<div class="bar"><small>〈 뒤로</small>궁합 결과</div><div class="wrap">
<div style="display:flex;align-items:center;text-align:center;margin:6px 0 14px">
 <div style="flex:1"><div style="font-size:16px;font-weight:700">김하늘</div><div style="color:${EL.W};font-weight:700;font-size:13px">을목 일간</div><div class="t m" style="font-size:11px">돼지띠 · 을사일주</div></div>
 <div style="font-size:28px">💞</div>
 <div style="flex:1"><div style="font-size:16px;font-weight:700">이준호</div><div style="color:#B9BEC7;font-weight:700;font-size:13px">경금 일간</div><div class="t m" style="font-size:11px">원숭이띠 · 경신일주</div></div></div>
<div class="card" style="background:#2F2639;text-align:center;padding:20px 14px">
 <div style="font-size:44px;font-weight:900;color:#CDB4F0;line-height:1.1">95점</div>
 <div style="font-size:21px;font-weight:900;margin:4px 0 6px">천생연분</div>
 <p class="t">서로에게 힘이 되어 주는 좋은 인연입니다. 지금처럼 아끼고 표현하면 오래도록 함께할 수 있습니다.</p></div>
${[['일간 천간합 (乙庚合)','두 사람의 본성이 서로 끌어당기는 합의 관계입니다. 처음 만났을 때부터 편안하고 자연스럽게 가까워지는 인연입니다.'],
   ['배우자 자리 육합 (巳申合)','함께 사는 생활 리듬이 잘 맞는 좋은 합입니다. 가정과 일상에서 화목하게 지낼 수 있습니다.'],
   ['오행 보완 (목(木)·금(金))','한 사람에게 부족한 기운을 다른 사람이 채워 줍니다. 함께 있을 때 균형이 잡히는 관계입니다.']]
  .map(([h,d]) => `<div class="card"><div style="color:#7BD389;font-weight:700;font-size:14px;margin-bottom:4px">😊 ${h}</div><p class="t">${d}</p></div>`).join('')}</div>`;

const inputScreen = `<div class="bar">사주사랑</div><div class="wrap">
<p class="t" style="font-size:14px;margin:4px 0 12px">태어난 날과 시간을 입력하면<br>사주팔자와 오행, 대운, 오늘의 운세를 풀어 드립니다.</p>
<div class="card"><div class="field"><label>이름 (선택)</label>김하늘</div></div>
<div class="card"><h3>성별</h3><span class="radio"><i class="on"></i>여성</span><span class="radio"><i></i>남성</span></div>
<div class="card"><h3>생년월일</h3><div style="margin-bottom:12px"><span class="radio"><i class="on"></i>양력</span><span class="radio"><i></i>음력</span></div>
<div style="display:flex;gap:8px"><div class="field" style="flex:1.4"><label>년</label>1995</div><div class="field" style="flex:1"><label>월</label>3</div><div class="field" style="flex:1"><label>일</label>15</div></div></div>
<div class="card"><h3>태어난 시간</h3><div style="display:flex;gap:8px;margin-bottom:10px"><div class="field" style="flex:1"><label>시 (0~23)</label>14</div><div class="field" style="flex:1"><label>분</label>30</div></div>
<span class="check"><i>✓</i>서울 기준 시간 보정 (-32분)</span></div>
<div class="btn" style="margin-top:4px">사주 보기</div></div>`;

const shots = [
  ['01', '타고난 <em>나의 사주팔자</em>를<br>한눈에', '생년월일시만 입력하면 바로 풀어 드려요', resultBar + `<div class="wrap">${birth}${pillarCard}${dmCard}</div>`],
  ['02', '매일 아침,<br><em>오늘의 운세</em> 확인', '재물·애정·건강과 행운의 색·숫자까지', resultBar + `<div class="wrap">${birth}${fortuneCard}<div class="btn">💞 궁합 보기</div></div>`],
  ['03', '우리 <em>궁합</em>은<br>몇 점일까?', '두 사람의 일간·배우자 자리·띠·오행으로 분석', compatScreen],
  ['04', '<em>오행 분포</em>와<br>10년 <em>대운</em>의 흐름', '부족한 기운을 채우는 생활 팁까지', resultBar + `<div class="wrap">${elemCard}${daeunCard}</div>`],
  ['05', '양력·음력·윤달까지<br><em>절기 기준</em>으로 정확하게', '입력한 정보는 휴대폰 밖으로 나가지 않아요', inputScreen],
];

const feature = `<!doctype html><html><head><meta charset="utf-8">${css}
<style>body{width:1024px;height:500px;background:radial-gradient(ellipse at 25% 30%,#8F5DB8 0%,#4A2668 50%,#1D0F2E 100%)}</style></head><body>
${[[60,50,3],[960,60,4],[900,420,3],[420,40,2],[520,450,3],[980,250,2],[30,400,2]].map(([x,y,r])=>`<div class="star" style="left:${x}px;top:${y}px;width:${r*2}px;height:${r*2}px;opacity:.8"></div>`).join('')}
<img src="${ICON}" style="position:absolute;left:90px;top:100px;width:300px;height:300px;border-radius:68px;box-shadow:0 20px 60px rgba(0,0,0,.5)">
<div style="position:absolute;left:450px;top:120px">
 <div style="font-size:104px;font-weight:900;letter-spacing:-3px;line-height:1.1">사주<span style="color:#F6CF8A">사랑</span></div>
 <div style="font-size:36px;font-weight:500;color:#E5D6F2;margin-top:18px">오늘의 운세 · 궁합 · 사주풀이</div>
 <div style="font-size:26px;color:#F6CF8A;margin-top:26px;font-weight:700">✦ 무료 · 회원가입 없이 바로</div>
</div></body></html>`;

(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1080, height: 1920 }, deviceScaleFactor: 1 });
  for (const [n, h, s, inner] of shots) {
    const f = `${SP}/shot-${n}.html`;
    fs.writeFileSync(f, page(h, s, inner));
    await p.goto('file://' + f);
    await p.evaluate(() => document.fonts.ready);
    await p.waitForTimeout(400);
    await p.screenshot({ path: `${OUT}/screenshot-${n}.png` });
  }
  const fp = await b.newPage({ viewport: { width: 1024, height: 500 }, deviceScaleFactor: 1 });
  fs.writeFileSync(`${SP}/feature.html`, feature);
  await fp.goto(`file://${SP}/feature.html`);
  await fp.evaluate(() => document.fonts.ready);
  await fp.waitForTimeout(400);
  await fp.screenshot({ path: `${OUT}/feature-graphic.png` });
  // 한눈에 보기용 미리보기
  const imgs = shots.map(([n]) => 'data:image/png;base64,' + fs.readFileSync(`${OUT}/screenshot-${n}.png`).toString('base64'));
  const pv = await b.newPage({ viewport: { width: 1500, height: 900 }, deviceScaleFactor: 1 });
  await pv.setContent(`<body style="margin:0;background:#111;padding:20px">
   <img src="data:image/png;base64,${fs.readFileSync(`${OUT}/feature-graphic.png`).toString('base64')}" style="width:600px;display:block;margin:0 auto 20px">
   <div style="display:flex;gap:14px;justify-content:center">${imgs.map(s=>`<img src="${s}" style="width:270px">`).join('')}</div></body>`);
  await pv.waitForTimeout(300);
  await pv.screenshot({ path: `${OUT}/store-preview.png`, fullPage: true });
  await b.close();
})();
