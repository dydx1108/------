<template>
  <AppShell title="我的发布" subtitle="学生端 · 管理自己提交的信息" role="student">
    <DataTable :columns="columns" :rows="items">
      <template #type="{ row }">{{ typeMap[row.type] }}</template>
      <template #status="{ row }">{{ statusMap[row.status] }}</template>
      <template #event_date="{ row }">{{ formatDate(row.event_date) }}</template>
      <template #actions="{ row }">
        <button class="danger-link" type="button" @click="removeItem(row.id)">删除</button>
      </template>
    </DataTable>
  </AppShell>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import AppShell from '../../components/AppShell.vue';
import DataTable from '../../components/DataTable.vue';
import { api } from '../../services/api';
import { formatDate, statusMap, typeMap } from '../../utils/format';

const items = ref([]);
const columns = [
  { key: 'title', label: '标题' },
  { key: 'type', label: '类型' },
  { key: 'location', label: '地点' },
  { key: 'event_date', label: '日期' },
  { key: 'status', label: '状态' }
];

async function loadItems() {
  const result = await api.get('/items', { params: { mine: true } });
  items.value = result.data;
}

async function removeItem(id) {
  if (!confirm('确认删除这条发布信息吗？')) return;
  await api.delete(`/items/${id}`);
  await loadItems();
}

onMounted(loadItems);
</script>
