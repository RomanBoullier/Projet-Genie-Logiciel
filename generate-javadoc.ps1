$javaFxLib = if (Test-Path "C:\javafx-sdk-21.0.11\lib") {
    "C:\javafx-sdk-21.0.11\lib"
} elseif (Test-Path "C:\javafx\lib") {
    "C:\javafx\lib"
} else {
    throw "JavaFX SDK not found. Please install it under C:\javafx-sdk-21.0.11 or C:\javafx."
}

$sourceFiles = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }

New-Item -ItemType Directory -Force -Path "javadoc" | Out-Null

javadoc -d javadoc --module-path $javaFxLib --add-modules javafx.controls,javafx.fxml -sourcepath src $sourceFiles
