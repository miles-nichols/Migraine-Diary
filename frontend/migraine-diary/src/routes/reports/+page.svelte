<script lang="ts">
  import { onMount } from 'svelte';

  // TypeScript interfaces
  interface MedicineDailyLog {
    medicineName: string;
    dose: string;
    relief: number;
  }

  interface Episode {
    episodeId?: number;
    username: string;
    episodeDate: string;
    triggerIds: number[];
    menstrualPeriod: boolean;
    morningSeverity: number;
    afternoonSeverity: number;
    eveningSeverity: number;
    notes: string;
    medicineLogs: MedicineDailyLog[];
  }

  // Reactive state variables
  let episodes: Episode[] = [];
  let username: string | null = null;
  let selectedYear: number = new Date().getFullYear();
  let selectedMonth: number = new Date().getMonth() + 1;

  onMount(() => {
    if (typeof window !== 'undefined') {
      username = localStorage.getItem('username');
      console.log('Retrieved username:', username);
    }
  });

  async function fetchEpisodes() {
    console.log('fetchEpisodes called');
    if (!username) {
      console.warn("Username not found. Cannot fetch logs.");
      episodes = [];
      return;
    }

    const url = `/episodes/user/${username}/month/${selectedYear}/${selectedMonth}`;
    console.log('Fetching from URL:', url);
    
    try {
      const response = await fetch(url);
      console.log('Response status:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Fetched data:', data);
      episodes = data as Episode[];
      console.log('Episodes count:', episodes.length);
      
    } catch (error) {
      console.error("Failed to fetch episodes:", error);
      episodes = [];
    }
  }

  // Fixed reactive statement
  $: if (username) {
    console.log('Fetching episodes for:', {username, selectedYear, selectedMonth});
    fetchEpisodes();
  }

  // Fixed episodesByDay mapping with proper date parsing
  $: episodesByDay = episodes.reduce((acc, episode) => {
    // Parse the date string directly to avoid timezone issues
    const [year, month, day] = episode.episodeDate.split('-').map(Number);
    console.log('Mapping:', episode.episodeDate, '→ Day:', day);
    acc[day] = episode;
    return acc;
  }, {} as { [key: number]: Episode });

  // Debug episodesByDay
  $: console.log('episodesByDay object:', episodesByDay);

  // Reactive statement to calculate the number of days in the selected month
  $: daysInMonth = new Date(selectedYear, selectedMonth, 0).getDate();
  $: days = Array.from({ length: daysInMonth }, (_, i) => i + 1);

  const years = Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - 2 + i);
  const months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
</script>

<main class="container">
  <h1>Monthly Migraine Report</h1>
  <p class="report-period">
    Report for {months[selectedMonth - 1]} {selectedYear}
  </p>

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
  </div>

  <div class="report-grid-container">
    <div class="grid-row grid-header-row">
      <div class="fixed-column">Day of Month</div>
      {#each days as day}
        <div class="day-cell">{day}</div>
      {/each}
    </div>

    <div class="grid-row">
      <div class="fixed-column">Morning Severity</div>
      {#each days as day}
        <div class="data-cell">
          {#if episodesByDay[day]}
            {episodesByDay[day].morningSeverity}
          {/if}
        </div>
      {/each}
    </div>

    <div class="grid-row">
      <div class="fixed-column">Afternoon Severity</div>
      {#each days as day}
        <div class="data-cell">
          {#if episodesByDay[day]}
            {episodesByDay[day].afternoonSeverity}
          {/if}
        </div>
      {/each}
    </div>

    <div class="grid-row">
      <div class="fixed-column">Evening Severity</div>
      {#each days as day}
        <div class="data-cell">
          {#if episodesByDay[day]}
            {episodesByDay[day].eveningSeverity}
          {/if}
        </div>
      {/each}
    </div>

    <div class="grid-row">
      <div class="fixed-column">Triggers</div>
      {#each days as day}
        <div class="data-cell trigger-cell">
          {#if episodesByDay[day]}
            {episodesByDay[day].triggerIds.join(', ')}
          {/if}
        </div>
      {/each}
    </div>

    <div class="grid-row">
      <div class="fixed-column">Menstrual Period</div>
      {#each days as day}
        <div class="data-cell period-cell">
          {#if episodesByDay[day] && episodesByDay[day].menstrualPeriod}
            X
          {/if}
        </div>
      {/each}
    </div>
  </div>

  <div class="medicines-section">
    <h2>Medicines Taken</h2>
    {#if episodes.length > 0}
      {#each episodes as episode}
        <div class="medicine-entry">
          <div class="medicine-header">
            <span class="medicine-date">On: {new Date(episode.episodeDate).toLocaleDateString()}</span>
          </div>
          {#each episode.medicineLogs as log}
            <div class="medicine-log">
              <span class="medicine-name">{log.medicineName}</span>
              <span class="medicine-dose">Dose: {log.dose}</span>
              <span class="medicine-relief">Relief: {log.relief}</span>
            </div>
          {/each}
        </div>
      {/each}
    {:else}
      <p class="no-data-message">No medicines recorded for this month.</p>
    {/if}
  </div>

</main>

<style>
  .container {
    max-width: 1200px;
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
    margin-bottom: 0.5rem;
  }
  
  .report-period {
    text-align: center;
    color: #666;
    margin-bottom: 2rem;
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
  }

  .report-grid-container {
    overflow-x: auto;
    background-color: #fff;
    border: 1px solid #e0e6ed;
    border-radius: 8px;
    padding: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }

  .grid-row {
    display: flex;
    border-bottom: 1px solid #eee;
    min-width: fit-content;
  }

  .grid-row:last-child {
    border-bottom: none;
  }
  
  .fixed-column, .day-cell, .data-cell {
    padding: 0.5rem 0.75rem;
    border-right: 1px solid #eee;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    min-width: 40px;
  }
  
  .fixed-column {
    background-color: #f8f8f8;
    font-weight: bold;
    color: #444;
    min-width: 150px;
    justify-content: flex-start;
    padding: 0.5rem 1rem;
    position: sticky;
    left: 0;
    z-index: 10;
  }
  
  .grid-header-row .fixed-column {
    background-color: #e2e8f0;
    min-width: 150px;
  }
  
  .day-cell {
    background-color: #eef3f7;
    font-weight: bold;
    color: #555;
  }
  
  .grid-header-row .day-cell {
    background-color: #e2e8f0;
  }

  .data-cell {
    color: #333;
    font-size: 0.9rem;
  }

  .trigger-cell {
    font-size: 0.8rem;
  }

  .period-cell {
    font-weight: bold;
    color: #e74c3c;
  }
  
  .medicines-section {
    margin-top: 2rem;
    background-color: #fff;
    border: 1px solid #e0e6ed;
    border-radius: 8px;
    padding: 1.5rem;
  }
  
  h2 {
    color: #2c3e50;
    margin-bottom: 1rem;
  }

  .medicine-entry {
    border-bottom: 1px solid #eee;
    padding-bottom: 0.75rem;
    margin-bottom: 0.75rem;
  }
  
  .medicine-entry:last-child {
    border-bottom: none;
  }

  .medicine-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    color: #3498db;
    margin-bottom: 0.5rem;
  }

  .medicine-log {
    display: flex;
    gap: 1rem;
    font-size: 0.9rem;
    color: #555;
  }
  
  .no-data-message {
    text-align: center;
    color: #95a5a6;
    font-style: italic;
  }
</style>