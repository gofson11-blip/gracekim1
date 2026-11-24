#!/usr/bin/env bash
# 템노리 8회차 (무료 방식: 실제 상품 사진 + 자막, 나레이션 없음) — 대쉬 틈새브러시
set -euo pipefail
cd "$(dirname "$0")"
FONT=/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc
W=1080; H=1920; FPS=30
mkdir -p build

printf '타일 줄눈 때\n아직 칫솔로 닦으세요?' > build/t1.txt
printf 'L자 솔로\n쓱 문지르면 끝' > build/t2.txt
printf '벽 모서리까지\n한 번에 닿아요' > build/t3.txt
printf '손가락 홈 손잡이라\n힘이 덜 들어요' > build/t4.txt
printf '물로 헹구면\n관리 끝' > build/t5.txt
printf '대쉬 틈새브러시\n프로필 링크에서 확인 ↑' > build/t6.txt
printf '광고 · 쿠팡 파트너스' > build/ad.txt
printf '템노리 살림템 #8' > build/tag.txt

title() { # $1=textfile $2=fontsize
  local i=0 out="" line f fs=${2:-72} lh
  lh=$(( fs * 135 / 100 ))
  while IFS= read -r line || [ -n "$line" ]; do
    f="build/$(basename "$1" .txt)_l$i.txt"; printf '%s' "$line" > "$f"
    out+="${out:+,}drawtext=fontfile=$FONT:fontsize=$fs:fontcolor=white:borderw=7:bordercolor=black:textfile=$f:x=(w-text_w)/2:y=h*0.12+$i*$lh"
    i=$((i+1))
  done < "$1"
  echo "$out"
}
AD="drawtext=fontfile=$FONT:fontsize=34:fontcolor=white:box=1:boxcolor=black@0.45:boxborderw=12:textfile=build/ad.txt:x=40:y=60"
TAG="drawtext=fontfile=$FONT:fontsize=34:fontcolor=white:box=1:boxcolor=0xFF6A00@0.85:boxborderw=12:textfile=build/tag.txt:x=w-text_w-40:y=60"

scene() { # $1=image $2=sec $3=textfile $4=out $5=font size $6=zoom amount
  local n; n=$(awk "BEGIN{printf \"%d\", $2*$FPS}")
  ffmpeg -v error -y -loop 1 -i "$1" -frames:v "$n" -filter_complex \
    "[0:v]scale=${W}:${H}:force_original_aspect_ratio=increase,crop=${W}:${H},boxblur=30:3,eq=brightness=-0.12[bg];\
     [0:v]scale=2000:-2,zoompan=z='1+$6*on/$n':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=1000x1000:fps=$FPS[fg];\
     [bg][fg]overlay=(W-w)/2:560,$(title "$3" "$5"),$AD,$TAG,format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$4"
}

scene ref/tile.png     2.6 build/t1.txt build/s1.mp4 74 0.06
scene ref/tile.png     2.0 build/t2.txt build/s2.mp4 80 0.10
scene ref/corner.png   2.3 build/t3.txt build/s3.mp4 80 0.08
scene ref/features.png 2.3 build/t4.txt build/s4.mp4 74 0.05
scene ref/rinse.png    2.0 build/t5.txt build/s5.mp4 80 0.08
scene ref/box.png      2.6 build/t6.txt build/s6.mp4 70 0.05
printf "file 's1.mp4'\nfile 's2.mp4'\nfile 's3.mp4'\nfile 's4.mp4'\nfile 's5.mp4'\nfile 's6.mp4'\n" > build/list.txt
# 무음 오디오 트랙 포함 (플랫폼 호환용)
ffmpeg -v error -y -f concat -safe 0 -i build/list.txt -f lavfi -i anullsrc=r=44100:cl=stereo \
  -map 0:v -map 1:a -c:v copy -c:a aac -b:a 128k -shortest -movflags +faststart temnori_ep08.mp4
ffprobe -v error -show_entries format=duration -of csv=p=0 temnori_ep08.mp4
