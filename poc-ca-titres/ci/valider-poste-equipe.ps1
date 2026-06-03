param(
    [string]$Racine = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [switch]$SkipNpmInstall,
    [switch]$SkipPlaywright,
    [switch]$SkipQwen,
    [switch]$SkipNetwork,
    [string]$QwenEndpoint = "https://dashscope-intl.aliyuncs.com/compatible-mode/v1"
)

Set-StrictMode -Version Latest

$ErrorActionPreference = "Continue"
$global:Failures = 0

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "== $Message ==" -ForegroundColor Cyan
}

function Add-Failure {
    param([string]$Message)
    $global:Failures++
    Write-Host "KO - $Message" -ForegroundColor Red
}

function Add-Success {
    param([string]$Message)
    Write-Host "OK - $Message" -ForegroundColor Green
}

function Test-RequiredCommand {
    param(
        [string]$Name,
        [string]$Label
    )

    $cmd = Get-Command $Name -ErrorAction SilentlyContinue
    if ($null -eq $cmd) {
        Add-Failure "$Label introuvable dans le PATH ($Name)"
        return $false
    }

    Add-Success "$Label detecte : $($cmd.Source)"
    return $true
}

function Invoke-Checked {
    param(
        [string]$Label,
        [string]$WorkingDirectory,
        [string]$Command,
        [string[]]$Arguments
    )

    Write-Step $Label
    $argText = $Arguments -join " "
    Write-Host ("Commande : {0} {1}" -f $Command, $argText) -ForegroundColor DarkGray
    Write-Host "Dossier  : $WorkingDirectory" -ForegroundColor DarkGray

    Push-Location $WorkingDirectory
    try {
        & $Command @Arguments
        if ($LASTEXITCODE -eq 0) {
            Add-Success $Label
        } else {
            Add-Failure "$Label (code $LASTEXITCODE)"
        }
    } catch {
        Add-Failure "$Label ($($_.Exception.Message))"
    } finally {
        Pop-Location
    }
}

function Test-NetEndpoint {
    param(
        [string]$Url,
        [string]$Label
    )

    try {
        $resp = Invoke-WebRequest -Uri $Url -UseBasicParsing -Method Head -TimeoutSec 20
        Add-Success "$Label joignable (HTTP $($resp.StatusCode))"
    } catch {
        $msg = $_.Exception.Message
        if ($msg -match 'SSL|TLS|trust relationship|certificat|certificate|secure channel') {
            Add-Failure "$Label - ERREUR CERTIFICAT : proxy a inspection TLS / MITM probable ($msg)"
        } elseif ($_.Exception.Response) {
            $code = [int]$_.Exception.Response.StatusCode
            Add-Success "$Label joignable (HTTP $code - endpoint atteint, reponse recue)"
        } else {
            Add-Failure "$Label injoignable - bloque / DNS / timeout ($msg)"
        }
    }
}

Write-Host "Validation poste equipe - POC Titres" -ForegroundColor White
Write-Host "Racine : $Racine"
Write-Host ""
Write-Host "IMPORTANT : a executer SUR UN VRAI POSTE CA, SUR LE RESEAU CA (proxy inclus)." -ForegroundColor Yellow
Write-Host "Depuis le reseau perso du formateur, ce pre-flight ne prouve RIEN sur le proxy banque." -ForegroundColor Yellow

if (-not (Test-Path -LiteralPath $Racine)) {
    Add-Failure "Racine POC introuvable : $Racine"
    exit 1
}

$backend = $Racine
$jsf = Join-Path $Racine "jsf-legacy"
$angular = Join-Path $Racine "angular-front"
$npmCache = Join-Path $Racine ".npm-cache"
$e2e = Join-Path $Racine "e2e"

Write-Step "Pre-check outillage"
$hasJava = Test-RequiredCommand "java" "Java"
$hasMaven = Test-RequiredCommand "mvn" "Maven"
$hasNode = Test-RequiredCommand "node" "Node"
$hasNpm = Test-RequiredCommand "npm" "npm"
$hasGit = Test-RequiredCommand "git" "Git"

if ($hasJava) { & java -version }
if ($hasMaven) { & mvn -version }
if ($hasNode) { & node -v }
if ($hasNpm) { & npm -v }

if ($hasMaven) {
    Invoke-Checked "Backend Spring - mvn clean verify" $backend "mvn" @("clean", "verify")
    Invoke-Checked "JSF legacy - mvn clean verify" $jsf "mvn" @("clean", "verify")
} else {
    Write-Host "INFO - builds Java/JSF ignores faute de Maven." -ForegroundColor Yellow
}

if ($hasNpm) {
    if (-not $SkipNpmInstall) {
        Invoke-Checked "Angular - npm install" $angular "npm" @("install", "--cache", $npmCache)
    }
    Invoke-Checked "Angular - npm test" $angular "npm" @("test", "--", "--watch=false", "--browsers=ChromeHeadless")
    Invoke-Checked "Angular - npm run build" $angular "npm" @("run", "build")
} else {
    Write-Host "INFO - tests Angular ignores faute de npm." -ForegroundColor Yellow
}

# Sonde Playwright : le e2e utilise browserName=chromium => binaire TELECHARGE.
# 'playwright install' est un download live a travers le proxy (vecteur de panne local classique).
if ($hasNpm -and -not $SkipPlaywright) {
    if (Test-Path -LiteralPath $e2e) {
        Invoke-Checked "Playwright - npm install (e2e)" $e2e "npm" @("install", "--cache", $npmCache)
        Invoke-Checked "Playwright - download Chromium (npx playwright install)" $e2e "npx" @("playwright", "install", "chromium")
    } else {
        Add-Failure "Dossier e2e introuvable : $e2e (sonde Playwright impossible)"
    }
} else {
    Write-Host "INFO - sonde Playwright ignoree (-SkipPlaywright ou npm absent)." -ForegroundColor Yellow
}

# Sonde Docker (INFORMATIVE) : Docker absent ne casse NI le local-host NI le Codespace cloud,
# seulement un eventuel devcontainer local (hors plan). Affiche sans compter d'echec.
Write-Step "Sonde Docker (informative)"
$dockerCmd = Get-Command docker -ErrorAction SilentlyContinue
if ($null -eq $dockerCmd) {
    Write-Host "INFO - Docker absent : sans impact sur local-host et Codespaces (devcontainer local non requis)." -ForegroundColor Yellow
} else {
    Add-Success "Docker detecte : $($dockerCmd.Source) (optionnel ; utile seulement pour un devcontainer local)"
}

if (-not $SkipNetwork) {
    Write-Step "Pre-check reseau - chemin LOCAL (git + Copilot)"

    if ($hasGit) {
        Invoke-Checked "GitHub HTTPS - git ls-remote (clone sur 443)" $Racine "git" @("ls-remote", "https://github.com/github/gitignore.git")
    }

    Test-NetEndpoint "https://github.com" "GitHub.com"
    # Detecteur MITM cle : une erreur de certificat ici = proxy a inspection TLS => Copilot ET le tunnel
    # Codespaces tombent tous les deux. Remede = ticket DSI (CA racine + allowlist) EN AMONT.
    Test-NetEndpoint "https://api.githubcopilot.com" "Copilot backend (api.githubcopilot.com)"

    Write-Step "Pre-check reseau - filet Codespaces (a tester DEPUIS le poste CA)"
    Test-NetEndpoint "https://github.dev" "Codespaces (github.dev)"
    Test-NetEndpoint "https://global.rel.tunnels.api.visualstudio.com" "Tunnel Codespaces (visualstudio.com)"

    if (-not $SkipQwen) {
        Write-Step "Pre-check reseau - filet Qwen/DashScope (geo-blocable en local)"
        # Le filet anti-quota Copilot Free passe par un cloud chinois : cible de blocage geo d'un proxy
        # bancaire FR. S'il est KO ici, acter que le filet ne tient QUE cote Codespaces.
        Test-NetEndpoint $QwenEndpoint "Qwen/DashScope"
    } else {
        Write-Host "INFO - sonde Qwen/DashScope ignoree (-SkipQwen)." -ForegroundColor Yellow
    }
}

Write-Step "Controles OP4 manuels a signer (ce que le script NE PEUT PAS automatiser)"
Write-Host "- Licence GitHub Copilot active dans le compte du participant (sinon completion morte)."
Write-Host "- Authentification GitHub sans boucle SSO."
Write-Host "- Reponse Copilot reelle (completion) obtenue dans un fichier Java fictif."
Write-Host "- AGENT MODE + un custom agent reellement EXECUTABLES dans Eclipse/STS sur ce poste"
Write-Host "  (capacite recente, plugin open source 21 mai 2026 : maturite a verifier ICI)."
Write-Host "- Bascule de filet validee : quota Copilot Free atteint => Qwen prend le relais."
Write-Host ""
Write-Host "NOTE : joignabilite reseau (github.com, Copilot, Codespaces, Qwen) et proxy a inspection"
Write-Host "TLS sont desormais AUTOMATISES ci-dessus. Un KO 'ERREUR CERTIFICAT' sur le Copilot backend"
Write-Host "= proxy MITM => ouvrir le ticket DSI (CA racine + allowlist) avant J1."

Write-Host ""
if ($global:Failures -eq 0) {
    Write-Host "VALIDATION OK - poste pret pour la formation." -ForegroundColor Green
    exit 0
}

Write-Host "VALIDATION KO - $global:Failures point(s) a corriger avant la formation." -ForegroundColor Red
exit 1
