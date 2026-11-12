#!/usr/bin/env bash
# 템노리 5회차 릴스 합성 (11.4초, 1080x1920, 일레븐랩스 내레이션) — 냉장고 정리함
set -euo pipefail
cd "$(dirname "$0")"
FONT=/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc
W=1080; H=1920; FPS=30
mkdir -p build

printf '냉장고 열 때마다\n한숨 나오죠?' > build/t1.txt
printf '이게 같은\n냉장고예요' > build/t2.txt
printf '손잡이 잡고\n쏙 꺼내면 끝' > build/t3.txt
printf '주방 팬트리에도\n딱이에요' > build/t4.txt
printf '국내생산 · 4개 세트\n프로필 링크에서 확인 ↑' > build/t5.txt
printf '광고 · 쿠팡 파트너스' > build/ad.txt
printf '템노리 살림템 #5' > build/tag.txt

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

# 내레이션 구간(초): A 0-2.12 / B 2.71-4.19 / C 4.92-7.21 / D 7.83-9.31 / E 10.01-13.42
scene scene_messy.png   2.2 build/t1.txt build/s1.mp4 0.004  88
scene scene_tidy.png    1.6 build/t2.txt build/s2.mp4 0.0015 84
scene scene_hand.png    2.4 build/t3.txt build/s3.mp4 0.0008 76
scene scene_kitchen.png 1.6 build/t4.txt build/s4.mp4 0.0008 76
scene scene_tidy.png    3.6 build/t5.txt build/s5.mp4 0.0005 66

printf "file 's1.mp4'\nfile 's2.mp4'\nfile 's3.mp4'\nfile 's4.mp4'\nfile 's5.mp4'\n" > build/list.txt
ffmpeg -v error -y -f concat -safe 0 -i build/list.txt -c copy build/video.mp4

seg() { echo "[0:a]atrim=$1:$2,asetpts=PTS-STARTPTS,afade=t=in:d=0.03,afade=t=out:st=$(awk "BEGIN{print $2-$1-0.04}"):d=0.04,apad=whole_dur=$3[$4]"; }
ffmpeg -v error -y -i narration.mp3 -filter_complex \
  "$(seg 0 2.12 2.2 a);$(seg 2.71 4.19 1.6 b);$(seg 4.92 7.21 2.4 c);$(seg 7.83 9.31 1.6 d);$(seg 10.01 13.42 3.6 e);[a][b][c][d][e]concat=n=5:v=0:a=1,loudnorm=I=-14:TP=-1.5:LRA=11,aresample=44100[out]" \
  -map "[out]" -c:a pcm_s16le build/narr.wav
ffmpeg -v error -y -i build/video.mp4 -i build/narr.wav -map 0:v -map 1:a -c:v copy -c:a aac -b:a 160k -t 11.4 -movflags +faststart temnori_ep05.mp4
ffprobe -v error -show_entries format=duration -of csv=p=0 temnori_ep05.mp4
