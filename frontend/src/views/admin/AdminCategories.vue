<template>
  <AppShell title="分类管理" subtitle="管理员端 · 维护物品分类" role="admin">
    <form class="inline-form glass-card" @submit.prevent="saveCategory">
      <input v-model.trim="form.name" placeholder="分类名称" required />
      <input v-model="form.color" type="color" aria-label="分类颜色" />
      <input v-model.trim="form.description" placeholder="分类说明" />
      <button class="small-button" type="submit">{{ form.id ? '保存修改' : '新增分类' }}</button>
      <button v-if="form.id" class="ghost-link" type="button" @click="resetForm">取消</button>
    </form>

    <DataTable :columns="columns" :rows="categories">
      <template #color="{ row }">
        <span class="color-chip" :style="{ background: row.color }"></span>{{ row.color }}
      </template>
      <template #actions="{ row }">
        <button class="ghost-link" type="button" @click="editCategory(row)">编辑</button>
        <button class="danger-link" type="button" @click="removeCategory(row.id)">删除</button>
      </template>
    </DataTable>
  </AppShell>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import AppShell from '../../components/AppShell.vue';
import DataTable from '../../components/DataTable.vue';
import { api } from '../../services/api';

const categories = ref([]);
const form = reactive({ id: null, name: '', color: '#1f9d88', description: '' });
const columns = [
  { key: 'name', label: '名称' },
  { key: 'color', label: '颜色' },
  { key: 'description', label: '说明' }
];

function resetForm() {
  Object.assign(form, { id: null, name: '', color: '#1f9d88', description: '' });
}

function editCategory(row) {
  Object.assign(form, row);
}

async function loadCategories() {
  const result = await api.get('/categories');
  categories.value = result.data;
}

async function saveCategory() {
  if (form.id) {
    await api.put(`/categories/${form.id}`, form);
  } else {
    await api.post('/categories', form);
  }
  resetForm();
  await loadCategories();
}

async function removeCategory(id) {
  if (!confirm('删除分类前请确认没有物品正在使用它。')) return;
  await api.delete(`/categories/${id}`);
  await loadCategories();
}

onMounted(loadCategories);
</script>
