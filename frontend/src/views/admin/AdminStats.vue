<template>
  <AppShell title="数据统计" subtitle="管理员端 · 平台运营看板" role="admin">
    <section class="stats-command">
      <article class="glass-card stats-overview reveal-card tilt-card">
        <div class="stats-overview__header">
          <div>
            <p>状态总览</p>
            <h2>物品状态雷达</h2>
          </div>
          <strong><CountUpNumber :value="totalItems" /></strong>
        </div>

        <div class="status-stack">
          <div
            v-for="item in statusItems"
            :key="item.key"
            class="status-lane"
          >
            <div class="status-lane__meta">
              <span>{{ item.label }}</span>
              <strong><CountUpNumber :value="item.total" /></strong>
            </div>
            <div class="status-lane__track">
              <i :class="['status-lane__bar', `is-${item.tone}`]" :style="{ width: `${item.width}%` }"></i>
            </div>
          </div>
        </div>

        <div class="status-summary">
          <article class="tilt-card">
            <span>已发布占比</span>
            <strong><CountUpNumber :value="publishedRate" suffix="%" /></strong>
          </article>
          <article class="tilt-card">
            <span>认领完成率</span>
            <strong><CountUpNumber :value="claimRate" suffix="%" /></strong>
          </article>
          <article class="tilt-card">
            <span>待处理压力</span>
            <strong><CountUpNumber :value="pendingRate" suffix="%" /></strong>
          </article>
        </div>
      </article>

      <article class="glass-card heat-card reveal-card tilt-card">
        <div class="stats-overview__header">
          <div>
            <p>分类排行</p>
            <h2>分类热度榜</h2>
          </div>
          <strong><CountUpNumber :value="hottestCategory.total" /></strong>
        </div>

        <div class="heat-list">
          <div
            v-for="(item, index) in rankedCategories"
            :key="item.name"
            class="heat-row tilt-card"
          >
            <div class="heat-row__rank">{{ String(index + 1).padStart(2, '0') }}</div>
            <div class="heat-row__body">
              <div class="heat-row__title">
                <span>{{ item.name }}</span>
                <strong><CountUpNumber :value="item.total" /></strong>
              </div>
              <div class="heat-row__track">
                <i :style="{ width: `${item.width}%` }"></i>
              </div>
            </div>
          </div>
        </div>
      </article>
    </section>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue';
import CountUpNumber from '../../components/CountUpNumber.vue';
import AppShell from '../../components/AppShell.vue';
import { api } from '../../services/api';
import { statusMap } from '../../utils/format';

const stats = reactive({
  itemStats: [],
  categoryStats: []
});

function totalByStatus(status) {
  return Number(stats.itemStats.find((item) => item.status === status)?.total || 0);
}

const totalItems = computed(() =>
  stats.itemStats.reduce((sum, item) => sum + Number(item.total), 0)
);

const categoryMax = computed(() =>
  Math.max(1, ...stats.categoryStats.map((item) => Number(item.total || 0)))
);

const statusMax = computed(() =>
  Math.max(1, ...stats.itemStats.map((item) => Number(item.total || 0)))
);

const statusItems = computed(() =>
  ['published', 'claimed', 'pending', 'closed', 'rejected']
    .map((key) => {
      const total = totalByStatus(key);
      return {
        key,
        label: statusMap[key] || key,
        total,
        width: Math.max(total ? 12 : 8, Math.round((total / statusMax.value) * 100)),
        tone:
          key === 'published'
            ? 'green'
            : key === 'claimed'
              ? 'gold'
              : key === 'pending'
                ? 'blue'
                : 'muted'
      };
    })
    .filter((item) => item.total > 0)
);

const rankedCategories = computed(() =>
  stats.categoryStats.slice(0, 6).map((item) => ({
    ...item,
    total: Number(item.total || 0),
    width: Math.max(item.total ? 12 : 8, Math.round((Number(item.total || 0) / categoryMax.value) * 100))
  }))
);

const hottestCategory = computed(() => ({
  name: rankedCategories.value[0]?.name || '暂无分类',
  total: rankedCategories.value[0]?.total || 0
}));

const publishedRate = computed(() => {
  if (!totalItems.value) return 0;
  return Math.round((totalByStatus('published') / totalItems.value) * 100);
});

const claimRate = computed(() => {
  if (!totalItems.value) return 0;
  return Math.round((totalByStatus('claimed') / totalItems.value) * 100);
});

const pendingRate = computed(() => {
  if (!totalItems.value) return 0;
  return Math.round((totalByStatus('pending') / totalItems.value) * 100);
});

onMounted(async () => {
  const result = await api.get('/stats');
  Object.assign(stats, result.data);
});
</script>
