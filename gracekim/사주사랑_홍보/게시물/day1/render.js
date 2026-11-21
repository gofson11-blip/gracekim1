const {chromium}=require('playwright');const {spawn}=require('child_process');
(async()=>{const FPS=30,DUR=23;const b=await chromium.launch();const p=await b.newPage({viewport:{width:1080,height:1920}});
await p.goto('file://'+process.cwd()+'/reel.html',{waitUntil:'networkidle'});await p.evaluate(()=>document.fonts.ready);
console.log('font',await p.evaluate(()=>document.fonts.check('900 100px "Noto Sans KR"')));
const ff=spawn('ffmpeg',['-y','-f','image2pipe','-framerate',''+FPS,'-i','-','-f','lavfi','-i','anullsrc=r=44100:cl=stereo','-shortest','-c:v','libx264','-pix_fmt','yuv420p','-preset','medium','-crf','18','-c:a','aac','-movflags','+faststart','day1_reel.mp4'],{stdio:['pipe','ignore','inherit']});
for(let i=0;i<FPS*DUR;i++){await p.evaluate(t=>setTime(t),i/FPS);const buf=await p.screenshot({type:'jpeg',quality:92});if(!ff.stdin.write(buf))await new Promise(r=>ff.stdin.once('drain',r));}
ff.stdin.end();await new Promise(r=>ff.on('close',r));
for(const t of [1.5,5,9.5,13.5,17,21.5]){await p.evaluate(t=>setTime(t),t);await p.screenshot({path:`f_${t}.png`})}
await p.evaluate(()=>setCover());await p.screenshot({path:'day1_cover.png'});
await b.close()})();
