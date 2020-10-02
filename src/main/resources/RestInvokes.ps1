# Powershell-Script zum generieren von Rest-Aufrufen
# Dient dem einfachen Erstellen von Anfragen im JSON-Format

$Uri = "http://localhost$(Read-Host -Prompt "Bitte URI Vervollständigen http://localhost...")"
Write-Host "Uri ist $Uri"

$Method = Read-Host -Prompt "Bitte Method Festlegen (Get, Put, Post, Delete,...)"
Write-Host "Es wird $Method Ausgeführt"

$body = Read-Host -Prompt 'Bitte Body eingeben: wert1=test;wert2=super;...'
$bodyjson = ConvertTo-Json (ConvertFrom-StringData($body.split(';') | Out-String))
Write-Host $bodyjson

Invoke-RestMethod -Uri $Uri -Method $Method -Body $bodyjson