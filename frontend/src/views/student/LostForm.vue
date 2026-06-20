<template>
  <AppShell :title="pageTitle" subtitle="学生端 · 发布信息" role="student">
    <ItemForm :categories="categories" :initial-value="initialValue" submit-text="提交审核" @submit="submitItem" />
    <ToastMessage :message="toast" />
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AppShell from '../../components/AppShell.vue';
import ItemForm from '../../components/ItemForm.vue';
import ToastMessage from '../../components/ToastMessage.vue';
import { api } from '../../services/api';

const route = useRoute();
const router = useRouter();
const categories = ref([]);
const toast = ref('');

const itemType = computed(() => route.meta.itemType || 'lost');
const pageTitle = computed(() => (itemType.value === 'lost' ? '发布失物' : '发布拾物'));
const initialValue = computed(() => ({ type: itemType.value }));

async function submitItem(payload) {
  await api.post('/items', payload);
  toast.value = '提交成功，管理员审核后会展示到对应专区。';
  setTimeout(() => router.push('/student/posts'), 900);
}

onMounted(async () => {
  const result = await api.get('/categories');
  categories.value = result.data;
});
</script>
