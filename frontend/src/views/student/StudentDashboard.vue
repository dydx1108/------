<template>
  <AppShell :title="pageTitle" :subtitle="pageSubtitle" role="student">
    <section class="student-board-hero" :class="heroClass">
      <article class="glass-card board-hero-card reveal-card tilt-card">
        <div class="board-hero-copy">
          <p class="board-eyebrow">{{ boardEyebrow }}</p>
          <h2>{{ boardHeadline }}</h2>
          <span>{{ boardDesc }}</span>
        </div>
        <div class="board-hero-metrics">
          <strong><CountUpNumber :value="items.length" /></strong>
          <p>{{ boardMetricText }}</p>
        </div>
      </article>

      <article
        v-for="metric in metrics"
        :key="metric.label"
        class="glass-card student-stat-card reveal-card tilt-card"
      >
        <span>{{ metric.label }}</span>
        <strong><CountUpNumber :value="metric.value" /></strong>
        <p>{{ metric.tip }}</p>
      </article>
    </section>

    <div class="toolbar glass-card board-toolbar tilt-card">
      <input v-model="keyword" :placeholder="searchPlaceholder" @keyup.enter="loadItems" />
      <select v-model="status" @change="loadItems">
        <option value="">全部状态</option>
        <option value="published">已发布</option>
        <option value="claimed">已认领</option>
      </select>
      <button class="small-button" type="button" @click="loadItems">查询</button>
    </div>

    <div v-if="boardType === 'found'" class="found-board-grid">
      <article
        v-for="item in items"
        :key="item.id"
        class="item-card found-card reveal-card"
      >
        <div class="item-image-wrap card-layer card-layer--back">
          <img :src="item.image_url" :alt="item.title" loading="lazy" />
          <span class="type-badge found card-layer card-layer--front">{{ typeMap[item.type] }}</span>
          <span class="found-card__hint card-layer card-layer--mid">实拍物品</span>
        </div>
        <div class="item-content found-card__content">
          <div class="card-title-row card-layer card-layer--front">
            <h3>{{ item.title }}</h3>
            <span class="status-badge">{{ statusMap[item.status] || item.status }}</span>
          </div>
          <p class="found-card__lead card-layer card-layer--mid">{{ item.description }}</p>
          <div class="meta-grid card-layer card-layer--slow">
            <span>拾到地点：{{ item.location }}</span>
            <span>拾到时间：{{ formatDate(item.event_date) }}</span>
            <span>发布人：{{ item.publisher_name || item.contact_name }}</span>
            <span>联系电话：{{ item.contact_phone }}</span>
          </div>
          <div class="card-actions card-layer card-layer--front">
            <RouterLink class="small-button" :to="`/student/items/${item.id}`">查看详情</RouterLink>
          </div>
        </div>
      </article>
    </div>

    <div v-else class="lost-board-list">
      <article
        v-for="item in items"
        :key="item.id"
        class="item-card lost-card reveal-card"
      >
        <div class="lost-card__serial card-layer card-layer--back">
          <span>{{ String(item.id).padStart(2, '0') }}</span>
        </div>
        <div class="item-content lost-card__content">
          <div class="card-title-row card-layer card-layer--front">
            <div>
              <p class="lost-card__eyebrow">失物寻回公告</p>
              <h3>{{ item.title }}</h3>
            </div>
            <span class="status-badge">{{ statusMap[item.status] || item.status }}</span>
          </div>
          <p class="lost-card__lead card-layer card-layer--mid">{{ item.description }}</p>
          <div class="lost-meta-grid card-layer card-layer--slow">
            <span>丢失地点：{{ item.location }}</span>
            <span>丢失时间：{{ formatDate(item.event_date) }}</span>
            <span>物品分类：{{ item.category_name }}</span>
            <span>发布人：{{ item.publisher_name || item.contact_name }}</span>
            <span>联系电话：{{ item.contact_phone }}</span>
          </div>
          <div class="card-actions card-layer card-layer--front">
            <RouterLink class="small-button" :to="`/student/items/${item.id}`">查看详情</RouterLink>
          </div>
        </div>
      </article>
    </div>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import AppShell from '../../components/AppShell.vue';
import CountUpNumber from '../../components/CountUpNumber.vue';
import { api } from '../../services/api';
import { formatDate, statusMap, typeMap } from '../../utils/format';

const route = useRoute();
const items = ref([]);
const keyword = ref('');
const status = ref('');

const boardType = computed(() => route.meta.boardType || 'lost');
const pageTitle = computed(() => (boardType.value === 'found' ? '拾物招领' : '失物寻回'));
const pageSubtitle = computed(() =>
  boardType.value === 'found' ? '学生端 · 图片展示感招领专区' : '学生端 · 信息公告感寻物专区'
);
const heroClass = computed(() => (boardType.value === 'found' ? 'is-found' : 'is-lost'));
const searchPlaceholder = computed(() =>
  boardType.value === 'found' ? '搜索拾到物品、地点或描述' : '搜索丢失物品、地点或特征'
);

const boardEyebrow = computed(() => (boardType.value === 'found' ? '实拍招领专区' : '校园寻物公告栏'));
const boardHeadline = computed(() =>
  boardType.value === 'found' ? '看图快速核对拾到物品' : '先看清文字特征，再联系发布人'
);
const boardDesc = computed(() =>
  boardType.value === 'found'
    ? '这里集中展示同学拾到并拍照上传的物品，方便失主按图片和细节快速匹配。'
    : '这里专门展示失主发布的寻物信息，不放默认图，重点看地点、时间和物品特征描述。'
);
const boardMetricText = computed(() =>
  boardType.value === 'found' ? '当前拾物招领条目' : '当前失物寻回公告'
);

const publishedCount = computed(() => items.value.filter((item) => item.status === 'published').length);
const claimedCount = computed(() => items.value.filter((item) => item.status === 'claimed').length);
const categoryCount = computed(() => new Set(items.value.map((item) => item.category_name)).size);

const metrics = computed(() =>
  boardType.value === 'found'
    ? [
        { label: '带图物品', value: items.value.filter((item) => item.image_url).length, tip: '全部为实拍招领物品' },
        { label: '已发布', value: publishedCount.value, tip: '正在等待失主核对联系' },
        { label: '已认领', value: claimedCount.value, tip: '已完成交接或核验' }
      ]
    : [
        { label: '文字公告', value: items.value.length, tip: '全部采用纯文字描述' },
        { label: '待帮助', value: publishedCount.value, tip: '已公开展示等待线索' },
        { label: '涉及分类', value: categoryCount.value, tip: '覆盖多类常见失物' }
      ]
);

async function loadItems() {
  const result = await api.get('/items', {
    params: {
      keyword: keyword.value,
      status: status.value,
      type: boardType.value
    }
  });
  items.value = result.data;
}

watch(() => route.fullPath, loadItems);
onMounted(loadItems);
</script>
