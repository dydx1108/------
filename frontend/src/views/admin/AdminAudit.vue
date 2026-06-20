<template>
  <AppShell title="审核中心" subtitle="管理员端 · 物品审核与认领审核" role="admin">
    <section class="audit-summary">
      <article class="glass-card audit-metric reveal-card tilt-card">
        <span>待审物品</span>
        <strong>{{ pendingItems.length }}</strong>
        <p>等待管理员确认发布</p>
      </article>
      <article class="glass-card audit-metric reveal-card tilt-card">
        <span>待审认领</span>
        <strong>{{ pendingClaims.length }}</strong>
        <p>需要尽快核对归属</p>
      </article>
      <article class="glass-card audit-metric reveal-card tilt-card">
        <span>本轮已处理</span>
        <strong>{{ processedToday }}</strong>
        <p>当前会话内完成的审核量</p>
      </article>
      <article class="glass-card audit-metric audit-metric--priority reveal-card tilt-card">
        <span>处理策略</span>
        <strong>{{ priorityLabel }}</strong>
        <p>优先清空认领申请，再继续处理新发布物品</p>
      </article>
    </section>

    <section class="audit-console audit-console--queue">
      <article class="glass-card audit-panel audit-panel--primary reveal-card tilt-card">
        <div class="audit-panel__header">
          <div>
            <p>主审核区</p>
            <h2>待审核物品</h2>
          </div>
          <div class="audit-panel__badge">{{ pendingItems.length }} 条</div>
        </div>

        <template v-if="paginatedItems.length">
          <DataTable :columns="itemColumns" :rows="paginatedItems">
            <template #type="{ row }">{{ typeMap[row.type] }}</template>
            <template #actions="{ row }">
              <div class="audit-actions">
                <button class="small-button" type="button" @click="reviewItem(row.id, 'published')">通过</button>
                <button class="danger-link" type="button" @click="reviewItem(row.id, 'rejected')">驳回</button>
              </div>
            </template>
          </DataTable>

          <div class="queue-footer">
            <span>第 {{ itemPage }} / {{ itemPageCount }} 页</span>
            <div class="queue-pagination">
              <button class="ghost-link" type="button" :disabled="itemPage === 1" @click="itemPage -= 1">上一页</button>
              <button class="ghost-link" type="button" :disabled="itemPage === itemPageCount" @click="itemPage += 1">下一页</button>
            </div>
          </div>
        </template>

        <div v-else class="audit-empty">
          <div class="audit-empty__orb"></div>
          <h3>当前没有待审核物品</h3>
          <p>新的失物或拾物发布后，会优先出现在这里。</p>
        </div>
      </article>

      <article class="glass-card audit-panel audit-panel--secondary reveal-card tilt-card">
        <div class="audit-panel__header">
          <div>
            <p>认领队列</p>
            <h2>认领审核</h2>
          </div>
          <div class="audit-panel__badge">{{ pendingClaims.length }} 条</div>
        </div>

        <template v-if="pendingClaims.length">
          <div class="claim-queue-layout">
            <div class="claim-queue-list">
              <button
                v-for="claim in pendingClaims"
                :key="claim.id"
                type="button"
                class="claim-queue-item"
                :class="{ 'is-active': activeClaim?.id === claim.id }"
                @click="activeClaimId = claim.id"
              >
                <div>
                  <span class="claim-review-card__eyebrow">物品</span>
                  <strong>{{ claim.item_title }}</strong>
                </div>
                <p>{{ claim.claimant_name }}</p>
              </button>
            </div>

            <div class="claim-detail-card">
              <template v-if="activeClaim">
                <div class="claim-review-card__top">
                  <div>
                    <span class="claim-review-card__eyebrow">当前申请</span>
                    <h3>{{ activeClaim.item_title }}</h3>
                  </div>
                  <span class="claim-status">{{ statusMap[activeClaim.status] }}</span>
                </div>

                <div class="claim-review-card__grid">
                  <div>
                    <span class="claim-review-card__label">申请人</span>
                    <strong>{{ activeClaim.claimant_name }}</strong>
                  </div>
                  <div>
                    <span class="claim-review-card__label">联系电话</span>
                    <strong>{{ activeClaim.claimant_phone || '未填写' }}</strong>
                  </div>
                  <div class="claim-review-card__evidence">
                    <span class="claim-review-card__label">说明摘要</span>
                    <p>{{ activeClaim.evidence }}</p>
                  </div>
                </div>

                <div class="claim-review-card__actions claim-review-card__actions--sticky">
                  <button class="small-button" type="button" @click="reviewClaim(activeClaim.id, 'approved')">通过</button>
                  <button class="danger-link" type="button" @click="reviewClaim(activeClaim.id, 'rejected')">驳回</button>
                </div>
              </template>
            </div>
          </div>
        </template>

        <div v-else class="audit-empty audit-empty--compact">
          <div class="audit-empty__orb"></div>
          <h3>暂无待审核认领</h3>
          <p>新的认领申请出现后，会优先显示在这里。</p>
        </div>
      </article>
    </section>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import AppShell from '../../components/AppShell.vue';
import DataTable from '../../components/DataTable.vue';
import { api } from '../../services/api';
import { statusMap, typeMap } from '../../utils/format';

const pendingItems = ref([]);
const claims = ref([]);
const processedToday = ref(0);
const itemPage = ref(1);
const pageSize = 6;
const activeClaimId = ref(null);

const itemColumns = [
  { key: 'title', label: '标题' },
  { key: 'type', label: '类型' },
  { key: 'location', label: '地点' },
  { key: 'publisher_name', label: '发布人' }
];

const pendingClaims = computed(() => claims.value.filter((claim) => claim.status === 'pending'));
const itemPageCount = computed(() => Math.max(1, Math.ceil(pendingItems.value.length / pageSize)));
const paginatedItems = computed(() => {
  const start = (itemPage.value - 1) * pageSize;
  return pendingItems.value.slice(start, start + pageSize);
});

const activeClaim = computed(() =>
  pendingClaims.value.find((claim) => claim.id === activeClaimId.value) || pendingClaims.value[0] || null
);

const priorityLabel = computed(() => {
  if (pendingClaims.value.length > 0) return '高';
  if (pendingItems.value.length > 0) return '中';
  return '低';
});

watch(pendingClaims, (nextClaims) => {
  if (!nextClaims.length) {
    activeClaimId.value = null;
    return;
  }
  if (!nextClaims.some((claim) => claim.id === activeClaimId.value)) {
    activeClaimId.value = nextClaims[0].id;
  }
});

watch(itemPageCount, (count) => {
  if (itemPage.value > count) {
    itemPage.value = count;
  }
});

async function loadAuditData() {
  const [itemsResult, claimsResult] = await Promise.all([
    api.get('/items', { params: { status: 'pending' } }),
    api.get('/claims')
  ]);
  pendingItems.value = itemsResult.data;
  claims.value = claimsResult.data;
}

async function reviewItem(id, status) {
  await api.patch(`/items/${id}/status`, { status });
  processedToday.value += 1;
  await loadAuditData();
}

async function reviewClaim(id, status) {
  await api.patch(`/claims/${id}/review`, {
    status,
    reply: status === 'approved' ? '审核通过，请联系发布人领取。' : '凭证不足，请补充物品细节。'
  });
  processedToday.value += 1;
  await loadAuditData();
}

onMounted(loadAuditData);
</script>
