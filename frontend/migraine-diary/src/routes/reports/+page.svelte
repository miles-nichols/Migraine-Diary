<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';

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

  const triggerOptions = [
    { id: 1, name: 'Menses (period)'},
    { id: 2, name: 'Ovulation' },
    { id: 3, name: 'Hormone replacement therapy' },
    { id: 4, name: 'Oral contraceptives' },
    { id: 5, name: 'Alcohol' },
    { id: 6, name: 'Chocolate' },
    { id: 7, name: 'Aged cheeses' },
    { id: 8, name: 'Monosodium glutamate (MSG)' },
    { id: 9, name: 'Artificial sweeteners' },
    { id: 10, name: 'Caffeine' },
    { id: 11, name: 'Nuts' },
    { id: 12, name: 'Nitrates/Nitrites' },
    { id: 13, name: 'Citrus fruits' },
    { id: 14, name: 'Other dietary' },
    { id: 15, name: 'Weather' },
    { id: 16, name: 'Seasons' },
    { id: 17, name: 'Travel' },
    { id: 18, name: 'Altitude' },
    { id: 19, name: 'Schedule change' },
    { id: 20, name: 'Sleeping patterns' },
    { id: 21, name: 'Diet change' },
    { id: 22, name: 'Skipping meals' },
    { id: 23, name: 'Strong light' },
    { id: 24, name: 'Flickering light' },
    { id: 25, name: 'Odors' },
    { id: 26, name: 'Let-down periods' },
    { id: 27, name: 'Times of intense activity' },
    { id: 28, name: 'Loss' },
    { id: 29, name: 'Relationship difficulties' },
    { id: 30, name: 'Job stress' },
    { id: 31, name: 'Crisis' },
    { id: 32, name: 'Other' }
  ];

  // Function to convert trigger IDs to names
  function getTriggerNames(triggerIds: number[]): string {
    if (!triggerIds || !triggerIds.length) return '';
    
    return triggerIds
      .map(id => {
        const trigger = triggerOptions.find(t => t.id === id);
        return trigger ? trigger.name : `Unknown (${id})`;
      })
      .join(', ');
  }

  // Navigation function
  function goToMainScreen() {
    goto('/');
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
    console.log('Fetched raw data:', data);
    
    // Ensure all properties are properly set with proper type annotation
    episodes = data.map((episode: any) => ({
      ...episode,
      medicineLogs: episode.medicineLogs || [],
      triggerIds: episode.triggerIds || [],
      notes: episode.notes || '',
      morningSeverity: episode.morningSeverity || 0,
      afternoonSeverity: episode.afternoonSeverity || 0,
      eveningSeverity: episode.eveningSeverity || 0,
      menstrualPeriod: episode.menstrualPeriod || false
    })) as Episode[];
    
    console.log('Processed episodes:', episodes);
    
  } catch (error) {
    console.error("Failed to fetch episodes:", error);
    episodes = [];
  }
}

  // Reactive statement
  $: if (username) {
    console.log('Fetching episodes for:', {username, selectedYear, selectedMonth});
    fetchEpisodes();
  }

  // Debug logging
  $: console.log('All episodes:', episodes);

  // Fixed episodesByDay mapping
  $: episodesByDay = episodes.reduce((acc, episode) => {
    const [year, month, day] = episode.episodeDate.split('-').map(Number);
    console.log('Mapping:', episode.episodeDate, '→ Day:', day);
    acc[day] = episode;
    return acc;
  }, {} as { [key: number]: Episode });

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
  <!-- Navigation header with back button -->
  <div class="navigation-header">
    <button on:click={goToMainScreen} class="back-button">
      ← Back to Main Screen
    </button>
    <h1>Monthly Migraine Report</h1>
  </div>
  
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
            {getTriggerNames(episodesByDay[day].triggerIds)}
          {/if}
        </div>
      {/each}
    </div>

    <div class="grid-row">
      <div class="fixed-column">Menstrual Period</div>
      {#each days as day}
        <div class="data-cell period-cell">
          {#if episodesByDay[day] && episodesByDay[day].menstrualPeriod}
            Yes
          {/if}
          {#if episodesByDay[day] && !episodesByDay[day].menstrualPeriod}
            No
          {/if}
        </div>
      {/each}
    </div>
  </div>

  <div class="medicines-section">
    <h2>Medicines and Episode Details</h2>
    {#if episodes.length > 0}
      {#each episodes as episode}
        <div class="medicine-entry">
          <div class="medicine-header">
            <span class="medicine-date">📅 {new Date(episode.episodeDate).toLocaleDateString()}</span>
          </div>
          
       
          {#if episode.notes && episode.notes.trim()}
            <div class="episode-notes">
              <strong>📝 Notes:</strong> {episode.notes}
            </div>
          {/if}
          
          {#if episode.medicineLogs && episode.medicineLogs.length > 0}
            <div class="medicine-logs-container">
              <h3>💊 Medicines Taken:</h3>
              {#each episode.medicineLogs as log}
                <div class="medicine-log">
                  <div class="medicine-info">
                    <span class="medicine-name"><strong>{log.medicineName}</strong></span>
                    <span class="medicine-dose">Dose: {log.dose}</span>
                    <span class="medicine-relief">Relief: {log.relief}/10</span>
                  </div>
                </div>
              {/each}
            </div>
          {:else}
            <p class="no-medicine-message">No medicines recorded for this day.</p>
          {/if}
          
    
          <div class="severity-summary">
            <h3>📊 Pain Severity:</h3>
            <div class="severity-levels">
              <span>Morning: {episode.morningSeverity}/10</span>
              <span>Afternoon: {episode.afternoonSeverity}/10</span>
              <span>Evening: {episode.eveningSeverity}/10</span>
            </div>
          </div>
          
   
          {#if episode.triggerIds && episode.triggerIds.length > 0}
            <div class="triggers-summary">
              <h3>⚡ Triggers:</h3>
              <div class="trigger-list">
                {getTriggerNames(episode.triggerIds)}
              </div>
            </div>
          {/if}
          
         
          <div class="menstrual-summary">
            <h3>🔴 Menstrual Period:</h3>
            <span>{episode.menstrualPeriod ? 'Yes' : 'No'}</span>
          </div>
        </div>
      {/each}
    {:else}
      <p class="no-data-message">No episode data recorded for this month.</p>
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

  /* Navigation header styles */
  .navigation-header {
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    margin-bottom: 1rem;
  }

  .back-button {
    position: absolute;
    left: 0;
    padding: 0.5rem 1rem;
    background-color: #3498db;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: background-color 0.2s ease;
  }

  .back-button:hover {
    background-color: #2980b9;
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

  .medicines-section h2 {
    color: #2c3e50;
    margin-bottom: 1.5rem;
    border-bottom: 2px solid #3498db;
    padding-bottom: 0.5rem;
  }

  .medicine-entry {
    border: 1px solid #e0e6ed;
    border-radius: 8px;
    padding: 1.5rem;
    margin-bottom: 1.5rem;
    background-color: #f8f9fa;
  }

  .medicine-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
    flex-wrap: wrap;
    gap: 1rem;
  }

  .medicine-date {
    font-weight: bold;
    color: #2c3e50;
    font-size: 1.1rem;
  }

  .episode-notes {
    background-color: #fff3cd;
    border-left: 4px solid #ffc107;
    padding: 0.75rem;
    margin: 0.5rem 0;
    border-radius: 4px;
    color: #856404;
    width: 100%;
  }

  .medicine-logs-container {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    margin: 1rem 0;
  }

  .medicine-logs-container h3 {
    color: #2c3e50;
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
  }

  .medicine-log {
    background-color: white;
    padding: 1rem;
    border-radius: 6px;
    border-left: 4px solid #3498db;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }

  .medicine-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
  }

  .medicine-name {
    color: #2c3e50;
    font-weight: 600;
    min-width: 120px;
  }

  .medicine-dose {
    color: #7f8c8d;
    background-color: #ecf0f1;
    padding: 0.3rem 0.6rem;
    border-radius: 4px;
    font-weight: 500;
  }

  .medicine-relief {
    color: #27ae60;
    font-weight: 600;
    background-color: #e8f5e8;
    padding: 0.3rem 0.6rem;
    border-radius: 4px;
  }

  .no-medicine-message {
    color: #95a5a6;
    font-style: italic;
    text-align: center;
    padding: 1rem;
    background-color: #f8f9fa;
    border-radius: 4px;
    margin: 1rem 0;
  }

  .severity-summary,
  .triggers-summary,
  .menstrual-summary {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px dashed #bdc3c7;
  }

  .severity-summary h3,
  .triggers-summary h3,
  .menstrual-summary h3 {
    color: #2c3e50;
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
  }

  .severity-levels {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
  }

  .severity-levels span {
    background-color: #ecf0f1;
    padding: 0.4rem 0.8rem;
    border-radius: 4px;
    font-size: 0.9rem;
    color: #7f8c8d;
    font-weight: 500;
  }

  .trigger-list {
    background-color: #e8f4f8;
    padding: 0.75rem;
    border-radius: 4px;
    color: #31708f;
    font-size: 0.9rem;
  }

  .menstrual-summary span {
    background-color: #f8d7da;
    padding: 0.4rem 0.8rem;
    border-radius: 4px;
    color: #721c24;
    font-weight: bold;
  }

  .no-data-message {
    text-align: center;
    color: #95a5a6;
    font-style: italic;
    padding: 2rem;
    background-color: #f8f9fa;
    border-radius: 8px;
  }

  /* Responsive design for smaller screens */
  @media (max-width: 768px) {
    .medicine-info {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }
    
    .medicine-header {
      flex-direction: column;
      align-items: flex-start;
    }
    
    .severity-levels {
      flex-direction: column;
      gap: 0.5rem;
    }
    
    .controls {
      flex-direction: column;
      align-items: center;
    }
  }
</style>