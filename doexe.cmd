rmdir /S /Q ".\out\runtime-image"
rmdir /S /Q ".\out\gamemaster"
rmdir /S /Q ".\out\myjar"
mkdir ".\out\myjar"
copy "target\game-master-1.0-SNAPSHOT.jar" ".\out\myjar"
jlink --module-path ".\res\javafx-sdk-24.0.1\lib" ^
--add-modules javafx.controls,javafx.fxml,java.logging ^
--output ".\out\runtime-image" ^
--strip-debug ^
--compress 2 ^
--no-header-files ^
--no-man-pages && ^
jpackage --input ".\out\myjar" ^
--main-jar game-master-1.0-SNAPSHOT.jar ^
--main-class core.Main ^
--type app-image ^
--name GameMaster ^
--dest ".\out" ^
--runtime-image ".\out\runtime-image" ^
--vendor "eklepser" ^
--icon ".\res\icon.ico"
xcopy ".\res\bin\*" ".\out\gamemaster\runtime\bin\" /Y
copy ".\src\main\resources\key.json" ".\out\gamemaster\app\" /Y