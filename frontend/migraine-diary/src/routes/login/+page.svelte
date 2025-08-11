<script>
	import { goto } from '$app/navigation';
	let isLogin = true;
	let email = '';
	let password = '';
	let confirmPassword = '';
	let error = '';

	function toggleMode() {
		isLogin = !isLogin;
		error = '';
		email = '';
		password = '';
		confirmPassword = '';
	}

	async function handleSubmit() {
		error = '';
		if (!email || !password) {
			error = 'Please fill in all fields.';
			return;
		}
		if (!isLogin && password !== confirmPassword) {
			error = 'Passwords do not match.';
			return;
		}
		if (isLogin) {
			try {
				const res = await fetch('http://localhost:8080/login', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({ username: email, password })
				});
				if (res.ok) {
					localStorage.setItem('loggedIn', 'true');
					goto('/');
				} else {
					const msg = await res.text();
					error = msg || 'Login failed.';
				}
			} catch (e) {
				error = 'Network error.';
			}
		} else {
			// Simulate signup (replace with real signup endpoint if available)
			localStorage.setItem('loggedIn', 'true');
			goto('/');
		}
	}
</script>
<main>
	<div class="container">
		<h1>{isLogin ? 'Login' : 'Sign Up'}</h1>
		<form on:submit|preventDefault={handleSubmit}>
			<input type="email" placeholder="Email" bind:value={email} required />
			<input type="password" placeholder="Password" bind:value={password} required />
			{#if !isLogin}
				<input type="password" placeholder="Confirm Password" bind:value={confirmPassword} required />
			{/if}
			{#if error}
				<div class="error">{error}</div>
			{/if}
			<button type="submit" class="auth-btn">{isLogin ? 'Login' : 'Sign Up'}</button>
		</form>
		<button class="toggle-btn" on:click={toggleMode}>
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
