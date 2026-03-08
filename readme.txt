cd C:\ws\java\p2p

& "C:\Program Files\nodejs\node.exe" .\node_modules\browser-sync\dist\bin.js start --proxy localhost:8080 --files "src/main/resources/static/css/**/*.css,src/main/resources/templates/**/*.html" --open false