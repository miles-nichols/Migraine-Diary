<script>
  import { goto } from '$app/navigation';
  let isLogin = true;
  let username = '';
  let password = '';
  let confirmPassword = '';
  let error = '';
  let email = '';

  function switchPage() {
    isLogin = !isLogin;
    error = '';
    username = '';
    password = '';
    confirmPassword = '';
    email = '';
  }

  async function handleSubmit() {
    error = '';
    // Validate fields
    if (!username || !password) {
      error = 'Please fill in all fields.';
      return;
    }
    if (!isLogin && password !== confirmPassword) {
      error = 'Passwords do not match.';
      return;
    }
    if (!isLogin && !email) {
      error = 'Email is required for registration.';
      return;
    }

        //USE PORT 8081
		try {
      const url = isLogin ? 'http://localhost:8081/user/login' : 'http://localhost:8081/user/register';
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(isLogin ? {username, password} : {username, password, email}),
      });

      // Handle both JSON and text responses
      let result;
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        result = await response.json();
      } else {
        result = { message: await response.text() };
      }

      if (response.ok) {
        goto('/'); // Redirect to home on success
      } else {
        error = result.message || 'Request failed';
      }
    } catch (err) {
      console.error('Auth failed:', err);
      error = 'An unexpected error occurred. Please try again.';
    } 
  }
</script>

<main>
	<div class="container">
		<h1>{isLogin ? 'Login' : 'Sign Up'}</h1>
		<form on:submit|preventDefault={handleSubmit}>
			<input type="text" placeholder="Username" bind:value={username} required />
			<input type="password" placeholder="Password" bind:value={password} required />
			{#if !isLogin}
				<input type="password" placeholder="Confirm Password" bind:value={confirmPassword} required />
				<input type="email" placeholder="Email" bind:value={email} required />
			{/if}
			{#if error}
				<div class="error">{error}</div>
			{/if}
			<button type="submit" class="auth-btn">{isLogin ? 'Login' : 'Sign Up'}</button>
		</form>
		<button class="toggle-btn" on:click={switchPage}>
			{isLogin ? "Don't have an account? Sign Up" : "Already have an account? Login"}
		</button>
	</div>
</main>

<style>
	main {
		display: flex;
		justify-content: center;
		align-items: center;
		min-height: 80vh;
		background: linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%);
	}
	.container {
		background: #fff;
		padding: 2.5rem 2rem;
		border-radius: 1.5rem;
		box-shadow: 0 4px 24px rgba(0,0,0,0.08);
		text-align: center;
		max-width: 400px;
		width: 100%;
	}
	h1 {
		color: #3b82f6;
		font-size: 2em;
		margin-bottom: 1em;
	}
	form {
		display: flex;
		flex-direction: column;
		gap: 1em;
		margin-bottom: 1em;
	}
	input {
		padding: 0.75em;
		border-radius: 0.5em;
		border: 1px solid #e0e7ff;
		font-size: 1em;
	}
	.auth-btn {
		background: linear-gradient(90deg, #6366f1 0%, #3b82f6 100%);
		color: #fff;
		border: none;
		padding: 0.75em 2em;
		font-size: 1.1em;
		border-radius: 2em;
		cursor: pointer;
		box-shadow: 0 2px 8px rgba(99,102,241,0.15);
		transition: background 0.2s, transform 0.2s;
	}
	.auth-btn:hover {
		background: linear-gradient(90deg, #3b82f6 0%, #6366f1 100%);
		transform: translateY(-2px) scale(1.04);
	}
	.toggle-btn {
		background: none;
		border: none;
		color: #6366f1;
		font-size: 1em;
		cursor: pointer;
		margin-top: 0.5em;
		text-decoration: underline;
	}
	.error {
		color: #ef4444;
		font-size: 0.95em;
		margin-bottom: 0.5em;
	}
</style>
