<template>
  <RouterView />
  <div class="cursor-aura" :style="auraStyle" aria-hidden="true"></div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useTiltGlow } from './composables/useTiltGlow';

const pointer = reactive({ x: 50, y: 50 });
const route = useRoute();
const { bindAll } = useTiltGlow();

const auraStyle = computed(() => ({
  left: `${pointer.x}px`,
  top: `${pointer.y}px`
}));

function handlePointerMove(event) {
  pointer.x = event.clientX;
  pointer.y = event.clientY;
  document.documentElement.style.setProperty('--mouse-x', `${event.clientX}px`);
  document.documentElement.style.setProperty('--mouse-y', `${event.clientY}px`);
}

watch(
  () => route.fullPath,
  async () => {
    await nextTick();
    bindAll();
  },
  { flush: 'post' }
);

onMounted(() => window.addEventListener('pointermove', handlePointerMove));
onUnmounted(() => window.removeEventListener('pointermove', handlePointerMove));
</script>
