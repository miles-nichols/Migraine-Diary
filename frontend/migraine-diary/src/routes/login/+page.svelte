<script>
  import { goto } from '$app/navigation';
  let isLogin = true;
  let username = '';
  let password = '';
  let confirmPassword = '';
  let error = '';
  let email = '';
  let isLoading = false;
  let rememberMe = false;

  function switchPage() {
    isLogin = !isLogin;
    error = '';
    username = '';
    password = '';
    confirmPassword = '';
    email = '';
  }

  async function handleSubmit() {
    isLoading = true;
    error = '';
    
    // Validate fields
    if (!username || !password) {
      error = 'Please fill in all fields.';
      isLoading = false;
      return;
    }
    if (!isLogin && password !== confirmPassword) {
      error = 'Passwords do not match.';
      isLoading = false;
      return;
    }
    if (!isLogin && !email) {
      error = 'Email is required for registration.';
      isLoading = false;
      return;
    }

    try {
      const url = isLogin ? 'http://localhost:8080/user/login' : 'http://localhost:8080/user/register';
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(isLogin ? {username, password} : {username, password, email}),
      });

      let result;
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        result = await response.json();
      } else {
        result = { message: await response.text() };
      }

      if (response.ok) {
        localStorage.setItem('loggedIn', 'true');
        localStorage.setItem('username', result.username || username);
        localStorage.setItem('token', result.token || result.jwtToken || result.accessToken);
        
        if (rememberMe) {
          localStorage.setItem('rememberMe', 'true');
        }
        
        goto('/');
      } else {
        error = result.message || result.error || `Request failed with status ${response.status}`;
      }
    } catch (err) {
      console.error('Auth failed:', err);
      error = 'Network error. Please check your connection and try again.';
    } finally {
      isLoading = false;
    }
  }
</script>

<main>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h1>{isLogin ? 'Welcome Back' : 'Create Account'}</h1>
        <p class="auth-subtitle">
          {isLogin ? 'Sign in to your Migraine Diary' : 'Join us to track your health journey'}
        </p>
      </div>
      
      <form on:submit|preventDefault={handleSubmit} class="auth-form">
        <div class="input-group">
          <input 
            type="text" 
            placeholder="Username" 
            bind:value={username} 
            required
            class="auth-input"
            disabled={isLoading}
          />
          <span class="input-icon">👤</span>
        </div>
        
        <div class="input-group">
          <input 
            type="password" 
            placeholder="Password" 
            bind:value={password} 
            required
            class="auth-input"
            disabled={isLoading}
          />
          <span class="input-icon">🔒</span>
        </div>
        
        {#if !isLogin}
          <div class="input-group">
            <input 
              type="password" 
              placeholder="Confirm Password" 
              bind:value={confirmPassword} 
              required
              class="auth-input"
              class:input-error={confirmPassword && password !== confirmPassword}
              disabled={isLoading}
            />
            <span class="input-icon">✅</span>
          </div>
          
          <div class="input-group">
            <input 
              type="email" 
              placeholder="Email Address" 
              bind:value={email} 
              required
              class="auth-input"
              disabled={isLoading}
            />
            <span class="input-icon">📧</span>
          </div>
        {/if}
        
        {#if isLogin}
          <label class="remember-me">
            <input type="checkbox" bind:checked={rememberMe} disabled={isLoading} />
            <span class="checkmark"></span>
            Remember me
          </label>
        {/if}
        
        {#if error}
          <div class="error-message">
            <span class="error-icon">⚠️</span>
            {error}
          </div>
        {/if}
        
        <button type="submit" class="auth-button" disabled={isLoading}>
          {#if isLoading}
            <span class="loading-spinner"></span>
          {/if}
          <span class="button-text">
            {isLoading ? 'Processing...' : isLogin ? 'Sign In' : 'Create Account'}
          </span>
        </button>
      </form>
      
      <div class="auth-footer">
        <p class="toggle-text">
          {isLogin ? "Don't have an account?" : "Already have an account?"}
        </p>
        <button class="toggle-button" on:click={switchPage} disabled={isLoading}>
          {isLogin ? 'Sign Up' : 'Sign In'}
        </button>
      </div>
    </div>
  </div>
</main>

<style>
  main {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 1rem;
  }

  .auth-container {
    width: 100%;
    max-width: 400px;
  }

  .auth-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    padding: 2.5rem;
    border-radius: 1.5rem;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
  }

  .auth-header {
    text-align: center;
    margin-bottom: 2rem;
  }

  .auth-header h1 {
    color: #2d3748;
    font-size: 2rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .auth-subtitle {
    color: #718096;
    font-size: 0.95rem;
    margin: 0;
  }

  .auth-form {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .input-group {
    position: relative;
  }

  .auth-input {
    width: 100%;
    padding: 1rem 1rem 1rem 3rem;
    border: 2px solid #e2e8f0;
    border-radius: 0.75rem;
    font-size: 1rem;
    transition: all 0.3s ease;
    background: #f7fafc;
    box-sizing: border-box;
  }

  .auth-input:focus {
    outline: none;
    border-color: #667eea;
    background: white;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    transform: translateY(-1px);
  }

  .auth-input:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .input-icon {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #a0aec0;
    pointer-events: none;
  }

  .input-error {
    border-color: #fc8181;
    background: #fed7d7;
  }

  .input-error:focus {
    border-color: #e53e3e;
    box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.1);
  }

  .remember-me {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    color: #4a5568;
    font-size: 0.9rem;
    cursor: pointer;
    padding: 0.5rem 0;
  }

  .remember-me input {
    display: none;
  }

  .checkmark {
    width: 1.25rem;
    height: 1.25rem;
    border: 2px solid #cbd5e0;
    border-radius: 0.375rem;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
  }

  .remember-me input:checked + .checkmark {
    background: #667eea;
    border-color: #667eea;
  }

  .remember-me input:checked + .checkmark::after {
    content: '✓';
    color: white;
    font-size: 0.8rem;
  }

  .error-message {
    background: #fed7d7;
    color: #c53030;
    padding: 0.75rem 1rem;
    border-radius: 0.5rem;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    border-left: 4px solid #f56565;
  }

  .error-icon {
    font-size: 1.1rem;
  }

  .auth-button {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    padding: 1rem 2rem;
    border-radius: 0.75rem;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    position: relative;
    overflow: hidden;
  }

  .auth-button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
  }

  .auth-button:active:not(:disabled) {
    transform: translateY(0);
  }

  .auth-button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none !important;
  }

  .loading-spinner {
    width: 1rem;
    height: 1rem;
    border: 2px solid transparent;
    border-top: 2px solid white;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }

  .auth-footer {
    text-align: center;
    margin-top: 1.5rem;
    padding-top: 1.5rem;
    border-top: 1px solid #e2e8f0;
  }

  .toggle-text {
    color: #718096;
    margin: 0 0 0.5rem 0;
    font-size: 0.9rem;
  }

  .toggle-button {
    background: none;
    border: none;
    color: #667eea;
    font-weight: 600;
    cursor: pointer;
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    transition: all 0.2s ease;
  }

  .toggle-button:hover:not(:disabled) {
    background: #edf2f7;
    transform: translateY(-1px);
  }

  .toggle-button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  /* Responsive design */
  @media (max-width: 480px) {
    .auth-card {
      padding: 2rem 1.5rem;
      margin: 1rem;
    }
    
    .auth-header h1 {
      font-size: 1.75rem;
    }
    
    .auth-input {
      padding: 0.875rem 0.875rem 0.875rem 2.5rem;
    }
    
    .input-icon {
      left: 0.875rem;
    }
  }
</style>