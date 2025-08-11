<script lang="ts">
  import { onMount } from 'svelte';
  
  // Define a key for your localStorage item
  const localStorageKey = 'migraineFormData';

  // Define interfaces for better type safety
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

  // Your default form state
  const defaultFormData: FormData = {
    date: new Date().toISOString().split('T')[0],
    morningSeverity: null,
    afternoonSeverity: null,
    eveningSeverity: null,
    triggers: [],
    menstrualPeriod: false,
    notes: '',
    medicines: [{
      name: '',
      dose: '',
      relief: null,
      time: '',
      notes: ''
    }]
  };

  // Helper function to safely get and parse data from localStorage
  function getStoredFormData(): FormData | null {
    // This check is crucial for server-side rendering (SSR) environments like SvelteKit
    // where `localStorage` is not available.
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

  // Use the helper function to initialize formData
  // It will either load from localStorage or use the default state
  let formData: FormData = getStoredFormData() || defaultFormData;
  
  // Svelte's reactive statement. It runs whenever 'formData' changes.
  // This will automatically save the form's state to localStorage as the user types.
  $: if (typeof window !== 'undefined') {
    localStorage.setItem(localStorageKey, JSON.stringify(formData));
  }

  // ... (the rest of your original code remains the same)
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

  let isLoading = false;
  let error: unknown | null;
  let success = false;

  function addMedicine() {
    formData.medicines.push({
      name: '',
      dose: '',
      relief: null,
      time: '',
      notes: ''
    });
  }

  function removeMedicine(index: number) {
    if (formData.medicines.length > 1) {
      formData.medicines.splice(index, 1);
    }
  }

  async function submitForm() {
    isLoading = true;
    error = null;
    success = false;

    try {
      const migraineResponse = await fetch('/api/migraine-logs', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          date: formData.date,
          morningSeverity: formData.morningSeverity,
          afternoonSeverity: formData.afternoonSeverity,
          eveningSeverity: formData.eveningSeverity,
          triggerIds: formData.triggers,
          menstrualPeriod: formData.menstrualPeriod,
          notes: formData.notes
        })
      });

      const migraineResult = await migraineResponse.json();

      if (!migraineResponse.ok) {
        throw new Error(migraineResult.message || 'Failed to save migraine log');
      }

      const medicinePromises = formData.medicines.map(medicine => {
        if (medicine.name && medicine.dose) {
          return fetch('/api/medicine-logs', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              episodeId: migraineResult.episode_id,
              date: formData.date,
              medicineName: medicine.name,
              dose: medicine.dose,
              relief: medicine.relief,
              timeTaken: medicine.time,
              notes: medicine.notes || ''
            })
          });
        }
        return Promise.resolve();
      });

      await Promise.all(medicinePromises);

      success = true;
      // After a successful submission, clear the saved data from localStorage
      if (typeof window !== 'undefined') {
        localStorage.removeItem(localStorageKey);
      }
      
      // Reset form to its default state
      formData = defaultFormData;
      
    } catch (err: unknown) {
      error = (err as Error).message;
      console.error('Submission error:', err);
    } finally {
      isLoading = false;
    }
  }
</script>

<main>
  <div class="container">
    <h1>{title}</h1>
    
    {#if success}
      <div class="success-message">
        <p>Your migraine day has been successfully logged!</p>
        <button on:click={() => success = false}>Log Another Day</button>
      </div>
    {:else}
      <form on:submit|preventDefault={submitForm}>
        <!-- Date Selection -->
        <div class="form-group">
          <label for="date">Date:</label>
          <input type="date" id="date" bind:value={formData.date} required>
        </div>
        
        <!-- Severity Section -->
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
        
        <!-- Triggers Section -->
        <div class="form-section">
          <h3>Potential Triggers</h3>
          <div class="trigger-grid">
            {#each triggerOptions as trigger}
              <label>
                <input 
                  type="checkbox" 
                  bind:group={formData.triggers} 
                  value={trigger.id}
                >
                {trigger.name}
              </label>
            {/each}
          </div>
        </div>
        
        <!-- Menstrual Period -->
        <div class="form-group">
          <label>
            <input 
              type="checkbox" 
              bind:checked={formData.menstrualPeriod}
            >
            Menstrual Period (for women)
          </label>
        </div>
        
        <!-- Medicines Section -->
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
        
        <!-- Notes Section -->
        <div class="form-group">
          <label for="notes">Additional Notes:</label>
          <textarea 
            id="notes" 
            bind:value={formData.notes}
            placeholder="Any other details about your migraine day..."
          ></textarea>
        </div>
        
        <!-- Submit Button -->
        <button type="submit" disabled={isLoading} class="submit-btn">
          {isLoading ? 'Saving...' : 'Save Migraine Day'}
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
    background: linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%);
  }
  
  .container {
    background: #fff;
    padding: 2rem;
    border-radius: 1rem;
    box-shadow: 0 4px 24px rgba(0,0,0,0.08);
    width: 100%;
    max-width: 800px;
  }
  
  h1 {
    color: #3b82f6;
    font-size: 2em;
    margin-bottom: 1em;
    text-align: center;
  }
  
  h3 {
    color: #6366f1;
    margin-top: 1.5rem;
    margin-bottom: 0.5rem;
  }
  
  .form-group {
    margin-bottom: 1rem;
  }
  
  .form-section {
    margin-bottom: 2rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid #e0e7ff;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    color: #4b5563;
  }
  
  input[type="text"],
  input[type="date"],
  input[type="time"],
  select,
  textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #e0e7ff;
    border-radius: 0.5rem;
    font-size: 1rem;
    margin-bottom: 1rem;
  }
  
  textarea {
    min-height: 100px;
    resize: vertical;
  }
  
  .severity-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
  }
  
  .trigger-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0.5rem;
  }
  
  .trigger-grid label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem;
    background: #f8fafc;
    border-radius: 0.25rem;
  }
  
  .medicine-entry {
    margin-bottom: 1rem;
    padding: 1rem;
    background: #f8fafc;
    border-radius: 0.5rem;
  }
  
  .medicine-row {
    display: flex;
    gap: 1rem;
    margin-bottom: 0.5rem;
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
  
  .add-btn, .submit-btn {
    background: #6366f1;
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 0.5rem;
    cursor: pointer;
    font-size: 1rem;
    transition: background 0.2s;
  }
  
  .add-btn {
    background: #e0e7ff;
    color: #6366f1;
    width: 100%;
  }
  
  .submit-btn {
    width: 100%;
    margin-top: 1rem;
    background: linear-gradient(90deg, #6366f1 0%, #3b82f6 100%);
  }
  
  .submit-btn:hover {
    background: linear-gradient(90deg, #3b82f6 0%, #6366f1 100%);
  }
  
  .submit-btn:disabled {
    background: #cbd5e1;
    cursor: not-allowed;
  }
  
  .remove-btn {
    background: none;
    border: none;
    color: #ef4444;
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0 0.5rem;
  }
  
  .error-message {
    color: #ef4444;
    margin-top: 1rem;
    padding: 0.5rem;
    background: #fee2e2;
    border-radius: 0.25rem;
  }
  
  .success-message {
    text-align: center;
    padding: 2rem;
  }
  
  .success-message p {
    color: #10b981;
    font-size: 1.2rem;
    margin-bottom: 1rem;
  }
  
  .success-message button {
    background: #3b82f6;
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 0.5rem;
    cursor: pointer;
  }
  
  @media (max-width: 768px) {
    .severity-grid {
      grid-template-columns: 1fr;
    }
    
    .trigger-grid {
      grid-template-columns: 1fr;
    }
    
    .medicine-row {
      flex-direction: column;
    }
  }
</style>