#!/usr/bin/env bash
# 템노리 6회차 릴스 합성 (1080x1920, 일레븐랩스 내레이션) — 김장매트
set -euo pipefail
cd "$(dirname "$0")"
FONT=/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc
W=1080; H=1920; FPS=30
mkdir -p build

printf '김장 끝나고\n거실 바닥 이렇죠?' > build/t1.txt
printf '매트 하나 깔면\n바닥 걱정 끝' > build/t2.txt
printf '테두리가 있어서\n양념이 안 튀어요' > build/t3.txt
printf '물로 헹궈서\n접어두면 끝' > build/t4.txt
printf '국산 · 10~100포기\n사이즈별로 있어요' > build/t5.txt
printf '김장 매트\n프로필 링크에서 확인 ↑' > build/t6.txt
printf '광고 · 쿠팡 파트너스' > build/ad.txt
printf '템노리 살림템 #6' > build/tag.txt

title() { # $1=textfile $2=fontsize — 줄마다 가운데 정렬
  local i=0 out="" line f fs=${2:-72} lh
  lh=$(( fs * 135 / 100 ))
  while IFS= read -r line || [ -n "$line" ]; do
    f="build/$(basename "$1" .txt)_l$i.txt"; printf '%s' "$line" > "$f"
    out+="${out:+,}drawtext=fontfile=$FONT:fontsize=$fs:fontcolor=white:borderw=7:bordercolor=black:textfile=$f:x=(w-text_w)/2:y=h*0.13+$i*$lh"
    i=$((i+1))
  done < "$1"
  echo "$out"
}
AD="drawtext=fontfile=$FONT:fontsize=34:fontcolor=white:box=1:boxcolor=black@0.45:boxborderw=12:textfile=build/ad.txt:x=40:y=60"
TAG="drawtext=fontfile=$FONT:fontsize=34:fontcolor=white:box=1:boxcolor=0xFF6A00@0.85:boxborderw=12:textfile=build/tag.txt:x=w-text_w-40:y=60"
frames() { awk "BEGIN{printf \"%d\", $1*$FPS}"; }

scene() { # $1=image $2=sec $3=textfile $4=out $5=zoom step $6=font size
  local n; n=$(frames "$2")
  ffmpeg -v error -y -loop 1 -i "$1" -frames:v "$n" -filter_complex \
    "[0:v]scale=2160:3840:force_original_aspect_ratio=increase,crop=2160:3840,zoompan=z='min(zoom+$5,1.25)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=${W}x${H}:fps=$FPS,$(title "$3" "$6"),$AD,$TAG,format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$4"
}

# 내레이션 구간(초, 원본): A 0-2.39 / B 2.93-5.35 / C 5.86-8.30 / D 8.85-11.26 / E 11.91-15.57 / F 16.06-17.92
# 원본 17.9초라 1.12배속 + 문장 사이 무음 제거 → 약 13.4초
T=1.12
d() { awk "BEGIN{printf \"%.2f\", ($2-$1)/$T+0.08}"; }
DA=$(d 0 2.39); DB=$(d 2.93 5.35); DC=$(d 5.86 8.30); DD=$(d 8.85 11.26); DE=$(d 11.91 15.57); DF=$(d 16.06 17.92)
scene scene_messy.png $DA build/t1.txt build/s1.mp4 0.004  84
scene scene_mat.png   $DB build/t2.txt build/s2.mp4 0.0015 82
scene scene_rim.png   $DC build/t3.txt build/s3.mp4 0.0008 76
scene scene_rinse.png $DD build/t4.txt build/s4.mp4 0.0008 80
scene scene_mat.png   $DE build/t5.txt build/s5.mp4 0.0005 72
scene scene_rim.png   $DF build/t6.txt build/s6.mp4 0.0005 66
printf "file 's1.mp4'\nfile 's2.mp4'\nfile 's3.mp4'\nfile 's4.mp4'\nfile 's5.mp4'\nfile 's6.mp4'\n" > build/list.txt
ffmpeg -v error -y -f concat -safe 0 -i build/list.txt -c copy build/video.mp4

seg() { # $1 start $2 end $3 total dur $4 label
  local L; L=$(awk "BEGIN{print ($2-$1)/$T}")
  echo "[0:a]atrim=$1:$2,asetpts=PTS-STARTPTS,atempo=$T,afade=t=in:d=0.03,afade=t=out:st=$(awk "BEGIN{print $L-0.04}"):d=0.04,apad=whole_dur=$3[$4]"
}
ffmpeg -v error -y -i narration.mp3 -filter_complex \
  "$(seg 0 2.39 $DA a);$(seg 2.93 5.35 $DB b);$(seg 5.86 8.30 $DC c);$(seg 8.85 11.26 $DD d);$(seg 11.91 15.57 $DE e);$(seg 16.06 17.92 $DF f);[a][b][c][d][e][f]concat=n=6:v=0:a=1,loudnorm=I=-14:TP=-1.5:LRA=11,aresample=44100[out]" \
  -map "[out]" -c:a pcm_s16le build/narr.wav
TOT=$(awk "BEGIN{print $DA+$DB+$DC+$DD+$DE+$DF}")
ffmpeg -v error -y -i build/video.mp4 -i build/narr.wav -map 0:v -map 1:a -c:v copy -c:a aac -b:a 160k -t $TOT -movflags +faststart temnori_ep06.mp4
ffprobe -v error -show_entries format=duration -of csv=p=0 temnori_ep06.mp4
