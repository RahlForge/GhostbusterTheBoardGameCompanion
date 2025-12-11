# BOM Character Policy

## Background
Byte Order Mark (BOM) characters at the beginning of source files can cause compilation issues in Kotlin/Java projects. These invisible characters (UTF-8 BOM: bytes `EF BB BF` or `0xFEFF`) can prevent the compiler from properly recognizing class definitions.

## Policy
**Any time the `replace_string_in_file` tool is used, the file will be automatically checked for BOM characters and cleaned if necessary.**

## What Was Done
- Scanned all `.kt` files in the project (22 files total)
- Removed BOM characters from all Kotlin source files
- Files are now saved with UTF-8 encoding without BOM

## How to Manually Check for BOM
```powershell
# Check a single file for BOM
Get-Content "path\to\file.kt" -Encoding Byte -TotalCount 3

# If output is "239, 187, 191" then BOM is present
```

## How to Remove BOM Manually
```powershell
# Remove BOM from a single file
$content = Get-Content "path\to\file.kt" -Raw
$content = $content.TrimStart([char]0xFEFF)
Set-Content "path\to\file.kt" -Value $content -NoNewline -Encoding UTF8

# Remove BOM from all Kotlin files in project
$files = Get-ChildItem -Path "app\src\main\java" -Filter "*.kt" -Recurse
foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    if ($content) {
        $content = $content.TrimStart([char]0xFEFF)
        Set-Content $file.FullName -Value $content -NoNewline -Encoding UTF8
    }
}
```

## Files Cleaned (December 10, 2025)
All 22 `.kt` files in the `app/src/main/java` directory have been cleaned of BOM characters.

## Prevention
- Configure your IDE to save files as UTF-8 without BOM
- In IntelliJ IDEA: Settings → Editor → File Encodings → "Transparent native-to-ascii conversion" should be disabled
- Always use UTF-8 encoding without BOM for Kotlin source files

