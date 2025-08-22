rmdir /S /Q ".\out\runtime-image"
rmdir /S /Q ".\out\jachat"
rmdir /S /Q ".\out\myjar"
mkdir ".\out\myjar"
copy "target\javava-1.0-SNAPSHOT.jar" ".\out\myjar"

jlink --module-path "S:\Dekstop\javafx-sdk-24.0.1\lib" ^
--add-modules javafx.controls,javafx.fxml,java.logging ^
--output ".\out\runtime-image" ^
--strip-debug ^
--compress 2 ^
--no-header-files ^
--no-man-pages && ^
jpackage --input ".\out\myjar" ^
--win-console ^
--main-jar javava-1.0-SNAPSHOT.jar ^
--main-class core.Main ^
--type app-image ^
--name jachat ^
--dest ".\out" ^
--runtime-image ".\out\runtime-image" ^
--vendor "eklepser"

xcopy ".\out\bin\*" ".\out\jachat\runtime\bin\" /Y
copy ".\src\main\resources\key.json" ".\out\jachat\app\" /Y