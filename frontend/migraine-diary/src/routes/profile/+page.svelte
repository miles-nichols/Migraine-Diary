<!-- profile.svelte -->
<script>
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';
    
    let username = '';
    
    onMount(() => {
        if (typeof window !== 'undefined') {
            username = localStorage.getItem('username') || '';
        }
    });
    
    function goBack() {
        goto('/');
    }
</script>

<div class="profile-container">
    <h1>Your Profile</h1>
    <div class="profile-info">
        <p><strong>Username:</strong> {username}</p>
    </div>
    <button on:click={goBack} class="back-btn">Back to Home</button>
</div>

<style>
    .profile-container {
        max-width: 400px;
        margin: 2rem auto;
        padding: 2rem;
        background: white;
        border-radius: 1rem;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    
    .back-btn {
        background: #3b82f6;
        color: white;
        border: none;
        padding: 0.5rem 1rem;
        border-radius: 0.5rem;
        cursor: pointer;
    }
</style>