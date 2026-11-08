#!/usr/bin/env bash
# 템노리 1회차 릴스 합성 (15초, 1080x1920, 일레븐랩스 내레이션 포함 — 배경음악은 인스타그램 앱에서 작게 추가)
set -euo pipefail
cd "$(dirname "$0")"
FONT=/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc
W=1080; H=1920; FPS=30
mkdir -p build

# 자막 (내레이션 구간에 맞춤)
printf '배수구 머리카락,\n아직도 손으로 빼세요?' > build/t1.txt
printf '올려두기만 하면 끝' > build/t2.txt
printf '머리카락은 걸러주고\n물은 쏙' > build/t3.txt
printf '욕실 · 세면대 · 싱크대\n어디든 OK' > build/t4.txt
printf '2개입\n프로필 링크에서 확인 ↑' > build/t5.txt
printf '광고 · 쿠팡 파트너스' > build/ad.txt

title() { # $1=textfile — 줄마다 가운데 정렬
  local i=0 out="" line f
  while IFS= read -r line || [ -n "$line" ]; do
    f="build/$(basename "$1" .txt)_l$i.txt"; printf '%s' "$line" > "$f"
    out+="${out:+,}drawtext=fontfile=$FONT:fontsize=78:fontcolor=white:borderw=7:bordercolor=black:textfile=$f:x=(w-text_w)/2:y=h*0.13+$i*104"
    i=$((i+1))
  done < "$1"
  echo "$out"
}
AD="drawtext=fontfile=$FONT:fontsize=34:fontcolor=white:box=1:boxcolor=black@0.45:boxborderw=12:textfile=build/ad.txt:x=40:y=60"

# 이미지 장면: 천천히 확대 (Ken Burns). $2는 소수 초 가능
still() { # $1=image $2=seconds $3=textfile $4=out
  local n; n=$(awk "BEGIN{printf \"%d\", $2*$FPS}")
  ffmpeg -v error -y -loop 1 -i "$1" -frames:v "$n" -filter_complex \
    "[0:v]scale=2160:3840:force_original_aspect_ratio=increase,crop=2160:3840,zoompan=z='min(zoom+0.0009,1.10)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=${W}x${H}:fps=$FPS,$(title "$3"),$AD,format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$4"
}

# 장면 길이: 3.0 / 2.0 / 3.0 / 3.6 / 3.4 = 15초 (내레이션 문장 경계에 맞춤)
ffmpeg -v error -y -i hook.mp4 -frames:v 90 -filter_complex \
  "[0:v]scale=${W}:${H},fps=$FPS,$(title build/t1.txt),$AD,format=yuv420p" \
  -c:v libx264 -preset medium -crf 20 -an build/s1.mp4
still scene2.png 2.0 build/t2.txt build/s2.mp4
still scene3.png 3.0 build/t3.txt build/s3.mp4
still scene4.png 3.6 build/t4.txt build/s4.mp4
still scene2.png 3.4 build/t5.txt build/s5.mp4

printf "file 's1.mp4'\nfile 's2.mp4'\nfile 's3.mp4'\nfile 's4.mp4'\nfile 's5.mp4'\n" > build/list.txt
ffmpeg -v error -y -f concat -safe 0 -i build/list.txt -c copy build/video.mp4

# 내레이션 0.2초 뒤 시작, 음량 정규화, 15초에 맞춰 패딩
ffmpeg -v error -y -i build/video.mp4 -i narration.mp3 -filter_complex \
  "[1:a]adelay=200|200,loudnorm=I=-14:TP=-1.5:LRA=11,apad,atrim=0:15,aresample=44100[a]" \
  -map 0:v -map "[a]" -c:v copy -c:a aac -b:a 160k -t 15 -movflags +faststart temnori_ep01.mp4
ffprobe -v error -show_entries format=duration -of csv=p=0 temnori_ep01.mp4
