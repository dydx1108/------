<template>
  <AppShell title="管理首页" subtitle="管理员端 · 总览与快捷处理" role="admin">
    <section class="command-deck">
      <article class="glass-card command-hero tilt-card">
        <div class="command-hero__content">
          <p class="command-hero__eyebrow">校园指挥舱</p>
          <h2>今日平台态势</h2>
          <strong><CountUpNumber :value="totalItems" /></strong>
          <span class="command-hero__meta">
            已发布 {{ publishedCount }} 条 · 待审核 {{ pendingCount }} 条 · 已认领 {{ claimedCount }} 条
          </span>
        </div>

        <div class="command-hero__rings" aria-hidden="true">
          <span class="command-ring command-ring--outer"></span>
          <span class="command-ring command-ring--mid"></span>
          <span class="command-ring command-ring--inner"></span>
          <span class="command-core"><CountUpNumber :value="recoveryRate" suffix="%" /></span>
        </div>
      </article>

      <article class="glass-card alert-card alert-card--priority reveal-card tilt-card">
        <div class="alert-card__glow" aria-hidden="true"></div>
        <p>重点提醒</p>
        <strong><CountUpNumber :value="pendingCount" /></strong>
        <h3>待审核物品</h3>
        <span>优先处理新提交内容，保证平台信息准确。</span>
        <RouterLink class="small-button" to="/admin/audit">进入审核中心</RouterLink>
      </article>

      <article
        v-for="metric in spotlightMetrics"
        :key="metric.label"
        class="glass-card spotlight-card reveal-card tilt-card"
      >
        <div class="spotlight-card__top">
          <span>{{ metric.label }}</span>
          <i :class="['spotlight-card__badge', `is-${metric.tone}`]">{{ metric.tag }}</i>
        </div>
        <strong><CountUpNumber :value="metric.value" /></strong>
        <p>{{ metric.tip }}</p>
        <div class="spotlight-card__track">
          <i :style="{ width: `${metric.progress}%` }"></i>
        </div>
      </article>
    </section>

    <section class="ops-grid">
      <article class="glass-card ops-panel reveal-card tilt-card">
        <div class="ops-panel__header">
          <div>
            <p>任务面板</p>
            <h2>待处理事项</h2>
          </div>
          <strong><CountUpNumber :value="urgentTasks" /></strong>
        </div>

        <ul class="ops-list">
          <li>
            <span class="ops-list__dot is-pending"></span>
            <div>
              <h3>待审核物品</h3>
              <p><CountUpNumber :value="pendingCount" /> 条正在等待管理员确认。</p>
            </div>
          </li>
          <li>
            <span class="ops-list__dot is-claim"></span>
            <div>
              <h3>待审核认领</h3>
              <p><CountUpNumber :value="pendingClaimCount" /> 条认领申请需要跟进。</p>
            </div>
          </li>
          <li>
            <span class="ops-list__dot is-published"></span>
            <div>
              <h3>当前已发布</h3>
              <p><CountUpNumber :value="publishedCount" /> 条内容已进入广场展示。</p>
            </div>
          </li>
        </ul>

        <RouterLink class="small-button" to="/admin/audit">立即处理</RouterLink>
      </article>

      <article class="glass-card ops-panel ops-panel--insight reveal-card tilt-card">
        <div class="ops-panel__header">
          <div>
            <p>运营建议</p>
            <h2>平台热区观察</h2>
          </div>
          <strong><CountUpNumber :value="hottestCategory.total" /></strong>
        </div>

        <div class="insight-hero">
          <div>
            <span>当前最热分类</span>
            <h3>{{ hottestCategory.name }}</h3>
            <p>建议优先优化该类物品的检索词与展示入口，提高找回效率。</p>
          </div>
          <div class="insight-pulse" aria-hidden="true">
            <i></i>
            <i></i>
            <i></i>
          </div>
        </div>

        <div class="insight-tags">
          <span>找回率 <CountUpNumber :value="recoveryRate" suffix="%" /></span>
          <span>总用户 <CountUpNumber :value="totalUsers" /></span>
          <span>管理员 <CountUpNumber :value="adminCount" /></span>
        </div>

        <RouterLink class="small-button" to="/admin/stats">查看数据统计</RouterLink>
      </article>
    </section>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import CountUpNumber from '../../components/CountUpNumber.vue';
import AppShell from '../../components/AppShell.vue';
import { api } from '../../services/api';

const stats = ref({
  itemStats: [],
  userStats: [],
  claimStats: [],
  categoryStats: []
});

function countBy(list, key, value) {
  return Number(list.find((item) => item[key] === value)?.total || 0);
}

const totalItems = computed(() =>
  stats.value.itemStats.reduce((sum, item) => sum + Number(item.total), 0)
);

const totalUsers = computed(() =>
  stats.value.userStats.reduce((sum, item) => sum + Number(item.total), 0)
);

const pendingCount = computed(() => countBy(stats.value.itemStats, 'status', 'pending'));
const publishedCount = computed(() => countBy(stats.value.itemStats, 'status', 'published'));
const claimedCount = computed(() => countBy(stats.value.itemStats, 'status', 'claimed'));
const adminCount = computed(() => countBy(stats.value.userStats, 'role', 'admin'));
const studentCount = computed(() => countBy(stats.value.userStats, 'role', 'student'));
const pendingClaimCount = computed(() => countBy(stats.value.claimStats, 'status', 'pending'));
const urgentTasks = computed(() => pendingCount.value + pendingClaimCount.value);

const recoveryRate = computed(() => {
  if (!totalItems.value) {
    return 0;
  }
  return Math.round((claimedCount.value / totalItems.value) * 100);
});

const hottestCategory = computed(() => {
  const [first] = stats.value.categoryStats || [];
  return {
    name: first?.name || '暂无分类热度',
    total: Number(first?.total || 0)
  };
});

const spotlightMetrics = computed(() => {
  const itemsBase = Math.max(totalItems.value, 1);
  const usersBase = Math.max(totalUsers.value, 1);

  return [
    {
      label: '已认领效率',
      value: claimedCount.value,
      tip: '已完成找回的物品数量',
      progress: Math.max(10, Math.round((claimedCount.value / itemsBase) * 100)),
      tone: 'gold',
      tag: `${recoveryRate.value}%`
    },
    {
      label: '学生活跃',
      value: studentCount.value,
      tip: '当前注册学生账号规模',
      progress: Math.max(10, Math.round((studentCount.value / usersBase) * 100)),
      tone: 'green',
      tag: '在线池'
    },
    {
      label: '平台管理员',
      value: adminCount.value,
      tip: '后台管理与审核角色数量',
      progress: Math.max(10, Math.round((adminCount.value / usersBase) * 100)),
      tone: 'blue',
      tag: '调度席'
    }
  ];
});

onMounted(async () => {
  const result = await api.get('/stats');
  stats.value = result.data;
});
</script>
