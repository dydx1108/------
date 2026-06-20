<template>
  <AppShell title="物品管理" subtitle="管理员端 · 查询、编辑与删除" role="admin">
    <div class="toolbar glass-card">
      <input v-model="keyword" placeholder="搜索物品、地点或描述" @keyup.enter="loadItems" />
      <select v-model="status" @change="loadItems">
        <option value="">全部状态</option>
        <option value="pending">待审核</option>
        <option value="published">已发布</option>
        <option value="claimed">已认领</option>
        <option value="closed">已关闭</option>
        <option value="rejected">已驳回</option>
      </select>
      <button class="small-button" type="button" @click="loadItems">查询</button>
    </div>

    <DataTable :columns="columns" :rows="items">
      <template #type="{ row }">{{ typeMap[row.type] }}</template>
      <template #status="{ row }">{{ statusMap[row.status] }}</template>
      <template #event_date="{ row }">{{ formatDate(row.event_date) }}</template>
      <template #actions="{ row }">
        <select class="mini-select" :value="row.status" @change="changeStatus(row.id, $event.target.value)">
          <option value="pending">待审核</option>
          <option value="published">已发布</option>
          <option value="claimed">已认领</option>
          <option value="closed">已关闭</option>
          <option value="rejected">已驳回</option>
        </select>
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
const keyword = ref('');
const status = ref('');
const columns = [
  { key: 'title', label: '标题' },
  { key: 'type', label: '类型' },
  { key: 'category_name', label: '分类' },
  { key: 'location', label: '地点' },
  { key: 'event_date', label: '日期' },
  { key: 'status', label: '状态' }
];

async function loadItems() {
  const result = await api.get('/items', { params: { keyword: keyword.value, status: status.value } });
  items.value = result.data;
}

async function changeStatus(id, nextStatus) {
  await api.patch(`/items/${id}/status`, { status: nextStatus });
  await loadItems();
}

async function removeItem(id) {
  if (!confirm('确认删除该物品吗？')) return;
  await api.delete(`/items/${id}`);
  await loadItems();
}

onMounted(loadItems);
</script>
