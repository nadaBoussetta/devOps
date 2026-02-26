@echo off
echo Lancement du backend...
start cmd /k "cd backend\server && mvn spring-boot:run"

echo Lancement du frontend...
start cmd /k "cd frontend && python -m http.server 5500"

echo Tout est lancé ! Ouvrez le navigateur sur http://localhost:5500/index.html
pause