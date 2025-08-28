<script lang="ts">
  import { onMount } from 'svelte';

  // Define the TypeScript interface for an episode entry.
  interface Episode {
    id?: number; 
    timestamp: string;
    // Add other fields from your Episode entity here, e.g.,
    // episodeName: string;
    // ...
  }

  // Reactive state variables
  let logs: Episode[] = [];
  let username: string | null = null;
  let selectedYear: number = new Date().getFullYear();
  let selectedMonth: number = new Date().getMonth() + 1;
  let selectedDay: number | null = null;

  // We need to get the username when the component first mounts.
  onMount(() => {
    if (typeof window !== 'undefined') {
      username = localStorage.getItem('username');
    }
  });

  async function fetchEpisodes() {
    if (!username) {
      console.warn("Username not found. Cannot fetch logs.");
      logs = [];
      return;
    }

    const url = `/episodes/user/${username}/month/${selectedYear}/${selectedMonth}`;

    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      logs = await response.json() as Episode[];
    } catch (error) {
      console.error("Failed to fetch episodes:", error);
      logs = [];
    }
  }

  // Reactive statement to re-run fetchEpisodes
  $: {
    if (username !== null) {
      fetchEpisodes();
    }
  }

  // Reactive statement to filter the logs by day on the frontend.
  $: displayedLogs = selectedDay 
    ? logs.filter(log => new Date(log.timestamp).getDate() === selectedDay)
    : logs;

  // Reactive statement to calculate the number of days in the selected month.
  $: daysInMonth = new Date(selectedYear, selectedMonth, 0).getDate();
  $: days = Array.from({ length: daysInMonth }, (_, i) => i + 1);

  const years = Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - 2 + i);

  const months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
</script>

<main class="container">
  <h1>Episode History</h1>

  <div class="controls">
    <select bind:value={selectedYear}>
      {#each years as year}
        <option value={year}>{year}</option>
      {/each}
    </select>

    <select bind:value={selectedMonth}>
      {#each months as month, i}
        <option value={i + 1}>{month}</option>
      {/each}
    </select>

    <select bind:value={selectedDay}>
      <option value={null}>All Days</option>
      {#each days as day}
        <option value={day}>{day}</option>
      {/each}
    </select>
  </div>

  <div class="logs-display">
    {#if displayedLogs.length > 0}
      {#each displayedLogs as log}
        <div class="log-entry">
          <span class="log-timestamp">{new Date(log.timestamp).toLocaleString()}</span>
          <p class="log-message">Details for this episode...</p>
        </div>
      {:else}
        <p class="no-logs-message">No episodes found for the selected date.</p>
      {/each}
    {/if}
  </div>
</main>

<style>
  .container {
    max-width: 900px;
    margin: 2rem auto;
    padding: 1rem;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
      Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
    background-color: #f0f2f5;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  h1 {
    text-align: center;
    color: #2c3e50;
    margin-bottom: 1.5rem;
  }

  .controls {
    display: flex;
    justify-content: center;
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  select {
    padding: 0.6rem 1rem;
    font-size: 1rem;
    border: 1px solid #bdc3c7;
    border-radius: 6px;
    background-color: #fff;
    cursor: pointer;
    transition: border-color 0.2s;
  }

  select:hover {
    border-color: #2980b9;
  }

  .logs-display {
    border: 1px solid #e0e6ed;
    border-radius: 8px;
    background-color: #fff;
    padding: 1.5rem;
  }

  .log-entry {
    background-color: #f9f9f9;
    border-left: 4px solid #3498db;
    border-radius: 4px;
    padding: 1rem;
    margin-bottom: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }

  .log-entry:last-child {
    margin-bottom: 0;
  }
  
  .log-timestamp {
    font-weight: 600;
    color: #7f8c8d;
    font-size: 0.9rem;
  }

  .log-level {
    font-family: monospace;
    font-size: 0.8rem;
    padding: 0.2rem 0.6rem;
    border-radius: 4px;
    background-color: #ecf0f1;
    color: #34495e;
    text-transform: uppercase;
    margin-left: 0.5rem;
  }

  .log-message {
    margin-top: 0.75rem;
    color: #34495e;
    line-height: 1.5;
    white-space: pre-wrap;
  }

  .no-logs-message {
    text-align: center;
    color: #95a5a6;
    font-style: italic;
    padding: 2rem 0;
  }
</style>