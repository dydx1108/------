export const statusMap = {
  pending: '待审核',
  published: '已发布',
  claimed: '已认领',
  closed: '已关闭',
  rejected: '已驳回',
  approved: '已通过'
};

export const typeMap = {
  lost: '失物',
  found: '拾物'
};

export function formatDate(value) {
  if (!value) return '-';
  return String(value).slice(0, 10);
}

export function pickHeroImage(items = []) {
  return items.find((item) => item.image_url)?.image_url || '/generated/hero-campus.svg';
}
