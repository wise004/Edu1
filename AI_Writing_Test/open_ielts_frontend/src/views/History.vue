<template>
  <div class="max-w-3xl mx-auto bg-white p-4 rounded shadow">
    <h2 class="text-2xl font-bold mb-4">All Submissions</h2>

    <div v-if="loading">Loading submissions...</div>
    <div v-else-if="error" class="text-red-500">{{ error }}</div>
    <div v-else>
      <div v-if="submissions.length === 0">No submissions yet.</div>
      <div v-for="(item, index) in submissions" :key="index" class="mb-4 border-b pb-2">
        <!-- Show item details -->
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      submissions: [],
      loading: false,
      error: null,
    };
  },
  async mounted() {
    this.loading = true;
    try {
      const res = await axios.get('http://localhost:8000/api/all-submissions/');
      this.submissions = res.data;
    } catch (err) {
      this.error = 'Error loading submissions';
    } finally {
      this.loading = false;
    }
  },
};
</script>
