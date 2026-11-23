$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$sourceRoot = Join-Path $projectRoot 'src'
$buildRoot = Join-Path $projectRoot 'build'
$classRoot = Join-Path $buildRoot 'classes'
$manifest = Join-Path $buildRoot 'MANIFEST.MF'
$jarPath = Join-Path $buildRoot 'LivroCaixa.jar'

# As classes compiladas são temporárias. O usuário precisa ver apenas o JAR final.
if (Test-Path -LiteralPath $classRoot) {
    Remove-Item -LiteralPath $classRoot -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $classRoot | Out-Null

try {
    $sources = Get-ChildItem -Path $sourceRoot -Recurse -Filter '*.java' |
        ForEach-Object FullName
    if (-not $sources) { throw 'Nenhum arquivo Java encontrado.' }

    & javac -encoding UTF-8 -d $classRoot $sources
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }

    Set-Content -LiteralPath $manifest -Encoding ascii -Value "Main-Class: livrocaixa.Main`r`n"
    & jar --create --file $jarPath --manifest $manifest -C $classRoot .
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao criar o JAR.' }
} finally {
    if (Test-Path -LiteralPath $classRoot) {
        Remove-Item -LiteralPath $classRoot -Recurse -Force
    }
    if (Test-Path -LiteralPath $manifest) {
        Remove-Item -LiteralPath $manifest -Force
    }
}

Write-Host "Compilado: $jarPath"
Write-Host 'Arquivos temporários .class removidos.'
