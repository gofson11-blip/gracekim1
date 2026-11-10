#!/usr/bin/env bash
# 템노리 4회차 릴스 합성 (12.5초 v2, 1080x1920, 일레븐랩스 내레이션)
set -euo pipefail
cd "$(dirname "$0")"
FONT=/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc
W=1080; H=1920; FPS=30
mkdir -p build

printf '이불 털어도\n먼지만 날리죠?' > build/t1.txt
printf '침대 위를\n쓱 밀기만 하면' > build/t2.txt
printf '흡입력 14000Pa\n먼지가 이만큼' > build/t3.txt
printf '소파·쿠션까지\n한 번에' > build/t4.txt
printf 'UV 램프 + 듀얼 브러시\n프로필 링크에서 확인 ↑' > build/t5.txt
printf '광고 · 쿠팡 파트너스' > build/ad.txt

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

# 세로 이미지 장면 (천천히 확대)
still() { # $1=image $2=sec $3=textfile $4=out
  local n; n=$(frames "$2")
  ffmpeg -v error -y -loop 1 -i "$1" -frames:v "$n" -filter_complex \
    "[0:v]scale=2160:3840:force_original_aspect_ratio=increase,crop=2160:3840,zoompan=z='min(zoom+0.0009,1.10)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=${W}x${H}:fps=$FPS,$(title "$3"),$AD,format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$4"
}
# v2 (11/11 성과 분석 반영): 결과 장면을 0초에, 내레이션 지연·무음 제거, 14.5초 → 약 12.5초
# 내레이션 구간(초): A 0-1.75 / B 2.22-4.07 / C 4.40-7.52 / D 8.02-9.95 / E 10.42-13.79
# 첫 장면: 먼지 가득한 집진통 + 빠른 확대 + 큰 자막
zoomstill() { # $1=image $2=sec $3=textfile $4=out $5=zoom step $6=font size
  local n; n=$(frames "$2")
  ffmpeg -v error -y -loop 1 -i "$1" -frames:v "$n" -filter_complex \
    "[0:v]scale=2160:3840:force_original_aspect_ratio=increase,crop=2160:3840,zoompan=z='min(zoom+$5,1.25)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=$n:s=${W}x${H}:fps=$FPS,$(title "$3" | sed "s/fontsize=70/fontsize=$6/g; s/\*96/*118/g"),$AD,format=yuv420p" \
    -r $FPS -c:v libx264 -preset medium -crf 20 -an "$4"
}
zoomstill scene_dust.png 1.85 build/t1.txt build/s1.mp4 0.004 88
still scene_bed.png 1.95 build/t2.txt build/s2.mp4
zoomstill scene_dust.png 3.2 build/t3.txt build/s3.mp4 0.0006 70
still scene_sofa.png 2.0 build/t4.txt build/s4.mp4
still scene_bed.png 3.5 build/t5.txt build/s5.mp4

printf "file 's1.mp4'\nfile 's2.mp4'\nfile 's3.mp4'\nfile 's4.mp4'\nfile 's5.mp4'\n" > build/list.txt
ffmpeg -v error -y -f concat -safe 0 -i build/list.txt -c copy build/video.mp4

# 내레이션: 문장 사이 무음을 잘라 장면 길이에 맞춰 이어 붙임
seg() { echo "[0:a]atrim=$1:$2,asetpts=PTS-STARTPTS,afade=t=in:d=0.03,afade=t=out:st=$(awk "BEGIN{print $2-$1-0.04}"):d=0.04,apad=whole_dur=$3[$4]"; }
ffmpeg -v error -y -i narration.mp3 -filter_complex \
  "$(seg 0 1.75 1.85 a);$(seg 2.22 4.07 1.95 b);$(seg 4.40 7.52 3.2 c);$(seg 8.02 9.95 2.0 d);$(seg 10.42 13.79 3.5 e);[a][b][c][d][e]concat=n=5:v=0:a=1,loudnorm=I=-14:TP=-1.5:LRA=11,aresample=44100[out]" \
  -map "[out]" -c:a pcm_s16le build/narr.wav
ffmpeg -v error -y -i build/video.mp4 -i build/narr.wav -map 0:v -map 1:a -c:v copy -c:a aac -b:a 160k -t 12.5 -movflags +faststart temnori_ep04.mp4
ffprobe -v error -show_entries format=duration -of csv=p=0 temnori_ep04.mp4
