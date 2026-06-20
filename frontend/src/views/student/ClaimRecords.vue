<template>
  <AppShell title="认领记录" subtitle="学生端 · 查看审核进度" role="student">
    <DataTable :columns="columns" :rows="claims">
      <template #status="{ row }">{{ statusMap[row.status] }}</template>
      <template #actions="{ row }">
        <button class="danger-link" type="button" @click="removeClaim(row.id)">删除</button>
      </template>
    </DataTable>
  </AppShell>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import AppShell from '../../components/AppShell.vue';
import DataTable from '../../components/DataTable.vue';
import { api } from '../../services/api';
import { statusMap } from '../../utils/format';

const claims = ref([]);
const columns = [
  { key: 'item_title', label: '物品' },
  { key: 'evidence', label: '认领说明' },
  { key: 'status', label: '状态' },
  { key: 'reply', label: '管理员回复' }
];

async function loadClaims() {
  const result = await api.get('/claims');
  claims.value = result.data;
}

async function removeClaim(id) {
  await api.delete(`/claims/${id}`);
  await loadClaims();
}

onMounted(loadClaims);
</script>
