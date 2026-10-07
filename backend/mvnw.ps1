param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Arguments
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$toolsDir = Join-Path $scriptDir "..\tools\apache-maven-3.9.6"
$resolvedHome = (Resolve-Path $toolsDir).Path

try {
    $fso = New-Object -ComObject Scripting.FileSystemObject
    $shortHome = $fso.GetFolder($resolvedHome).ShortPath
    $shortScript = $fso.GetFolder($scriptDir).ShortPath
} catch {
    $shortHome = $resolvedHome
    $shortScript = $scriptDir
}

$conf = Join-Path $shortHome "bin\m2.conf"
$jar = (Get-ChildItem -Path (Join-Path $resolvedHome "boot") -Filter "plexus-classworlds-*.jar" | Select-Object -First 1).FullName

try {
    $fso = New-Object -ComObject Scripting.FileSystemObject
    $shortJar = $fso.GetFile($jar).ShortPath
} catch {
    $shortJar = $jar
}

& java "-Dmaven.multiModuleProjectDirectory=$shortScript" "-Dclassworlds.conf=$conf" "-Dmaven.home=$shortHome" -classpath "$shortJar" org.codehaus.plexus.classworlds.launcher.Launcher @Arguments
exit $LASTEXITCODE
