<template>
  <main
    ref="stageRef"
    class="landing-page promo-page"
    :class="pageClasses"
    tabindex="0"
    @wheel.prevent="handleWheel"
    @keydown="handleKeydown"
    @touchstart.passive="handleTouchStart"
    @touchend.passive="handleTouchEnd"
  >
    <nav class="landing-nav glass-card promo-nav">
      <RouterLink class="brand" to="/">
        <span class="brand-orb"></span>
        拾光校园
      </RouterLink>
      <div class="promo-nav-actions">
        <button type="button" class="nav-link" @click="goToPanel(1)">核心亮点</button>
        <button type="button" class="nav-link" @click="goToPanel(2)">平台价值</button>
        <button type="button" class="nav-login" @click="enterPlatform">进入平台</button>
      </div>
    </nav>

    <div class="promo-shell">
      <div class="promo-stack">
        <section
          v-for="(panel, index) in panels"
          :key="panel.id"
          class="promo-panel"
          :class="panelClasses(index)"
          :style="panelTheme(panel)"
        >
          <div class="panel-grid">
            <div class="panel-copy">
              <h1>{{ panel.title }}</h1>
              <h2>{{ panel.subtitle }}</h2>

              <div class="hero-actions">
                <AnimatedButton @click="enterPlatform">立即进入</AnimatedButton>
                <AnimatedButton variant="ghost" @click="handleSecondary(index)">
                  {{ index === panels.length - 1 ? '回到首屏' : '继续翻屏' }}
                </AnimatedButton>
              </div>
            </div>

            <div class="panel-visual">
              <div class="scene-core" :class="`scene-core--${panel.id}`">
                <div v-if="panel.id === 'trace'" class="scene-track">
                  <div class="scene-track__grid"></div>
                  <div class="scene-track__shape scene-track__shape--pillar"></div>
                  <div class="scene-track__shape scene-track__shape--sheet"></div>
                  <div class="scene-track__shape scene-track__shape--capsule"></div>
                  <div class="scene-track__shape scene-track__shape--tag"></div>
                  <div class="scene-track__orb scene-track__orb--a"></div>
                  <div class="scene-track__orb scene-track__orb--b"></div>
                  <div class="scene-track__pulse scene-track__pulse--a"></div>
                  <div class="scene-track__pulse scene-track__pulse--b"></div>
                  <div class="scene-track__route scene-track__route--main"></div>
                  <div class="scene-track__route scene-track__route--sub"></div>
                  <div class="scene-track__scan"></div>
                  <div class="scene-track__pin"></div>
                  <div class="scene-track__marker scene-track__marker--a"></div>
                  <div class="scene-track__marker scene-track__marker--b"></div>
                  <div class="scene-track__marker scene-track__marker--c"></div>
                  <div class="scene-track__dots">
                    <span v-for="n in 8" :key="`trace-dot-${n}`" :style="{ '--dot-index': n }"></span>
                  </div>
                </div>

                <div v-else-if="panel.id === 'flow'" class="scene-flow">
                  <div class="scene-flow__grid"></div>
                  <div class="scene-flow__sheet scene-flow__sheet--center"></div>
                  <div class="scene-flow__sheet scene-flow__sheet--top"></div>
                  <div class="scene-flow__sheet scene-flow__sheet--right"></div>
                  <div class="scene-flow__sheet scene-flow__sheet--bottom"></div>
                  <div class="scene-flow__arrow scene-flow__arrow--a"></div>
                  <div class="scene-flow__arrow scene-flow__arrow--b"></div>
                  <div class="scene-flow__ring"></div>
                  <div class="scene-flow__chip scene-flow__chip--a"></div>
                  <div class="scene-flow__chip scene-flow__chip--b"></div>
                  <div class="scene-flow__diamond"></div>
                  <div class="scene-flow__glow-dot scene-flow__glow-dot--a"></div>
                  <div class="scene-flow__glow-dot scene-flow__glow-dot--b"></div>
                  <div class="scene-flow__queue">
                    <span v-for="n in 4" :key="`flow-queue-${n}`" :style="{ '--queue-index': n }"></span>
                  </div>
                  <div class="scene-flow__orb"></div>
                </div>

                <div v-else class="scene-shield">
                  <div class="scene-shield__grid"></div>
                  <div class="scene-shield__slab scene-shield__slab--top"></div>
                  <div class="scene-shield__slab scene-shield__slab--right"></div>
                  <div class="scene-shield__slab scene-shield__slab--bottom"></div>
                  <div class="scene-shield__slab scene-shield__slab--left"></div>
                  <div class="scene-shield__ring scene-shield__ring--a"></div>
                  <div class="scene-shield__ring scene-shield__ring--b"></div>
                  <div class="scene-shield__arc scene-shield__arc--a"></div>
                  <div class="scene-shield__arc scene-shield__arc--b"></div>
                  <div class="scene-shield__glass scene-shield__glass--a"></div>
                  <div class="scene-shield__glass scene-shield__glass--b"></div>
                  <div class="scene-shield__glass scene-shield__glass--c"></div>
                  <div class="scene-shield__beam scene-shield__beam--a"></div>
                  <div class="scene-shield__beam scene-shield__beam--b"></div>
                  <div class="scene-shield__node scene-shield__node--a"></div>
                  <div class="scene-shield__node scene-shield__node--b"></div>
                  <div class="scene-shield__node scene-shield__node--c"></div>
                </div>
              </div>

              <div class="particle-layer panel-particles" aria-hidden="true">
                <span v-for="n in 18" :key="`${panel.id}-${n}`" :style="{ '--i': n }"></span>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <aside class="promo-dots" aria-label="首屏切换">
      <button
        v-for="(panel, index) in panels"
        :key="panel.id"
        type="button"
        class="promo-dot"
        :class="{ 'is-active': activeIndex === index }"
        :aria-label="panel.title"
        @click="goToPanel(index)"
      >
        <span>{{ String(index + 1).padStart(2, '0') }}</span>
      </button>
    </aside>

    <button type="button" class="flip-hint" @click="handleSecondary(activeIndex)">
      <span>向下翻屏</span>
      <i></i>
    </button>

    <div class="route-burst" aria-hidden="true">
      <span class="burst-core"></span>
      <span class="burst-ring burst-ring-a"></span>
      <span class="burst-ring burst-ring-b"></span>
    </div>
  </main>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import AnimatedButton from '../components/AnimatedButton.vue';
import { playPageTransition } from '../utils/pageTransition';

const router = useRouter();
const stageRef = ref(null);
const activeIndex = ref(0);
const previousIndex = ref(null);
const isAnimating = ref(false);
const transitionDirection = ref(1);
const isRouting = ref(false);
const touchStartY = ref(0);
const hasPanelTransitioned = ref(false);

const panels = [
  {
    id: 'trace',
    title: '拾光校园',
    subtitle: '让每一件遗失，都有回来的方向',
    stats: [
      { value: '24h', label: '热门失物持续追踪' },
      { value: '3D', label: '分层翻屏动态呈现' }
    ],
    accent: '#22b39b',
    accentSoft: 'rgba(34, 179, 155, 0.26)'
  },
  {
    id: 'flow',
    title: '拾光校园',
    subtitle: '招领流程更顺滑，协作效率更直接',
    stats: [
      { value: '4步', label: '完成从发布到认领' },
      { value: '实时', label: '审核与通知同步推进' }
    ],
    accent: '#f0b94a',
    accentSoft: 'rgba(240, 185, 74, 0.24)'
  },
  {
    id: 'shield',
    title: '拾光校园',
    subtitle: '让平台治理、校园协作和安全感一起到位',
    stats: [
      { value: '100%', label: '流程全链路可见' },
      { value: '安心', label: '校园协作体验升级' }
    ],
    accent: '#8fd6ff',
    accentSoft: 'rgba(143, 214, 255, 0.22)'
  }
];

const pageClasses = computed(() => ({
  'is-flipping': isAnimating.value,
  'flip-forward': isAnimating.value && transitionDirection.value > 0,
  'flip-backward': isAnimating.value && transitionDirection.value < 0,
  'is-routing': isRouting.value
}));

function panelTheme(panel) {
  return {
    '--panel-accent': panel.accent,
    '--panel-accent-soft': panel.accentSoft
  };
}

function panelClasses(index) {
  if (index === activeIndex.value) {
    if (previousIndex.value === null && !hasPanelTransitioned.value) {
      return 'is-active is-initial';
    }
    return transitionDirection.value > 0 ? 'is-active enter-from-bottom' : 'is-active enter-from-top';
  }

  if (index === previousIndex.value) {
    return transitionDirection.value > 0 ? 'is-leaving leave-to-top' : 'is-leaving leave-to-bottom';
  }

  return index < activeIndex.value ? 'is-hidden above' : 'is-hidden below';
}

function lockAnimation() {
  isAnimating.value = true;
  window.setTimeout(() => {
    isAnimating.value = false;
    previousIndex.value = null;
  }, 980);
}

function goToPanel(index) {
  if (isAnimating.value || index === activeIndex.value || index < 0 || index >= panels.length) {
    return;
  }

  hasPanelTransitioned.value = true;
  transitionDirection.value = index > activeIndex.value ? 1 : -1;
  previousIndex.value = activeIndex.value;
  activeIndex.value = index;
  lockAnimation();
}

function goNext() {
  if (activeIndex.value < panels.length - 1) {
    goToPanel(activeIndex.value + 1);
  } else {
    goToPanel(0);
  }
}

function goPrev() {
  if (activeIndex.value > 0) {
    goToPanel(activeIndex.value - 1);
  }
}

function handleSecondary(index) {
  if (index === panels.length - 1) {
    goToPanel(0);
    return;
  }
  goToPanel(index + 1);
}

function enterPlatform() {
  if (isRouting.value) {
    return;
  }

  isRouting.value = true;
  playPageTransition(() => router.push('/login'));
}

function handleWheel(event) {
  if (isAnimating.value || Math.abs(event.deltaY) < 32) {
    return;
  }
  if (event.deltaY > 0) {
    goNext();
  } else {
    goPrev();
  }
}

function handleKeydown(event) {
  if (event.key === 'ArrowDown' || event.key === 'PageDown') {
    event.preventDefault();
    goNext();
  }
  if (event.key === 'ArrowUp' || event.key === 'PageUp') {
    event.preventDefault();
    goPrev();
  }
}

function handleTouchStart(event) {
  touchStartY.value = event.changedTouches[0]?.clientY || 0;
}

function handleTouchEnd(event) {
  const endY = event.changedTouches[0]?.clientY || 0;
  const delta = touchStartY.value - endY;
  if (Math.abs(delta) < 42 || isAnimating.value) {
    return;
  }
  if (delta > 0) {
    goNext();
  } else {
    goPrev();
  }
}

onMounted(async () => {
  document.body.style.overflow = 'hidden';
  await nextTick();
  stageRef.value?.focus();
});

onUnmounted(() => {
  document.body.style.overflow = '';
});
</script>

<style scoped>
.promo-page {
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 18% 16%, rgba(246, 196, 83, 0.22), transparent 18rem),
    radial-gradient(circle at 82% 22%, rgba(34, 179, 155, 0.16), transparent 22rem),
    linear-gradient(135deg, #071c18 0%, #10322c 36%, #e9f3ed 100%);
  transition: filter 0.72s ease, transform 0.72s ease;
}

.promo-page::before {
  transition: transform 0.9s cubic-bezier(0.18, 0.9, 0.22, 1.02), filter 0.9s ease, opacity 0.9s ease;
}

.promo-page::after {
  transition: transform 0.9s cubic-bezier(0.18, 0.9, 0.22, 1.02), opacity 0.9s ease;
}

.promo-nav {
  position: fixed;
  top: 22px;
  left: 50%;
  z-index: 20;
  width: min(1180px, calc(100% - 40px));
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.48);
}

.promo-nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-link,
.nav-login {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  padding: 0 16px;
  border: 0;
  border-radius: 999px;
  color: var(--green-deep);
  cursor: pointer;
  background: rgba(255, 255, 255, 0.52);
  transition: transform 0.28s var(--spring), background 0.28s var(--smooth);
}

.nav-link:hover,
.nav-login:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.78);
}

.nav-login {
  color: white;
  background: linear-gradient(135deg, var(--green-deep), var(--green));
}

.promo-shell {
  height: 100vh;
  overflow: hidden;
  perspective: 2200px;
  transition: transform 0.82s cubic-bezier(0.18, 0.9, 0.22, 1.02), filter 0.82s ease;
}

.promo-stack {
  position: relative;
  height: 100%;
  transform-style: preserve-3d;
  transition: transform 0.82s cubic-bezier(0.18, 0.9, 0.22, 1.02);
}

.promo-panel {
  position: absolute;
  inset: 0;
  min-height: 100vh;
  padding: 112px clamp(20px, 4vw, 68px) 48px;
  overflow: hidden;
  opacity: 0;
  pointer-events: none;
  transform-origin: center center;
  will-change: transform, opacity, filter;
  transition:
    transform 0.96s cubic-bezier(0.2, 0.9, 0.2, 1),
    opacity 0.96s ease,
    filter 0.96s ease;
}

.promo-panel::before {
  position: absolute;
  inset: 0;
  content: "";
  background:
    radial-gradient(circle at 20% 28%, var(--panel-accent-soft), transparent 18rem),
    radial-gradient(circle at 84% 18%, rgba(255, 255, 255, 0.18), transparent 16rem);
  opacity: 0.9;
}

.promo-panel::after {
  position: absolute;
  inset: 0;
  content: "";
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.03), transparent 34%),
    linear-gradient(transparent, rgba(0, 0, 0, 0.18));
}

.panel-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(320px, 0.92fr);
  align-items: center;
  gap: clamp(28px, 5vw, 72px);
  max-width: 1220px;
  height: calc(100vh - 160px);
  margin: 0 auto;
  transform-style: preserve-3d;
  transition: transform 0.88s cubic-bezier(0.18, 0.9, 0.22, 1.02);
}

.panel-copy {
  color: white;
  transform-style: preserve-3d;
  transition: transform 0.86s cubic-bezier(0.18, 0.9, 0.22, 1.02);
}

.panel-copy h1 {
  margin: 0;
  font-size: clamp(4.1rem, 11vw, 8.2rem);
  line-height: 0.9;
  letter-spacing: -0.08em;
}

.panel-copy h2 {
  max-width: 640px;
  margin: 20px 0 0;
  font-size: clamp(1.6rem, 3.8vw, 3.1rem);
  line-height: 1.08;
}

.panel-visual {
  position: relative;
  min-height: 560px;
  perspective: 1600px;
  transform-style: preserve-3d;
  transition: transform 0.86s cubic-bezier(0.18, 0.9, 0.22, 1.02);
}

.scene-core {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  padding: 0;
  border: 0;
  background: none;
  box-shadow: none;
  transform: rotateY(-8deg) rotateX(4deg);
  overflow: hidden;
  transform-style: preserve-3d;
}

.scene-core::before {
  display: none;
}

.scene-core::after {
  display: none;
}

.scene-core--trace {
  filter: drop-shadow(0 40px 82px rgba(7, 28, 24, 0.18));
}

.scene-core--flow {
  filter: drop-shadow(0 40px 82px rgba(7, 28, 24, 0.16));
}

.scene-core--shield {
  filter: drop-shadow(0 40px 82px rgba(7, 28, 24, 0.16));
}

.scene-core--trace,
.scene-core--flow,
.scene-core--shield {
  animation: visualFloat 6.5s ease-in-out infinite;
}

.scene-track,
.scene-flow,
.scene-shield {
  position: absolute;
  inset: 0;
}

.scene-track__grid {
  position: absolute;
  inset: 56px 56px 48px 72px;
  border-radius: 34px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.08) 1px, transparent 1px);
  background-size: 28px 28px;
  opacity: 0.86;
}

.scene-track__shape,
.scene-flow__sheet,
.scene-flow__chip,
.scene-shield__slab,
.scene-shield__glass {
  position: absolute;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.28), rgba(255, 255, 255, 0.06));
  box-shadow: 0 20px 56px rgba(7, 28, 24, 0.18);
}

.scene-track__shape--pillar {
  top: 122px;
  left: 94px;
  width: 84px;
  height: 242px;
  border-radius: 34px;
  background: linear-gradient(180deg, rgba(31, 157, 136, 0.52), rgba(255, 255, 255, 0.04));
}

.scene-track__shape--sheet {
  top: 108px;
  left: 200px;
  width: 214px;
  height: 124px;
  border-radius: 30px;
}

.scene-track__shape--capsule {
  bottom: 106px;
  left: 182px;
  width: 242px;
  height: 108px;
  border-radius: 999px;
}

.scene-track__shape--tag {
  top: 238px;
  right: 110px;
  width: 164px;
  height: 172px;
  border-radius: 28px;
  clip-path: polygon(16% 0, 100% 0, 100% 84%, 78% 100%, 0 100%, 0 14%);
}

.scene-track__orb,
.scene-flow__orb,
.scene-shield__node {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.56), rgba(255, 255, 255, 0.04));
  box-shadow: 0 0 42px rgba(143, 214, 255, 0.2);
}

.scene-track__orb--a {
  top: 76px;
  right: 74px;
  width: 112px;
  height: 112px;
}

.scene-track__orb--b {
  bottom: 108px;
  right: 208px;
  width: 68px;
  height: 68px;
}

.scene-track__pulse {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.14);
  animation: rotateRing 12s linear infinite;
}

.scene-track__pulse--a {
  top: 54px;
  right: 50px;
  width: 164px;
  height: 164px;
}

.scene-track__pulse--b {
  bottom: 86px;
  left: 52px;
  width: 124px;
  height: 124px;
  animation-direction: reverse;
}

.scene-track__route,
.scene-flow__lane,
.scene-flow__arrow,
.scene-shield__arc {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.88), transparent);
  box-shadow: 0 0 24px rgba(255, 255, 255, 0.22);
}

.scene-track__route--main {
  left: 100px;
  top: 188px;
  width: 320px;
  height: 3px;
  transform: rotate(-18deg);
}

.scene-track__route--sub {
  right: 78px;
  bottom: 114px;
  width: 190px;
  height: 2px;
  transform: rotate(-12deg);
}

.scene-track__scan {
  position: absolute;
  inset: 70px 78px 64px 92px;
  border-radius: 30px;
  background: linear-gradient(180deg, transparent 0%, rgba(255, 255, 255, 0.08) 46%, transparent 56%);
  opacity: 0.7;
  mix-blend-mode: screen;
  animation: scanSweep 4.8s ease-in-out infinite;
}

.scene-track__pin {
  position: absolute;
  top: 126px;
  right: 126px;
  width: 44px;
  height: 58px;
  border-radius: 28px 28px 28px 6px;
  background: linear-gradient(180deg, rgba(34, 179, 155, 0.95), rgba(17, 103, 91, 0.88));
  transform: rotate(34deg);
  box-shadow: 0 0 28px rgba(34, 179, 155, 0.34);
}

.scene-track__pin::after {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 14px;
  height: 14px;
  content: "";
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.86);
}

.scene-track__marker {
  position: absolute;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: rgba(246, 196, 83, 0.94);
  box-shadow: 0 0 0 8px rgba(246, 196, 83, 0.12);
}

.scene-track__marker--a {
  top: 206px;
  left: 182px;
}

.scene-track__marker--b {
  top: 304px;
  left: 348px;
}

.scene-track__marker--c {
  bottom: 116px;
  right: 166px;
}

.scene-track__dots {
  position: absolute;
  top: 22px;
  left: 58px;
  display: grid;
  gap: 10px;
  transform: rotate(18deg);
}

.scene-track__dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--panel-accent) 78%, white 22%);
  box-shadow: 0 0 18px color-mix(in srgb, var(--panel-accent) 76%, white 24%);
  animation: dotTrail 3.2s ease-in-out infinite;
  animation-delay: calc(var(--dot-index, 0) * 0.12s);
}

.scene-flow__ribbon {
  position: absolute;
  height: 12px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(246, 196, 83, 0), rgba(246, 196, 83, 0.88), rgba(110, 242, 222, 0.88), rgba(110, 242, 222, 0));
  box-shadow: 0 0 28px rgba(246, 196, 83, 0.18);
}

.scene-flow__ribbon--a {
  top: 130px;
  left: 150px;
  width: 332px;
  transform: rotate(10deg);
}

.scene-flow__ribbon--b {
  bottom: 116px;
  left: 118px;
  width: 280px;
  transform: rotate(-8deg);
}

.scene-flow__grid {
  position: absolute;
  inset: 88px 102px 72px 142px;
  border-radius: 36px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.06) 1px, transparent 1px);
  background-size: 30px 30px;
  transform: rotate(-8deg);
  opacity: 0.46;
}

.scene-flow__sheet--center {
  inset: 150px 124px 86px 168px;
  border-radius: 40px;
  opacity: 0.4;
  transform: rotate(-8deg);
}

.scene-flow__sheet--top {
  top: 82px;
  left: 112px;
  width: 126px;
  height: 126px;
  border-radius: 2px;
  opacity: 0.34;
  transform: rotate(10deg);
}

.scene-flow__sheet--right {
  top: 96px;
  right: 112px;
  width: 104px;
  height: 168px;
  border-radius: 2px;
  opacity: 0.34;
  transform: rotate(8deg);
}

.scene-flow__sheet--bottom {
  bottom: 74px;
  left: 76px;
  width: 174px;
  height: 86px;
  border-radius: 2px;
  opacity: 0.34;
  transform: rotate(-6deg);
}

.scene-flow__arrow {
  height: 5px;
}

.scene-flow__arrow::after {
  position: absolute;
  top: 50%;
  right: -8px;
  width: 18px;
  height: 18px;
  content: "";
  border-top: 4px solid rgba(110, 242, 222, 0.9);
  border-right: 4px solid rgba(110, 242, 222, 0.9);
  transform: translateY(-50%) rotate(45deg);
}

.scene-flow__arrow--a {
  top: 154px;
  left: 278px;
  width: 178px;
  transform: rotate(16deg);
}

.scene-flow__arrow--b {
  bottom: 136px;
  left: 132px;
  width: 160px;
  transform: rotate(-14deg);
}

.scene-flow__chip--a {
  top: 118px;
  right: 118px;
  width: 76px;
  height: 76px;
  border-radius: 22px;
  clip-path: polygon(50% 0, 100% 28%, 100% 72%, 50% 100%, 0 72%, 0 28%);
}

.scene-flow__chip--b {
  bottom: 88px;
  right: 142px;
  width: 56px;
  height: 56px;
  border-radius: 18px;
}

.scene-flow__diamond {
  position: absolute;
  right: 142px;
  bottom: 56px;
  width: 122px;
  height: 122px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 2px;
  background: linear-gradient(135deg, rgba(246, 196, 83, 0.34), rgba(255, 255, 255, 0.04));
  transform: rotate(14deg);
  box-shadow: 0 22px 56px rgba(7, 28, 24, 0.18);
}

.scene-flow__ring {
  position: absolute;
  top: 78px;
  right: 82px;
  width: 176px;
  height: 176px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.scene-flow__queue {
  position: absolute;
  right: 96px;
  bottom: 92px;
  display: grid;
  gap: 10px;
}

.scene-flow__queue span {
  width: calc(108px - var(--queue-index, 0) * 14px);
  height: 12px;
  margin-left: calc(var(--queue-index, 0) * 8px);
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.12), rgba(246, 196, 83, 0.84));
  opacity: calc(1 - var(--queue-index, 0) * 0.14);
}

.scene-flow__glow-dot {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(246, 196, 83, 0.94);
  box-shadow: 0 0 0 8px rgba(246, 196, 83, 0.12);
}

.scene-flow__glow-dot--a {
  top: 278px;
  right: 216px;
}

.scene-flow__glow-dot--b {
  bottom: 122px;
  right: 168px;
}

.scene-flow__orb {
  top: 118px;
  left: 116px;
  width: 104px;
  height: 104px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.62), rgba(255, 255, 255, 0.1));
  animation: slowPulse 4.4s ease-in-out infinite;
}

.scene-shield__grid {
  position: absolute;
  inset: 88px 86px 78px 122px;
  border-radius: 36px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 28px 28px;
  transform: rotate(-7deg);
  opacity: 0.28;
}

.scene-shield__slab--top {
  top: 92px;
  left: 144px;
  width: 126px;
  height: 98px;
  border-radius: 2px;
  opacity: 0.26;
  transform: rotate(10deg);
}

.scene-shield__slab--right {
  top: 112px;
  right: 126px;
  width: 94px;
  height: 144px;
  border-radius: 2px;
  opacity: 0.26;
  transform: rotate(8deg);
}

.scene-shield__slab--bottom {
  bottom: 68px;
  left: 86px;
  width: 164px;
  height: 84px;
  border-radius: 2px;
  opacity: 0.26;
  transform: rotate(-8deg);
}

.scene-shield__slab--left {
  top: 248px;
  left: 92px;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  opacity: 0.18;
}

.scene-shield__ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.scene-shield__ring--a {
  top: 144px;
  right: 178px;
  width: 148px;
  height: 148px;
}

.scene-shield__ring--b {
  top: 108px;
  left: 132px;
  width: 104px;
  height: 104px;
}

.scene-shield__arc {
  height: 8px;
  border-radius: 999px;
}

.scene-shield__arc--a {
  top: 186px;
  right: 92px;
  width: 156px;
  transform: rotate(28deg);
}

.scene-shield__arc--b {
  bottom: 140px;
  left: 144px;
  width: 176px;
  transform: rotate(-20deg);
}

.scene-shield__glass--a {
  top: 118px;
  left: 132px;
  width: 112px;
  height: 112px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.36), rgba(255, 255, 255, 0.04));
}

.scene-shield__glass--b {
  top: 212px;
  right: 132px;
  width: 82px;
  height: 82px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.04));
}

.scene-shield__glass--c {
  bottom: 92px;
  right: 214px;
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.24), rgba(255, 255, 255, 0.04));
}

.scene-shield__beam {
  position: absolute;
  height: 6px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(143, 214, 255, 0), rgba(143, 214, 255, 0.95), rgba(143, 214, 255, 0));
  box-shadow: 0 0 24px rgba(143, 214, 255, 0.3);
}

.scene-shield__beam--a {
  --beam-rotate: 16deg;
  top: 186px;
  left: 132px;
  width: 144px;
  transform: rotate(var(--beam-rotate));
}

.scene-shield__beam--b {
  --beam-rotate: -10deg;
  bottom: 154px;
  left: 162px;
  width: 168px;
  transform: rotate(var(--beam-rotate));
}

.scene-shield__node--a {
  top: 88px;
  right: 52px;
  width: 16px;
  height: 16px;
  background: radial-gradient(circle, rgba(143, 214, 255, 0.98), rgba(143, 214, 255, 0.28));
}

.scene-shield__node--b {
  top: 138px;
  right: 62px;
  width: 10px;
  height: 10px;
  background: radial-gradient(circle, rgba(246, 196, 83, 0.98), rgba(246, 196, 83, 0.28));
}

.scene-shield__node--c {
  bottom: 106px;
  right: 134px;
  width: 18px;
  height: 18px;
  background: radial-gradient(circle, rgba(143, 214, 255, 0.98), rgba(143, 214, 255, 0.28));
}

.scene-track__shape,
.scene-track__orb,
.scene-track__pulse,
.scene-track__pin,
.scene-track__marker {
  animation: sceneFloat 6.2s ease-in-out infinite;
}

.scene-track__shape--tag,
.scene-track__pulse--a {
  animation-delay: -1.2s;
}

.scene-track__shape--capsule,
.scene-track__orb--b {
  animation-delay: -2.1s;
}

.scene-track__shape--pillar,
.scene-track__marker--b {
  animation-delay: -0.8s;
}

.scene-track__scan,
.scene-track__route {
  animation: routePulse 4.2s ease-in-out infinite;
}

.scene-flow__sheet,
.scene-flow__chip,
.scene-flow__arrow,
.scene-flow__diamond,
.scene-flow__ring,
.scene-flow__glow-dot {
  animation: sceneDrift 6s ease-in-out infinite;
}

.scene-flow__sheet--top,
.scene-flow__chip--a {
  animation-delay: -1.4s;
}

.scene-flow__sheet--right,
.scene-flow__arrow--b {
  animation-delay: -2.4s;
}

.scene-flow__ribbon,
.scene-flow__queue span {
  animation: streamGlow 4.8s ease-in-out infinite;
}

.scene-flow__ribbon--b,
.scene-flow__queue span:nth-child(2) {
  animation-delay: -1.2s;
}

.scene-shield__slab,
.scene-shield__ring,
.scene-shield__glass,
.scene-shield__node,
.scene-shield__arc {
  animation: sceneFloat 6.6s ease-in-out infinite;
}

.scene-shield__slab--right,
.scene-shield__ring--b,
.scene-shield__node--b {
  animation-delay: -1.6s;
}

.scene-shield__glass--c,
.scene-shield__arc--b,
.scene-shield__node--c {
  animation-delay: -2.4s;
}

.scene-shield__beam {
  animation: shieldBeam 4.6s ease-in-out infinite;
}

.stat-card {
  position: absolute;
  display: grid;
  gap: 4px;
  min-width: 190px;
  padding: 18px 20px;
  border-radius: 24px;
  color: white;
  background: rgba(9, 25, 22, 0.54);
  backdrop-filter: blur(16px);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.2);
  animation: floatBadge 5.6s ease-in-out infinite;
}

.stat-card strong {
  font-size: 1.7rem;
}

.stat-card span {
  color: rgba(255, 255, 255, 0.76);
}

.stat-card-top {
  top: 6%;
  left: 2%;
}

.stat-card-bottom {
  right: 0;
  bottom: 8%;
  animation-delay: -2.4s;
}

.panel-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.panel-particles span {
  position: absolute;
  width: 8px;
  height: 8px;
  left: calc((var(--i) * 49px) % 100%);
  top: calc((var(--i) * 67px) % 100%);
  border-radius: 50%;
  background: color-mix(in srgb, var(--panel-accent) 72%, white 28%);
  box-shadow: 0 0 22px color-mix(in srgb, var(--panel-accent) 60%, white 40%);
  animation: panelParticle 9s linear infinite;
  animation-delay: calc(var(--i) * -0.46s);
}

.promo-dots {
  position: fixed;
  top: 50%;
  right: 24px;
  z-index: 15;
  display: grid;
  gap: 12px;
  transform: translateY(-50%);
}

.promo-dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 50%;
  color: rgba(255, 255, 255, 0.72);
  cursor: pointer;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(16px);
  transition: transform 0.28s var(--spring), background 0.28s var(--smooth), color 0.28s var(--smooth);
}

.promo-dot:hover,
.promo-dot.is-active {
  color: white;
  background: rgba(255, 255, 255, 0.22);
  transform: scale(1.08);
}

.flip-hint {
  position: fixed;
  bottom: 28px;
  left: 50%;
  z-index: 16;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 999px;
  color: white;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(16px);
  transform: translateX(-50%);
}

.flip-hint i {
  width: 10px;
  height: 10px;
  border-right: 2px solid currentColor;
  border-bottom: 2px solid currentColor;
  transform: rotate(45deg);
  animation: hintBounce 1.4s ease-in-out infinite;
}

.route-burst {
  position: fixed;
  inset: 0;
  z-index: 40;
  pointer-events: none;
  opacity: 0;
}

.burst-core,
.burst-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  border-radius: 50%;
  transform: translate(-50%, -50%) scale(0.2);
}

.burst-core {
  width: 18vw;
  height: 18vw;
  min-width: 180px;
  min-height: 180px;
  background: radial-gradient(circle, rgba(246, 196, 83, 0.88), rgba(31, 157, 136, 0.8) 52%, rgba(31, 157, 136, 0) 76%);
  filter: blur(16px);
}

.burst-ring {
  border: 2px solid rgba(255, 255, 255, 0.42);
}

.burst-ring-a {
  width: 28vw;
  height: 28vw;
  min-width: 280px;
  min-height: 280px;
}

.burst-ring-b {
  width: 42vw;
  height: 42vw;
  min-width: 420px;
  min-height: 420px;
}

.promo-panel.is-active,
.promo-panel.is-leaving {
  opacity: 1;
  pointer-events: auto;
}

.promo-panel.is-initial {
  animation: none;
}

.promo-panel.is-active {
  z-index: 3;
  filter: blur(0);
}

.promo-panel.is-leaving {
  z-index: 2;
}

.promo-panel.is-hidden {
  z-index: 1;
  filter: blur(8px);
}

.promo-panel.above {
  transform: translate3d(0, -14%, -120px) rotateX(16deg) scale(0.92);
}

.promo-panel.below {
  transform: translate3d(0, 14%, -120px) rotateX(-16deg) scale(0.92);
}

.promo-panel.enter-from-bottom {
  animation: panelEnterFromBottom 0.96s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-panel.enter-from-top {
  animation: panelEnterFromTop 0.96s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-panel.leave-to-top {
  animation: panelLeaveToTop 0.96s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-panel.leave-to-bottom {
  animation: panelLeaveToBottom 0.96s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-panel .panel-copy h1,
.promo-panel .panel-copy h2,
.promo-panel .hero-actions,
.promo-panel .scene-core,
.promo-panel .stat-card-top,
.promo-panel .stat-card-bottom {
  opacity: 0;
}

.promo-panel.is-active .panel-copy h1 {
  animation: gatherLeft 0.95s 0.08s cubic-bezier(0.2, 0.9, 0.22, 1.02) both;
}

.promo-panel.is-active .panel-copy h2 {
  animation: gatherRight 1s 0.16s cubic-bezier(0.2, 0.9, 0.22, 1.02) both;
}

.promo-panel.is-active .hero-actions {
  animation: gatherBottom 1s 0.24s cubic-bezier(0.2, 0.9, 0.22, 1.02) both;
}

.promo-panel.is-active .scene-core {
  animation: gatherTop 1s 0.12s cubic-bezier(0.2, 0.9, 0.22, 1.02) both;
}

.promo-panel.is-active .stat-card-top {
  animation: gatherLeft 1s 0.28s cubic-bezier(0.2, 0.9, 0.22, 1.02) both;
}

.promo-panel.is-active .stat-card-bottom {
  animation: gatherRight 1s 0.34s cubic-bezier(0.2, 0.9, 0.22, 1.02) both;
}

.promo-panel.is-initial .panel-copy h1,
.promo-panel.is-initial .panel-copy h2,
.promo-panel.is-initial .hero-actions,
.promo-panel.is-initial .scene-core,
.promo-panel.is-initial .stat-card-top,
.promo-panel.is-initial .stat-card-bottom {
  opacity: 1;
  animation: none;
}

.promo-page.is-flipping .promo-shell {
  transform: perspective(2200px) translateZ(28px) scale(1.06);
  filter: saturate(1.08) contrast(1.04);
  animation: screenJolt 0.34s ease;
}

.promo-page.is-flipping .promo-stack {
  transform: perspective(1800px) translateZ(52px);
}

.promo-page.is-flipping .panel-grid {
  transform: scale(1.05);
}

.promo-page.is-flipping .promo-panel.is-leaving::before {
  transform: scale(1.28);
  filter: blur(8px);
  opacity: 1;
}

.promo-page.is-flipping .promo-panel.is-active::before {
  transform: scale(0.92);
  filter: blur(2px);
}

.promo-page.flip-forward .promo-panel.is-leaving .panel-grid {
  transform: perspective(1800px) translateZ(-190px) rotateX(11deg) scale(0.87);
}

.promo-page.flip-forward .promo-panel.is-active .panel-grid {
  transform: perspective(1800px) translateZ(140px) rotateX(-4deg) scale(1.07);
}

.promo-page.flip-backward .promo-panel.is-leaving .panel-grid {
  transform: perspective(1800px) translateZ(-178px) rotateX(-11deg) scale(0.88);
}

.promo-page.flip-backward .promo-panel.is-active .panel-grid {
  transform: perspective(1800px) translateZ(130px) rotateX(4deg) scale(1.065);
}

.promo-page.flip-forward .promo-panel.is-active .panel-copy {
  transform: translate3d(0, 0, 132px) scale(1.024);
}

.promo-page.flip-forward .promo-panel.is-active .panel-visual {
  transform: translate3d(0, 0, 24px) scale(0.978);
}

.promo-page.flip-backward .promo-panel.is-active .panel-copy {
  transform: translate3d(0, 0, 124px) scale(1.02);
}

.promo-page.flip-backward .promo-panel.is-active .panel-visual {
  transform: translate3d(0, 0, 22px) scale(0.98);
}

.promo-page.is-routing {
  filter: saturate(1.18) contrast(1.08);
}

.promo-page.is-routing .promo-nav,
.promo-page.is-routing .promo-dots,
.promo-page.is-routing .flip-hint {
  opacity: 0;
  transform: translateY(-16px);
  transition: opacity 0.42s ease, transform 0.42s ease;
}

.promo-page.is-routing .promo-shell {
  transform: perspective(2400px) translateZ(40px) scale(1.1);
  filter: blur(8px) saturate(1.16);
}

.promo-page.is-routing .promo-stack {
  transform: perspective(2000px) translateZ(120px) rotateX(5deg) scale(1.05);
}

.promo-page.is-routing .promo-panel.is-active {
  animation: routePanelBurst 0.72s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-page.is-routing .panel-copy h1,
.promo-page.is-routing .panel-copy h2,
.promo-page.is-routing .hero-actions {
  animation: gatherCenter 0.72s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-page.is-routing .panel-copy h2 {
  animation-delay: 0.06s;
}

.promo-page.is-routing .hero-actions {
  animation-delay: 0.12s;
}

.promo-page.is-routing .panel-visual {
  animation: visualBurst 0.72s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-page.is-routing .route-burst {
  opacity: 1;
}

.promo-page.is-routing .burst-core {
  animation: burstCore 0.72s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-page.is-routing .burst-ring-a {
  animation: burstRing 0.72s 0.02s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.promo-page.is-routing .burst-ring-b {
  animation: burstRing 0.72s 0.08s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

@keyframes slowPulse {
  0%,
  100% {
    transform: scale(0.94);
    opacity: 0.75;
  }
  50% {
    transform: scale(1.08);
    opacity: 1;
  }
}

@keyframes visualFloat {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(0, -16px, 0) scale(1.015);
  }
}

@keyframes sceneFloat {
  0%,
  100% {
    transform: translate3d(0, 0, 0) rotate(0deg);
  }
  50% {
    transform: translate3d(0, -10px, 0) rotate(1.2deg);
  }
}

@keyframes sceneDrift {
  0%,
  100% {
    transform: translate3d(0, 0, 0);
  }
  50% {
    transform: translate3d(10px, -6px, 0);
  }
}

@keyframes dotTrail {
  0%,
  100% {
    opacity: 0.38;
    transform: scale(0.72);
  }
  50% {
    opacity: 1;
    transform: scale(1.12);
  }
}

@keyframes scanSweep {
  0%,
  100% {
    transform: translateY(-24px);
    opacity: 0.18;
  }
  50% {
    transform: translateY(28px);
    opacity: 0.72;
  }
}

@keyframes routePulse {
  0%,
  100% {
    opacity: 0.34;
    filter: brightness(0.9);
  }
  50% {
    opacity: 1;
    filter: brightness(1.24);
  }
}

@keyframes streamGlow {
  0%,
  100% {
    opacity: 0.42;
    transform: translateX(0);
  }
  50% {
    opacity: 1;
    transform: translateX(10px);
  }
}

@keyframes shieldBeam {
  0%,
  100% {
    opacity: 0.28;
    transform: scaleX(0.92) rotate(var(--beam-rotate, 0deg));
  }
  50% {
    opacity: 1;
    transform: scaleX(1.06) rotate(var(--beam-rotate, 0deg));
  }
}

@keyframes floatBadge {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes streakDrift {
  0%,
  100% {
    opacity: 0.45;
    transform: translateX(0) rotate(-18deg);
  }
  50% {
    opacity: 1;
    transform: translateX(12px) rotate(-14deg);
  }
}

@keyframes rotateRing {
  to {
    transform: rotate(1turn);
  }
}

@keyframes panelParticle {
  from {
    opacity: 0;
    transform: translate3d(0, 32px, 0) scale(0.5);
  }
  22%,
  76% {
    opacity: 1;
  }
  to {
    opacity: 0;
    transform: translate3d(84px, -180px, 0) scale(1.4);
  }
}

@keyframes hintBounce {
  50% {
    transform: rotate(45deg) translate(4px, 4px);
  }
}

@keyframes screenJolt {
  0% {
    transform: perspective(2200px) translate3d(0, 0, 0) scale(1.018);
  }
  24% {
    transform: perspective(2200px) translate3d(-6px, 2px, 0) scale(1.064);
  }
  48% {
    transform: perspective(2200px) translate3d(6px, -2px, 0) scale(1.052);
  }
  72% {
    transform: perspective(2200px) translate3d(-2px, 1px, 0) scale(1.058);
  }
  100% {
    transform: perspective(2200px) translate3d(0, 0, 0) scale(1.06);
  }
}

@keyframes panelEnterFromBottom {
  from {
    opacity: 0.35;
    transform: translate3d(0, 120%, -260px) rotateX(-28deg) scale(0.88);
    filter: blur(14px);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0) rotateX(0) scale(1);
    filter: blur(0);
  }
}

@keyframes panelEnterFromTop {
  from {
    opacity: 0.35;
    transform: translate3d(0, -120%, -260px) rotateX(28deg) scale(0.88);
    filter: blur(14px);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0) rotateX(0) scale(1);
    filter: blur(0);
  }
}

@keyframes panelLeaveToTop {
  from {
    opacity: 1;
    transform: translate3d(0, 0, 0) rotateX(0) scale(1);
    filter: blur(0);
  }
  to {
    opacity: 0;
    transform: translate3d(0, -116%, -240px) rotateX(24deg) scale(0.86);
    filter: blur(12px);
  }
}

@keyframes panelLeaveToBottom {
  from {
    opacity: 1;
    transform: translate3d(0, 0, 0) rotateX(0) scale(1);
    filter: blur(0);
  }
  to {
    opacity: 0;
    transform: translate3d(0, 116%, -240px) rotateX(-24deg) scale(0.86);
    filter: blur(12px);
  }
}

@keyframes gatherLeft {
  from {
    opacity: 0;
    filter: blur(12px);
    transform: translate3d(-140px, 26px, 0) scale(0.88) rotate(-5deg);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) scale(1) rotate(0);
  }
}

@keyframes gatherRight {
  from {
    opacity: 0;
    filter: blur(12px);
    transform: translate3d(140px, 20px, 0) scale(0.88) rotate(5deg);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) scale(1) rotate(0);
  }
}

@keyframes gatherTop {
  from {
    opacity: 0;
    filter: blur(14px);
    transform: translate3d(0, -120px, 0) scale(0.84) rotateX(18deg);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) scale(1) rotateX(0);
  }
}

@keyframes gatherBottom {
  from {
    opacity: 0;
    filter: blur(14px);
    transform: translate3d(0, 120px, 0) scale(0.84) rotateX(-18deg);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) scale(1) rotateX(0);
  }
}

@keyframes gatherGrid {
  from {
    opacity: 0;
    filter: blur(14px);
    transform: translate3d(90px, -90px, 0) rotate(-18deg) scale(0.76);
  }
  to {
    opacity: 0.55;
    filter: blur(0);
    transform: translate3d(0, 0, 0) rotate(-8deg) scale(1);
  }
}

@keyframes gatherCornerA {
  from {
    opacity: 0;
    filter: blur(12px);
    transform: translate3d(-190px, -150px, 0) rotate(-18deg) scale(0.64);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) rotate(0) scale(1);
  }
}

@keyframes gatherCornerB {
  from {
    opacity: 0;
    filter: blur(12px);
    transform: translate3d(190px, -150px, 0) rotate(18deg) scale(0.64);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) rotate(0) scale(1);
  }
}

@keyframes gatherCornerC {
  from {
    opacity: 0;
    filter: blur(12px);
    transform: translate3d(-190px, 150px, 0) rotate(16deg) scale(0.64);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) rotate(0) scale(1);
  }
}

@keyframes gatherCornerD {
  from {
    opacity: 0;
    filter: blur(12px);
    transform: translate3d(190px, 150px, 0) rotate(-16deg) scale(0.64);
  }
  to {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) rotate(0) scale(1);
  }
}

@keyframes gatherCenter {
  from {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) scale(1);
  }
  to {
    opacity: 0;
    filter: blur(16px);
    transform: translate3d(0, 0, 0) scale(0.7);
  }
}

@keyframes routePanelBurst {
  from {
    opacity: 1;
    transform: translate3d(0, 0, 0) rotateX(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translate3d(0, -3%, 0) rotateX(12deg) scale(1.16);
  }
}

@keyframes visualBurst {
  from {
    opacity: 1;
    filter: blur(0);
    transform: translate3d(0, 0, 0) scale(1);
  }
  to {
    opacity: 0;
    filter: blur(14px);
    transform: translate3d(0, 0, 0) scale(1.18);
  }
}

@keyframes shardCollapse {
  from {
    opacity: 0.88;
    filter: blur(0);
    transform: translate3d(0, 0, 0) scale(1);
  }
  to {
    opacity: 0;
    filter: blur(12px);
    transform: translate3d(0, 0, 0) scale(0.42);
  }
}

@keyframes burstCore {
  from {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.18);
  }
  40% {
    opacity: 1;
  }
  to {
    opacity: 0;
    transform: translate(-50%, -50%) scale(6.8);
  }
}

@keyframes burstRing {
  from {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.16);
  }
  28% {
    opacity: 0.8;
  }
  to {
    opacity: 0;
    transform: translate(-50%, -50%) scale(3.8);
  }
}

@media (max-width: 1024px) {
  .panel-grid {
    grid-template-columns: 1fr;
    height: auto;
    padding-top: 18px;
  }

  .panel-copy h1 {
    font-size: 4.6rem;
  }

  .panel-visual {
    min-height: 460px;
  }

  .promo-dots {
    right: 14px;
  }

  .burst-ring-a,
  .burst-ring-b,
  .burst-core {
    min-width: 0;
    min-height: 0;
  }
}

@media (max-width: 640px) {
  .promo-nav {
    top: 12px;
    width: calc(100% - 20px);
  }

  .promo-nav,
  .promo-nav-actions {
    align-items: flex-start;
    flex-direction: column;
  }

  .promo-panel {
    padding: 126px 14px 78px;
  }

  .panel-copy h1 {
    font-size: 3.4rem;
  }

  .panel-copy h2 {
    font-size: 1.6rem;
  }

  .panel-visual {
    min-height: 360px;
  }

  .scene-core,
  .stat-card-top,
  .stat-card-bottom {
    position: relative;
    top: auto;
    right: auto;
    bottom: auto;
    left: auto;
    width: 100%;
    margin-top: 14px;
  }

  .promo-dots {
    display: none;
  }

  .flip-hint {
    bottom: 16px;
  }
}
</style>
