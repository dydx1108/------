<template>
  <article class="item-card reveal-card">
    <div class="item-image-wrap card-layer card-layer--back">
      <img :src="item.image_url || '/generated/hero-campus.svg'" :alt="item.title" loading="lazy" />
      <span class="type-badge card-layer card-layer--front" :class="item.type">{{ typeMap[item.type] }}</span>
    </div>
    <div class="item-content">
      <div class="card-title-row card-layer card-layer--front">
        <h3>{{ item.title }}</h3>
        <span class="status-badge">{{ statusMap[item.status] || item.status }}</span>
      </div>
      <p class="card-layer card-layer--mid">{{ item.description }}</p>
      <div class="meta-grid card-layer card-layer--slow">
        <span>地点：{{ item.location }}</span>
        <span>日期：{{ formatDate(item.event_date) }}</span>
        <span>分类：{{ item.category_name }}</span>
      </div>
      <div class="card-layer card-layer--front">
        <slot />
      </div>
    </div>
  </article>
</template>

<script setup>
import { formatDate, statusMap, typeMap } from '../utils/format';

defineProps({
  item: {
    type: Object,
    required: true
  }
});
</script>
