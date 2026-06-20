<template>
  <span class="flip-counter" :class="{ 'is-ticking': ticking }">
    <span class="flip-counter__top">{{ displayText }}</span>
    <span class="flip-counter__bottom">{{ displayText }}</span>
  </span>
</template>

<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue';

const props = defineProps({
  value: {
    type: Number,
    default: 0
  },
  duration: {
    type: Number,
    default: 1100
  },
  suffix: {
    type: String,
    default: ''
  }
});

const displayValue = ref(0);
const ticking = ref(false);
let frameId = 0;
let tickTimer = 0;

const displayText = computed(() => `${displayValue.value}${props.suffix}`);

function pulseTick() {
  ticking.value = false;
  window.clearTimeout(tickTimer);
  requestAnimationFrame(() => {
    ticking.value = true;
    tickTimer = window.setTimeout(() => {
      ticking.value = false;
    }, 180);
  });
}

function animateTo(target) {
  cancelAnimationFrame(frameId);

  const startValue = displayValue.value;
  const startTime = performance.now();
  const delta = target - startValue;

  if (!delta) {
    displayValue.value = target;
    return;
  }

  let lastRendered = startValue;

  const tick = (now) => {
    const progress = Math.min(1, (now - startTime) / props.duration);
    const eased = 1 - Math.pow(1 - progress, 3);
    const nextValue = Math.round(startValue + delta * eased);

    if (nextValue !== lastRendered) {
      displayValue.value = nextValue;
      pulseTick();
      lastRendered = nextValue;
    }

    if (progress < 1) {
      frameId = requestAnimationFrame(tick);
    }
  };

  frameId = requestAnimationFrame(tick);
}

watch(
  () => props.value,
  (value) => animateTo(Number(value) || 0),
  { immediate: true }
);

onBeforeUnmount(() => {
  cancelAnimationFrame(frameId);
  window.clearTimeout(tickTimer);
});
</script>
