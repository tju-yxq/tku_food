# Test login functionality

# Test 1: Try to login with email
Write-Host "=== Test 1: Login with email ==="
try {
    $loginBody = @{
        account = "test@example.com"
        password = "123456"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "http://localhost:8090/api/users/login" -Method Post -ContentType "application/json" -Body $loginBody
    Write-Host "Login successful!" -ForegroundColor Green
    Write-Host "Token: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "Login failed:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red

    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "Error details: $responseBody" -ForegroundColor Red
    }
}

# Test 2: Try to login with ID
Write-Host "`n=== Test 2: Login with ID ==="
try {
    $loginBody2 = @{
        account = "1"
        password = "123456"
    } | ConvertTo-Json

    $response2 = Invoke-RestMethod -Uri "http://localhost:8090/api/users/login" -Method Post -ContentType "application/json" -Body $loginBody2
    Write-Host "ID login successful!" -ForegroundColor Green
    Write-Host "Token: $($response2.data)" -ForegroundColor Green
} catch {
    Write-Host "ID login failed:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

# Test 3: Check application health
Write-Host "`n=== Test 3: Check application status ==="
try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8090/actuator/health" -Method Get
    Write-Host "Application status: $($healthCheck.status)" -ForegroundColor Green
} catch {
    Write-Host "Cannot access health check endpoint" -ForegroundColor Red
}
