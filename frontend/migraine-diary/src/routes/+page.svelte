<script>
    import { goto } from '$app/navigation';
    let title = "Please Log Your Day";
    let loggedIn = false;

    if (typeof window !== 'undefined') {
        loggedIn = localStorage.getItem('loggedIn') === 'true';
    }

    function goToLogPage() {
        goto('/log');
    }
    function goToLogin() {
        goto('/login');
    }
</script>

<main>
    <div class="container">
        <h1>Welcome to Your Migraine Diary!</h1>
        <h2>{title}</h2>
        {#if loggedIn}
            <button class="log-btn" on:click={goToLogPage}>Log Your Day</button>
        {:else}
            <button class="log-btn" on:click={goToLogin}>Log In / Sign Up</button>
            <div class="login-prompt">Please log in to access your diary.</div>
        {/if}
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
        font-size: 2.5em;
        margin-bottom: 0.5em;
    }
    h2 {
        color: #6366f1;
        font-size: 1.5em;
        margin-bottom: 2em;
    }
    .log-btn {
        background: linear-gradient(90deg, #6366f1 0%, #3b82f6 100%);
        color: #fff;
        border: none;
        padding: 0.75em 2em;
        font-size: 1.1em;
        border-radius: 2em;
        cursor: pointer;
        box-shadow: 0 2px 8px rgba(99,102,241,0.15);
        transition: background 0.2s, transform 0.2s;
        margin-bottom: 1em;
    }
    .log-btn:hover {
        background: linear-gradient(90deg, #3b82f6 0%, #6366f1 100%);
        transform: translateY(-2px) scale(1.04);
    }
    .login-prompt {
        color: #ef4444;
        font-size: 1em;
        margin-top: 0.5em;
    }
</style>