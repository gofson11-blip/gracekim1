#!/usr/bin/env bash
# 템노리 3회차 릴스 합성 (17초, 1080x1920, 일레븐랩스 내레이션)
set -euo pipefail
cd "$(dirname "$0")"
FONT=/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc
W=1080; H=1920; FPS=30
mkdir -p build

printf '물걸레질,\n아직도 무릎 꿇고 하세요?' > build/t1.txt
printf '높이 8.8cm\n소파 밑까지 쏙' > build/t2.txt
printf '센서 11개로 부딪힘 걱정 없이\n계단 앞에선 알아서 멈춤' > build/t3.txt
printf '도서관보다 조용해서\n밤에도 OK' > build/t4.txt
printf '에브리봇 엣지2\n프로필 링크에서 확인 ↑' > build/t5.txt
printf '광고 · 쿠팡 파트너스' > build/ad.txt
printf '*소음: 제조사 공인시험기관 측정 기준' > build/note.txt

title() { # $1=textfile — 줄마다 가운데 정렬
  local i=0 out="" line f
  while IFS= read -r line || [ -n "$line" ]; do
    f="build/$(basename "$1" .txt)_l$i.txt"; printf '%s' "$line" > "$f"
    out+="${out:+,}drawtext=fontfile=$FONT:fontsize=70:fontcolor=white:borderw=7:bordercolor=black:textfile=$f:x=(w-text_w)/2:y=h*0.13+$i*96"
    i=$((i+1))
  done < "$1"
  echo "$out"
}
AD="drawtext=fontfile=$FONT:fontsize=34:fontcolor=white:box=1:boxcolor=black@0.45:boxborderw=12:textfile=build/ad.txt:x=40:y=60"
NOTE="drawtext=fontfile=$FONT:fontsize=30:fontcolor=white:box=1:boxcolor=black@0.45:boxborderw=10:textfile=build/note.txt:x=(w-text_w)/2:y=h-170"

frames() { awk "BEGIN{printf \"%d\", $1*$FPS}"; }

# 가로 이미지 → 흐린 배경 위에 원본을 가운데 배치 + 천천히 확대
landscape() { # $1=image $2=sec $3=textfile $4=out [$5=extra filter]
  local n; n=$(frames "$2")
  ffmpeg -v error -y -loop 1 -i "$1" -frames:v "$n" -filter_complex \
    "[0:v]scale=${W}:${H}:force_original_aspect_ratio=increase,crop=${W}:${H},boxblur=30:3,eq=brightness=-0.08[bg];[0:v]scale=${W}:-2[fg];[bg][fg]overlay=(W-w)/2:(H-h)/2+120,scale=2160:3840,zoompan=z='min(zoom+0.0007,1.08)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=${W}x${H}:fps=$FPS,$(title "$3"),$AD${5:+,$5},format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$4"
}

# 3분할로 나온 밤 장면: 가운데 세로 패널만 잘라 사용
night() { # $1=sec $2=textfile $3=out $4=extra
  local n; n=$(frames "$1")
  ffmpeg -v error -y -loop 1 -i scene_night.png -frames:v "$n" -filter_complex \
    "[0:v]crop=432:768:472:0,scale=2160:3840,zoompan=z='min(zoom+0.0007,1.08)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=${W}x${H}:fps=$FPS,$(title "$2"),$AD${4:+,$4},format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$3"
}

# 장면 길이: 3.1 / 3.7 / 5.2 / 3.2 / 1.8 = 17초 (내레이션 문장 경계에 맞춤)
ffmpeg -v error -y -i hook.mp4 -frames:v 93 -filter_complex \
  "[0:v]scale=${W}:${H},fps=$FPS,$(title build/t1.txt),$AD,format=yuv420p" \
  -c:v libx264 -preset medium -crf 20 -an build/s1.mp4
landscape scene_sofa.png 3.7 build/t2.txt build/s2.mp4
landscape scene_edge.png 5.2 build/t3.txt build/s3.mp4
night 3.2 build/t4.txt build/s4.mp4 "$NOTE"
landscape scene_sofa.png 1.8 build/t5.txt build/s5.mp4

printf "file 's1.mp4'\nfile 's2.mp4'\nfile 's3.mp4'\nfile 's4.mp4'\nfile 's5.mp4'\n" > build/list.txt
ffmpeg -v error -y -f concat -safe 0 -i build/list.txt -c copy build/video.mp4

ffmpeg -v error -y -i build/video.mp4 -i narration.mp3 -filter_complex \
  "[1:a]adelay=200|200,loudnorm=I=-14:TP=-1.5:LRA=11,apad,atrim=0:17,aresample=44100[a]" \
  -map 0:v -map "[a]" -c:v copy -c:a aac -b:a 160k -t 17 -movflags +faststart temnori_ep03.mp4
ffprobe -v error -show_entries format=duration -of csv=p=0 temnori_ep03.mp4
