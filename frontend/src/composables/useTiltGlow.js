import { onBeforeUnmount, onMounted } from 'vue';

const DEFAULT_SELECTOR = [
  '.glass-card',
  '.item-card',
  '.message-card',
  '.detail-card',
  '.toolbar',
  '.inline-form',
  '.table-wrap',
  '.status-summary article',
  '.heat-row'
].join(', ');

const RIPPLE_SELECTOR = [
  'button',
  '.small-button',
  '.submit-button',
  '.animated-button',
  '.item-card',
  '.glass-card',
  '.detail-card',
  '.message-card'
].join(', ');

export function useTiltGlow(selector = DEFAULT_SELECTOR, rippleSelector = RIPPLE_SELECTOR) {
  const cardDisposers = new WeakMap();
  const rippleDisposers = new WeakMap();
  const settleTimers = new WeakMap();
  let observer = null;

  function createRipple(target, event) {
    const rect = target.getBoundingClientRect();
    const ripple = document.createElement('span');
    const size = Math.max(rect.width, rect.height) * 0.72;

    ripple.className = 'energy-ripple';
    ripple.style.width = `${size}px`;
    ripple.style.height = `${size}px`;
    ripple.style.left = `${event.clientX - rect.left - size / 2}px`;
    ripple.style.top = `${event.clientY - rect.top - size / 2}px`;

    target.appendChild(ripple);
    ripple.addEventListener('animationend', () => ripple.remove(), { once: true });
  }

  function bindRipple(target) {
    if (rippleDisposers.has(target)) {
      return;
    }

    const handlePointerDown = (event) => createRipple(target, event);
    target.addEventListener('pointerdown', handlePointerDown);

    rippleDisposers.set(target, () => {
      target.removeEventListener('pointerdown', handlePointerDown);
      rippleDisposers.delete(target);
    });
  }

  function bindCard(card) {
    if (cardDisposers.has(card)) {
      return;
    }

    card.classList.add('tilt-card');

    const handleMove = (event) => {
      const rect = card.getBoundingClientRect();
      const offsetX = event.clientX - rect.left;
      const offsetY = event.clientY - rect.top;
      const percentX = offsetX / rect.width;
      const percentY = offsetY / rect.height;
      const rotateY = (percentX - 0.5) * 18;
      const rotateX = (0.5 - percentY) * 18;

      card.style.setProperty('--mx', `${offsetX}px`);
      card.style.setProperty('--my', `${offsetY}px`);
      card.style.setProperty('--rx', `${rotateX}deg`);
      card.style.setProperty('--ry', `${rotateY}deg`);
      card.style.setProperty('--tx', `${(percentX - 0.5) * 20}px`);
      card.style.setProperty('--ty', `${(percentY - 0.5) * 20}px`);
      card.classList.add('is-tilting');
    };

    const handleDown = () => {
      card.classList.add('is-pressing');
    };

    const handleUp = () => {
      card.classList.remove('is-pressing');
    };

    const reset = () => {
      card.style.setProperty('--rx', '0deg');
      card.style.setProperty('--ry', '0deg');
      card.style.setProperty('--tx', '0px');
      card.style.setProperty('--ty', '0px');
      card.classList.remove('is-tilting');
      card.classList.remove('is-pressing');
    };

    card.addEventListener('pointermove', handleMove);
    card.addEventListener('pointerleave', reset);
    card.addEventListener('pointerdown', handleDown);
    card.addEventListener('pointerup', handleUp);
    card.addEventListener('pointercancel', reset);

    const settleTimer = window.setTimeout(() => {
      card.classList.add('motion-settled');
      settleTimers.delete(card);
    }, 1300);
    settleTimers.set(card, settleTimer);

    cardDisposers.set(card, () => {
      card.removeEventListener('pointermove', handleMove);
      card.removeEventListener('pointerleave', reset);
      card.removeEventListener('pointerdown', handleDown);
      card.removeEventListener('pointerup', handleUp);
      card.removeEventListener('pointercancel', reset);
      window.clearTimeout(settleTimers.get(card));
      settleTimers.delete(card);
      cardDisposers.delete(card);
    });
  }

  function bindMatches(root = document) {
    root.querySelectorAll?.(selector).forEach(bindCard);
    root.querySelectorAll?.(rippleSelector).forEach(bindRipple);
    if (root.matches?.(selector)) bindCard(root);
    if (root.matches?.(rippleSelector)) bindRipple(root);
  }

  function bindAll() {
    bindMatches(document);
  }

  onMounted(() => {
    bindAll();

    observer = new MutationObserver((mutations) => {
      for (const mutation of mutations) {
        mutation.addedNodes.forEach((node) => {
          if (node.nodeType === 1) {
            bindMatches(node);
          }
        });
      }
    });

    observer.observe(document.body, { childList: true, subtree: true });
  });

  onBeforeUnmount(() => {
    observer?.disconnect();
    observer = null;
    document.querySelectorAll(selector).forEach((node) => cardDisposers.get(node)?.());
    document.querySelectorAll(rippleSelector).forEach((node) => rippleDisposers.get(node)?.());
  });

  return { bindAll };
}
