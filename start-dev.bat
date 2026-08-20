@echo off
chcp 65001 >nul
echo ================================================================
echo   档案管理系统 DMS - Start Backend + Frontend
echo ================================================================
echo.
echo   Frontend (UI):      http://localhost:5173
echo   Backend  (API):     http://localhost:8081
echo   API Doc (Knife4j):  http://localhost:8081/doc.html
echo.
echo   Starting backend and frontend in separate windows...
echo   (Close the two new windows to stop the services)
echo.
start "DMS-Backend" cmd /k "java -jar target\Document-Management-System-0.0.1-SNAPSHOT.jar --server.port=8081"
pushd dms-ui
start "DMS-Frontend" cmd /k "npm run dev"
popd
echo.
echo   Both started. Open http://localhost:5173 in your browser.
echo.
pause
