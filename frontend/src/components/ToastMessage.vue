<template>
  <Transition name="toast">
    <div v-if="message" class="toast-message" role="status">
      {{ message }}
    </div>
  </Transition>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    default: ''
  }
});
</script>
