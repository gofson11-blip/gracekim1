const {chromium}=require('playwright');const {spawn}=require('child_process');
const [name,DUR]=[process.argv[2],+process.argv[3]];
(async()=>{const FPS=30;const b=await chromium.launch();const p=await b.newPage({viewport:{width:1080,height:1920}});
await p.goto('file://'+process.cwd()+'/'+name+'.html',{waitUntil:'networkidle'});await p.evaluate(()=>document.fonts.ready);
const ff=spawn('ffmpeg',['-v','error','-y','-f','image2pipe','-framerate',''+FPS,'-i','-','-f','lavfi','-i','anullsrc=r=44100:cl=stereo','-shortest','-c:v','libx264','-pix_fmt','yuv420p','-crf','18','-c:a','aac','-movflags','+faststart',name+'_reel.mp4'],{stdio:['pipe','ignore','inherit']});
for(let i=0;i<FPS*DUR;i++){await p.evaluate(t=>setTime(t),i/FPS);const buf=await p.screenshot({type:'jpeg',quality:92});if(!ff.stdin.write(buf))await new Promise(r=>ff.stdin.once('drain',r));}
ff.stdin.end();await new Promise(r=>ff.on('close',r));
const ts=process.argv.slice(4).map(Number);for(const t of ts){await p.evaluate(t=>setTime(t),t);await p.screenshot({path:`${name}_f${t}.png`})}
await p.evaluate(()=>setCover());await p.screenshot({path:name+'_cover.png'});await b.close()})();
