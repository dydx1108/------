export function playPageTransition(push) {
  if (typeof document === 'undefined') {
    push();
    return;
  }

  const root = document.documentElement;
  if (root.classList.contains('page-transitioning')) {
    return;
  }

  root.classList.add('page-transitioning');

  window.setTimeout(() => {
    push();
  }, 520);

  window.setTimeout(() => {
    root.classList.remove('page-transitioning');
  }, 1100);
}
