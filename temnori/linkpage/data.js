// 템노리 사이트 데이터 — 새 회차·카드뉴스는 여기에 한 줄씩 추가하면 됩니다.
// date: 게시 날짜·시각(한국 시간). 이 시각이 지나야 사이트에 나타납니다.

const CATS = ["욕실", "주방", "침구", "정리", "청소"];

const ITEMS = [
  { ep:1, date:"2026-11-09T20:00", cats:["욕실","주방"], name:"정사각 실리콘 배수구 거름망 2개입", desc:"욕실·세면대·싱크대 배수구 머리카락 걱정 끝", img:"img/ep01.jpg", url:"https://link.coupang.com/a/iv21KdyH7c" },
  { ep:2, date:"2026-11-10T12:00", cats:["욕실","청소"], name:"무선 전동 욕실 청소기", desc:"허리 안 숙이고 벽·바닥까지, 브러시 교체형", img:"img/ep02.jpg", url:"https://link.coupang.com/a/iwTJ45MD9M" },
  { ep:3, date:"2026-11-11T12:00", cats:["청소"], name:"에브리봇 엣지2 물걸레 로봇청소기", desc:"낮은 높이로 소파 밑까지 물걸레질", img:"img/ep03.jpg", url:"https://link.coupang.com/a/gLXEUZ5vy0" },
  { ep:4, date:"2026-11-13T12:00", cats:["침구","청소"], name:"벤트론스 침구 청소기 (UV·듀얼브러시)", desc:"이불·소파·쿠션 먼지를 쓱 밀어서", img:"img/ep04.jpg", url:"https://link.coupang.com/a/iy2S9jyhlk" },
  { ep:5, date:"2026-11-16T12:00", cats:["주방","정리"], name:"한누네 냉장고 정리함 4개 세트", desc:"국내생산, 손잡이로 쏙 꺼내 쓰는 정리함", img:"img/ep05.jpg", url:"https://link.coupang.com/a/iBM63LdPNs" },
  { ep:6, date:"2026-11-18T12:00", cats:["주방"], name:"조앤제이 국산 김장매트", desc:"테두리 있어서 양념 안 튀어요, 10~100포기 사이즈", img:"img/ep06.jpg", url:"https://link.coupang.com/a/iEpUmrKynk" }
];

const TIPS = [
  { no:1, date:"2026-11-17T12:00", title:"냉장고 정리할 때 다들 은근 하는 실수 3가지", dir:"tips/c01", count:5 },
  { no:2, date:"2026-11-19T12:00", title:"김장 끝나고 고춧가루 얼룩 지우는 순서", dir:"tips/c02", count:5 },
  { no:3, date:"2026-11-24T12:00", title:"창틀 까만 곰팡이 미리 막는 법", dir:"tips/c03", count:5 },
  { no:4, date:"2026-11-26T12:00", title:"우리 집 난방비 새는 곳 체크리스트", dir:"tips/c04", count:5 }
];

const kst = d => new Date(d + ":00+09:00").getTime();
const released = arr => arr.filter(i => kst(i.date) <= Date.now()).sort((a, b) => kst(b.date) - kst(a.date));
const esc = s => s.replace(/[&<>"]/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;"}[c]));

// 모든 페이지 공통 상단 (배너 + 프로필 + SNS + 메뉴)
function renderTop(active) {
  const tabs = [["index.html","상품"],["tips.html","살림 꿀팁"],["about.html","소개"]];
  document.getElementById("top").innerHTML = `
    <header>
      <img class="banner" src="img/banner.jpg" alt="템노리 TEMNORI - 생활 속 괜찮은 아이템을 쉽고 빠르게" width="1200" height="438">
      <div class="profile">
        <img class="logo" src="img/logo.png" alt="템노리 로고" width="56" height="56">
        <div class="pname"><b>템노리</b><span>@temnori.shop</span></div>
      </div>
      <nav class="sns">
        <a href="https://www.instagram.com/temnori.shop/" target="_blank" rel="noopener">인스타그램</a>
        <a href="https://www.tiktok.com/@temnori.shop" target="_blank" rel="noopener">틱톡</a>
        <a href="https://www.threads.com/@temnori.shop" target="_blank" rel="noopener">스레드</a>
      </nav>
    </header>
    <nav class="menu">${tabs.map(([h,t]) => `<a href="${h}"${h===active?' class="on"':''}>${t}</a>`).join("")}</nav>`;
}
