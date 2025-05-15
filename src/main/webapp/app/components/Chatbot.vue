<!-- src/main/webapp/app/components/Chatbot.vue -->
<template>
  <div class="chatbot-container">
    <!-- Chatbot ochiq holatda -->
    <div v-if="open" class="chatbot-window">
      <div class="chatbot-header">
        <div class="flex items-center space-x-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"
            />
          </svg>
          <h4 class="chatbot-title">Chatbot bilan suhbat</h4>
        </div>
        <button @click="open = false" class="close-btn">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      <div class="chatbot-messages">
        <div v-for="(msg, i) in messages" :key="i" :class="msg.role === 'user' ? 'user-message' : 'bot-message'">
          <div class="message-wrapper">
            <span class="message-content">
              {{ msg.content }}
            </span>
            <span class="message-timestamp">
              {{ new Date().toLocaleTimeString('uz', { hour: '2-digit', minute: '2-digit' }) }}
            </span>
          </div>
        </div>
      </div>
      <div class="chatbot-input">
        <input type="text" v-model="input" @keydown.enter="sendMessage" placeholder="Savolingizni yozing..." class="input-field" />
        <button @click="sendMessage" :disabled="loading" class="send-btn">
          <svg v-if="!loading" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
          </svg>
          <span v-else class="loading-spinner"></span>
        </button>
      </div>
    </div>

    <!-- Chatbot yopiq holatda -->
    <button v-else @click="open = true" class="chatbot-toggle-btn">
      <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H7a2 2 0 01-2-2v-6a2 2 0 012-2h2m-2 6h6m-2-4h2"
        />
      </svg>
    </button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'Chatbot',
  setup() {
    const messages = ref<any[]>([{ role: 'assistant', content: 'Salom! Kurs platformamiz haqida savolingiz bormi?' }]);
    const input = ref('');
    const loading = ref(false);
    const open = ref(false); // Boshlang‘ich holatda yopiq

    const sendMessage = async () => {
      if (!input.value.trim()) return;
      loading.value = true;

      const userMessage = { role: 'user', content: input.value };
      const updatedMessages = [...messages.value, userMessage];
      messages.value = updatedMessages;
      input.value = '';

      try {
        const token = window.localStorage.getItem('jhi-authenticationToken');
        const res = await axios.post(
          '/api/chat',
          { messages: updatedMessages },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        );

        messages.value = [...updatedMessages, { role: 'assistant', content: res.data.message }];
      } catch (err) {
        messages.value = [...updatedMessages, { role: 'assistant', content: 'Xatolik yuz berdi. Iltimos, keyinroq urinib ko‘ring.' }];
      }

      loading.value = false;
    };

    return {
      messages,
      input,
      loading,
      open,
      sendMessage,
    };
  },
});
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 50;
}

.chatbot-window {
  width: 340px;
  max-height: 500px;
  background: linear-gradient(145deg, #ffffff, #f0f4f8);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.chatbot-header {
  background: linear-gradient(90deg, #007bff, #00c4ff);
  color: white;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
}

.chatbot-title {
  font-size: 1.1rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 4px;
  z-index: 10;
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  transition:
    opacity 0.2s,
    transform 0.2s;
}

.close-btn svg {
  stroke: white;
  width: 20px;
  height: 20px;
}

.close-btn:hover {
  opacity: 0.8;
  transform: translateY(-50%) rotate(90deg);
}

.chatbot-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f9fafb;
  scrollbar-width: thin;
  scrollbar-color: #007bff #e5e7eb;
}

.chatbot-messages::-webkit-scrollbar {
  width: 6px;
}

.chatbot-messages::-webkit-scrollbar-track {
  background: #e5e7eb;
  border-radius: 10px;
}

.chatbot-messages::-webkit-scrollbar-thumb {
  background: #007bff;
  border-radius: 10px;
}

.chatbot-messages::-webkit-scrollbar-thumb:hover {
  background: #0056b3;
}

.user-message,
.bot-message {
  margin-bottom: 16px;
}

.message-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.bot-message .message-wrapper {
  align-items: flex-start;
}

.message-content {
  display: inline-block;
  padding: 8px 12px;
  border-radius: 12px;
  max-width: 80%;
  font-size: 0.9rem;
  line-height: 1.4;
}

.user-message .message-content {
  background: #007bff;
  color: white;
}

.bot-message .message-content {
  background: #e5e7eb;
  color: #1f2937;
}

.message-timestamp {
  font-size: 0.65rem;
  color: #6b7280;
  margin-top: 2px;
}

.chatbot-input {
  display: flex;
  padding: 12px;
  background: white;
  border-top: 1px solid #e5e7eb;
}

.input-field {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s;
}

.input-field:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.1);
}

.send-btn {
  margin-left: 8px;
  padding: 8px;
  background: #007bff;
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: background 0.2s;
}

.send-btn:hover {
  background: #0056b3;
}

.send-btn:disabled {
  background: #a3bffa;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid white;
  border-top: 2px solid transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.chatbot-toggle-btn {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  padding: 14px;
  border-radius: 50%;
  border: none;
  color: white;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.chatbot-toggle-btn:hover {
  transform: scale(1.15);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.chatbot-toggle-btn svg {
  width: 28px;
  height: 28px;
  stroke: white;
  stroke-width: 2;
}
</style>
