<!--Main-->
<script>
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';
    
    let loggedIn = false;
    let username = '';

    // Define a user object to hold user information
    let user = {
        username: '',
        email: '',
        password: ''
    };

    onMount(() => {
        // Check if user is logged in and get username from localStorage
        if (typeof window !== 'undefined') {
            loggedIn = localStorage.getItem('loggedIn') === 'true';
            username = localStorage.getItem('username') || '';
            
            // Update user object
            if (loggedIn && username) {
                user.username = username;
            }
        }
    });

    function goToLogPage() {
        goto('/log');
    }
    
    function goToLogin() {
        goto('/login');
    }
    
    function goToProfile() {
        goto('/profile');
    }
    
    function logout() {
        if (typeof window !== 'undefined') {
            localStorage.removeItem('loggedIn');
            localStorage.removeItem('username');
            localStorage.removeItem('token');
        }
        loggedIn = false;
        username = '';
        user.username = '';
        goto('/');
    }

</script>

<main>
    <!-- User info in upper right corner -->
    {#if loggedIn && username}
        <div class="user-info-corner">
            <span class="welcome-text">{username}!</span>
            <button on:click={goToProfile} class="profile-btn">Profile</button>
            <button on:click={logout} class="logout-btn">Logout</button>
        </div>
    {/if}
    
    <div class="container">
        <h1>Welcome to Your Migraine Diary!</h1>
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
        position: relative;
    }
    
    .user-info-corner {
        position: absolute;
        top: 1rem;
        right: 1rem;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        background: rgba(255, 255, 255, 0.95);
        padding: 0.75rem 1rem;
        border-radius: 1rem;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        backdrop-filter: blur(10px);
    }
    
    .welcome-text {
        color: #3b82f6;
        font-weight: 500;
        font-size: 0.95rem;
    }
    
    .profile-btn, .logout-btn {
        background: #f1f5f9;
        color: #3b82f6;
        border: 1px solid #e2e8f0;
        padding: 0.4rem 0.8rem;
        border-radius: 0.5rem;
        font-size: 0.85rem;
        cursor: pointer;
        transition: all 0.2s ease;
    }
    
    .profile-btn:hover {
        background: #3b82f6;
        color: white;
    }
    
    .logout-btn {
        background: #fee2e2;
        color: #ef4444;
        border-color: #fecaca;
    }
    
    .logout-btn:hover {
        background: #ef4444;
        color: white;
    }
    
    .container {
        background: #fff;
        padding: 2.5rem 2rem;
        border-radius: 1.5rem;
        box-shadow: 0 4px 24px rgba(0,0,0,0.08);
        text-align: center;
        max-width: 400px;
        width: 100%;
        margin-top: 3rem; /* Add space for the user info corner */
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

    /* Responsive design for mobile */
    @media (max-width: 768px) {
        .user-info-corner {
            top: 0.5rem;
            right: 0.5rem;
            flex-direction: column;
            gap: 0.5rem;
            padding: 0.5rem;
        }
        
        .welcome-text {
            font-size: 0.8rem;
        }
        
        .container {
            margin-top: 4rem;
            padding: 2rem 1.5rem;
        }
        
        h1 {
            font-size: 2em;
        }
        
        h2 {
            font-size: 1.2em;
        }
    }
</style>
