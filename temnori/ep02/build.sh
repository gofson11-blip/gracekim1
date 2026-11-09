#!/usr/bin/env bash
# 템노리 2회차 릴스 합성 (18초, 1080x1920, 일레븐랩스 내레이션)
set -euo pipefail
cd "$(dirname "$0")"
FONT=/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc
W=1080; H=1920; FPS=30
mkdir -p build

printf '욕실 청소,\n아직도 허리 숙여서 하세요?' > build/t1.txt
printf '봉 연결하면\n서서 벽까지 쓱쓱' > build/t2.txt
printf '1분에 400번 회전\n변기도 손 안 대고' > build/t3.txt
printf '교체용 브러시 6종\n욕실 전체 OK' > build/t4.txt
printf 'IPX6 방수 · 무선 충전\n프로필 링크에서 확인 ↑' > build/t5.txt
printf '광고 · 쿠팡 파트너스' > build/ad.txt

title() { # $1=textfile — 줄마다 가운데 정렬
  local i=0 out="" line f
  while IFS= read -r line || [ -n "$line" ]; do
    f="build/$(basename "$1" .txt)_l$i.txt"; printf '%s' "$line" > "$f"
    out+="${out:+,}drawtext=fontfile=$FONT:fontsize=74:fontcolor=white:borderw=7:bordercolor=black:textfile=$f:x=(w-text_w)/2:y=h*0.13+$i*100"
    i=$((i+1))
  done < "$1"
  echo "$out"
}
AD="drawtext=fontfile=$FONT:fontsize=34:fontcolor=white:box=1:boxcolor=black@0.45:boxborderw=12:textfile=build/ad.txt:x=40:y=60"

still() { # $1=image $2=seconds $3=textfile $4=out
  local n; n=$(awk "BEGIN{printf \"%d\", $2*$FPS}")
  ffmpeg -v error -y -loop 1 -i "$1" -frames:v "$n" -filter_complex \
    "[0:v]scale=2160:3840:force_original_aspect_ratio=increase,crop=2160:3840,zoompan=z='min(zoom+0.0009,1.10)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=${W}x${H}:fps=$FPS,$(title "$3"),$AD,format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$4"
}

# 장면 길이: 3.1 / 3.1 / 3.6 / 3.1 / 5.1 = 18초 (내레이션 문장 경계에 맞춤)
ffmpeg -v error -y -i hook.mp4 -frames:v 93 -filter_complex \
  "[0:v]scale=${W}:${H}:force_original_aspect_ratio=increase,crop=${W}:${H},fps=$FPS,$(title build/t1.txt),$AD,format=yuv420p" \
  -c:v libx264 -preset medium -crf 20 -an build/s1.mp4
still scene_wall.png 3.1 build/t2.txt build/s2.mp4
still scene_toilet.png 3.6 build/t3.txt build/s3.mp4
still scene_flatlay.png 3.1 build/t4.txt build/s4.mp4
still scene_wall.png 5.1 build/t5.txt build/s5.mp4

printf "file 's1.mp4'\nfile 's2.mp4'\nfile 's3.mp4'\nfile 's4.mp4'\nfile 's5.mp4'\n" > build/list.txt
ffmpeg -v error -y -f concat -safe 0 -i build/list.txt -c copy build/video.mp4

ffmpeg -v error -y -i build/video.mp4 -i narration.mp3 -filter_complex \
  "[1:a]adelay=200|200,loudnorm=I=-14:TP=-1.5:LRA=11,apad,atrim=0:18,aresample=44100[a]" \
  -map 0:v -map "[a]" -c:v copy -c:a aac -b:a 160k -t 18 -movflags +faststart temnori_ep02.mp4
ffprobe -v error -show_entries format=duration -of csv=p=0 temnori_ep02.mp4
