<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';

  // --- 1. Type Definitions & Initial State ---
  interface Medicine {
    name: string;
    dose: string;
    relief: number | null;
    time: string;
    notes: string;
  }

  interface FormData {
    date: string;
    morningSeverity: number | null;
    afternoonSeverity: number | null;
    eveningSeverity: number | null;
    triggers: number[];
    menstrualPeriod: boolean;
    notes: string;
    medicines: Medicine[];
  }

  const localStorageKey = 'migraineFormData';

  const defaultFormData: FormData = {
    date: new Date().toISOString().split('T')[0],
    morningSeverity: null,
    afternoonSeverity: null,
    eveningSeverity: null,
    triggers: [],
    menstrualPeriod: false,
    notes: '',
    medicines: [{ name: '', dose: '', relief: null, time: '', notes: '' }]
  };

  // Helper function to safely get and parse data from localStorage
  function getStoredFormData(): FormData | null {
    if (typeof window !== 'undefined') {
      const savedData = localStorage.getItem(localStorageKey);
      if (savedData) {
        try {
          return JSON.parse(savedData);
        } catch (e) {
          console.error('Failed to parse saved form data from localStorage:', e);
          return null;
        }
      }
    }
    return null;
  }

  let formData: FormData = getStoredFormData() || defaultFormData;

  // Svelte reactive statement to automatically save to localStorage
  $: if (typeof window !== 'undefined') {
    localStorage.setItem(localStorageKey, JSON.stringify(formData));
  }
  
  // --- 2. Form Logic & Data ---
  let isLoading = false;
  let error: unknown | null;
  let success = false;
  let title = "Log Your Migraine Day";

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

  function addMedicine() {
    formData.medicines = [...formData.medicines, { name: '', dose: '', relief: null, time: '', notes: '' }];
  }

  function removeMedicine(index: number) {
    if (formData.medicines.length > 1) {
      formData.medicines.splice(index, 1);
      formData.medicines = formData.medicines; // Trick Svelte into updating reactivity
    }
  }

  async function submitForm() {
    isLoading = true;
    error = null;
    success = false;

    const username = localStorage.getItem('username');
    if (!username) {
      error = "Username not found. Please log in.";
      isLoading = false;
      return;
    }

    try {
      const payload = {
        username: username,
        episodeDate: formData.date,
        morningSeverity: formData.morningSeverity,
        afternoonSeverity: formData.afternoonSeverity,
        eveningSeverity: formData.eveningSeverity,
        triggerIds: formData.triggers, 
        menstrualPeriod: formData.menstrualPeriod,
        notes: formData.notes,
       medicineLogs: formData.medicines
        .filter(med => med.name && med.name.trim() !== '') // <-- Filter out empty logs
        .map(med => ({
        medicineName: med.name,
        dose: med.dose,
        relief: med.relief,
        timeTaken: med.time,
        notes: med.notes
    }))
      };

      const url = '/episodes/log';
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errorData = await response.json();
        alert(errorData.error); 
        return;
      }

      success = true;
      if (typeof window !== 'undefined') {
        localStorage.removeItem(localStorageKey);
      }
      
      formData = { ...defaultFormData }; // Reset form and trigger reactivity
      
    } catch (err: unknown) {
      if (err instanceof Error) {
        error = err.message;
      } else if (typeof err === 'string') {
        error = err;
      } else {
        error = 'An unknown error occurred';
      }
      console.error('Submission error:', err);
    } finally {
      isLoading = false;
    }
  }

  // Handle success state and allow logging another day
  function handleReset() {
    success = false;
    formData = getStoredFormData() || defaultFormData;
  }
</script>

<main>
  <div class="container">
    <div class="header-section">
      <h1>{title}</h1>
      <button class="back-home-btn" on:click={() => goto('/')}>
        ← Back to Home
      </button>
    </div>
    
    {#if success}
      <div class="success-message">
        <p>Your migraine day has been successfully logged!</p>
        <button on:click={handleReset}>Log Another Day</button>
      </div>
    {:else}
      <form on:submit|preventDefault={submitForm}>
        
        <div class="form-group">
          <label for="date">Date</label>
          <input type="date" id="date" bind:value={formData.date} required>
        </div>
        
        <div class="form-section">
          <h3>Migraine Severity</h3>
          <div class="severity-grid">
            <div>
              <label>Morning</label>
              <select bind:value={formData.morningSeverity}>
                <option value={null}>No migraine</option>
                <option value={1}>1 - Mild</option>
                <option value={2}>2 - Moderate</option>
                <option value={3}>3 - Severe</option>
              </select>
            </div>
            <div>
              <label>Afternoon</label>
              <select bind:value={formData.afternoonSeverity}>
                <option value={null}>No migraine</option>
                <option value={1}>1 - Mild</option>
                <option value={2}>2 - Moderate</option>
                <option value={3}>3 - Severe</option>
              </select>
            </div>
            <div>
              <label>Evening/Night</label>
              <select bind:value={formData.eveningSeverity}>
                <option value={null}>No migraine</option>
                <option value={1}>1 - Mild</option>
                <option value={2}>2 - Moderate</option>
                <option value={3}>3 - Severe</option>
              </select>
            </div>
          </div>
        </div>
        
        <div class="form-section">
          <h3>Potential Triggers</h3>
          <div class="trigger-grid">
            {#each triggerOptions as trigger}
              <label class="trigger-label">
                <input 
                  type="checkbox" 
                  bind:group={formData.triggers} 
                  value={trigger.id}
                >
                <span>{trigger.name}</span>
              </label>
            {/each}
          </div>
        </div>
        
        <div class="form-section">
          <label class="checkbox-label">
            <input 
              type="checkbox" 
              bind:checked={formData.menstrualPeriod}
            >
            Menstrual Period
          </label>
        </div>
        
        <div class="form-section">
          <h3>Medicines Taken</h3>
          {#each formData.medicines as medicine, index}
            <div class="medicine-entry">
              <div class="medicine-row">
                <input 
                  type="text" 
                  placeholder="Medicine name" 
                  bind:value={medicine.name}
                >
                <input 
                  type="text" 
                  placeholder="Dose (e.g., 200mg)" 
                  bind:value={medicine.dose}
                >
                <input 
                  type="time" 
                  bind:value={medicine.time}
                >
                {#if formData.medicines.length > 1}
                  <button 
                    type="button" 
                    on:click={() => removeMedicine(index)}
                    class="remove-btn"
                  >
                    ×
                  </button>
                {/if}
              </div>
              <div class="relief-row">
                <label>Relief Level:</label>
                <select bind:value={medicine.relief}>
                  <option value={null}>Not rated</option>
                  <option value={0}>0 - No relief</option>
                  <option value={1}>1 - Slight relief</option>
                  <option value={2}>2 - Moderate relief</option>
                  <option value={3}>3 - Complete relief</option>
                </select>
              </div>
            </div>
          {/each}
          <button 
            type="button" 
            on:click={addMedicine}
            class="add-btn"
          >
            + Add Another Medicine
          </button>
        </div>
        
        <div class="form-section">
          <h3>Additional Notes</h3>
          <textarea 
            id="notes" 
            bind:value={formData.notes}
            placeholder="Any other details about your migraine day..."
          ></textarea>
        </div>
        
        <button type="submit" disabled={isLoading} class="submit-btn">
          {#if isLoading}
            Saving...
          {:else}
            Save Migraine Day
          {/if}
        </button>
        
        {#if error}
          <div class="error-message">{error}</div>
        {/if}
      </form>
    {/if}
  </div>
</main>

<style>
  main {
    display: flex;
    justify-content: center;
    align-items: flex-start;
    min-height: 100vh;
    padding: 2rem 0;
    background: linear-gradient(135deg, #f0f4f8 0%, #e8ecf4 100%);
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  }
  
  .container {
    background: #fff;
    padding: 2.5rem;
    border-radius: 1.5rem;
    box-shadow: 0 10px 30px rgba(0,0,0,0.05);
    width: 100%;
    max-width: 700px;
    border: 1px solid #e2e8f0;
  }
  
  h1 {
    color: #435b89;
    font-size: 2.2em;
    font-weight: 700;
    margin-bottom: 0.5em;
    text-align: center;
  }
  
  h3 {
    color: #4a638a;
    font-size: 1.25em;
    font-weight: 600;
    margin-top: 1.5rem;
    margin-bottom: 0.75rem;
  }
  
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  .form-section {
    margin-bottom: 2rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid #e2e8f0;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    color: #64748b;
    font-weight: 500;
  }

  .checkbox-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  input[type="checkbox"] {
    width: auto;
    accent-color: #60a5fa;
  }
  
  input[type="text"],
  input[type="date"],
  input[type="time"],
  select,
  textarea {
    width: 100%;
    padding: 0.85rem;
    border: 1px solid #cbd5e1;
    border-radius: 0.75rem;
    font-size: 1rem;
    color: #1e293b;
    transition: border-color 0.2s, box-shadow 0.2s;
  }
  
  input:focus, select:focus, textarea:focus {
    outline: none;
    border-color: #60a5fa;
    box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
  }
  
  textarea {
    min-height: 120px;
    resize: vertical;
  }

  /* Severity & Trigger Grids */
  .severity-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1.5rem;
  }
  
  .trigger-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
  }
  
  .trigger-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem;
    background: #f8fafc;
    border-radius: 0.5rem;
    cursor: pointer;
    font-weight: 400;
    transition: background 0.2s, box-shadow 0.2s;
  }

  .trigger-label:hover {
    background: #eff6ff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  }

  /* Medicine Section */
  .medicine-entry {
    margin-bottom: 1.5rem;
    padding: 1.5rem;
    background: #f8fafc;
    border-radius: 1rem;
    border: 1px solid #e2e8f0;
  }
  
  .medicine-row {
    display: flex;
    gap: 1rem;
    margin-bottom: 1rem;
    align-items: center;
  }
  
  .medicine-row input {
    flex: 1;
  }
  
  .relief-row {
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  .relief-row select {
    width: auto;
  }

  /* Buttons */
  .add-btn, .submit-btn, .back-home-btn {
    background: #60a5fa;
    color: white;
    border: none;
    padding: 1rem;
    border-radius: 0.75rem;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s, transform 0.2s;
    width: 100%;
  }

  .add-btn {
    background: #eff6ff;
    color: #3b82f6;
    border: 1px solid #dbeafe;
    padding: 0.75rem 1.5rem;
    margin-top: 0.5rem;
  }

  .add-btn:hover {
    background: #dbeafe;
    transform: translateY(-1px);
  }

  .submit-btn {
    margin-top: 2rem;
    background: linear-gradient(90deg, #4c77c6 0%, #60a5fa 100%);
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  }
  
  .submit-btn:hover {
    box-shadow: 0 6px 20px rgba(0,0,0,0.15);
    transform: translateY(-2px);
  }
  
  .submit-btn:disabled {
    background: #d1d5db;
    color: #9ca3af;
    cursor: not-allowed;
    box-shadow: none;
    transform: none;
  }
  
  .remove-btn {
    background: none;
    border: none;
    color: #ef4444;
    font-size: 2rem;
    font-weight: 300;
    cursor: pointer;
    padding: 0 0.5rem;
    line-height: 1;
    opacity: 0.7;
    transition: opacity 0.2s;
  }
  
  .remove-btn:hover {
    opacity: 1;
  }
  
  /* Messages */
  .error-message {
    color: #ef4444;
    background: #fecaca;
    padding: 1rem;
    border-radius: 0.75rem;
    margin-top: 1.5rem;
    font-weight: 500;
    text-align: center;
  }
  
  .success-message {
    text-align: center;
    padding: 3rem 1rem;
  }
  
  .success-message p {
    color: #10b981;
    font-size: 1.5rem;
    font-weight: 600;
    margin-bottom: 2rem;
  }
  
  .success-message button {
    background: #22c55e;
    color: white;
    border: none;
    padding: 0.75rem 2rem;
    border-radius: 0.75rem;
    cursor: pointer;
    font-weight: 600;
    transition: background 0.2s;
  }
  
  .success-message button:hover {
    background: #16a34a;
  }
  
  /* Media Queries for Responsiveness */
  @media (max-width: 768px) {
    .container {
      padding: 1.5rem;
      border-radius: 1rem;
    }

    h1 {
      font-size: 1.8em;
    }

    .severity-grid, .trigger-grid {
      grid-template-columns: 1fr;
    }
    
    .medicine-row {
      flex-direction: column;
      gap: 0.75rem;
    }
    .medicine-entry {
      padding: 1rem;
    }
  }
</style>