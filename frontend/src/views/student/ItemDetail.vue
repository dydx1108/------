<template>
  <AppShell :title="pageTitle" :subtitle="pageSubtitle" role="student">
    <article v-if="item" class="detail-card glass-card tilt-card" :class="item.type === 'lost' ? 'detail-card--lost' : 'detail-card--found'">
      <div v-if="item.type === 'found'" class="detail-card__visual">
        <img :src="item.image_url" :alt="item.title" />
        <span class="type-badge found">{{ typeMap[item.type] }}</span>
      </div>

      <div v-else class="detail-card__notice">
        <span class="detail-card__index">{{ String(item.id).padStart(2, '0') }}</span>
        <p>失物寻回</p>
        <strong>本条信息仅展示文字特征，请认真核对细节后再联系。</strong>
      </div>

      <div>
        <p class="eyebrow">{{ typeMap[item.type] }} · {{ item.category_name }}</p>
        <h2>{{ item.title }}</h2>
        <p>{{ item.description }}</p>
        <div class="meta-grid detail-meta-grid">
          <span>{{ item.type === 'found' ? '拾到地点' : '丢失地点' }}：{{ item.location }}</span>
          <span>{{ item.type === 'found' ? '拾到时间' : '丢失时间' }}：{{ formatDate(item.event_date) }}</span>
          <span>联系人：{{ item.contact_name }}</span>
          <span>电话：{{ item.contact_phone }}</span>
        </div>
        <form v-if="item.type === 'found'" class="claim-form" @submit.prevent="submitClaim">
          <textarea v-model.trim="evidence" rows="4" placeholder="请填写能证明归属的特征或说明" required></textarea>
          <button class="submit-button" type="submit">提交认领</button>
        </form>
        <div v-else class="detail-contact-note">
          <strong>这是一条失物寻回信息</strong>
          <p>如果你知道相关线索，直接通过上方联系方式联系发布人会更合适。</p>
        </div>
      </div>
    </article>
    <ToastMessage :message="toast" />
  </AppShell>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import AppShell from '../../components/AppShell.vue';
import ToastMessage from '../../components/ToastMessage.vue';
import { api } from '../../services/api';
import { formatDate, typeMap } from '../../utils/format';

const route = useRoute();
const item = ref(null);
const evidence = ref('');
const toast = ref('');
const pageTitle = ref('物品详情');
const pageSubtitle = ref('学生端 · 查看信息');

async function loadItem() {
  const result = await api.get(`/items/${route.params.id}`);
  item.value = result.data;
  pageTitle.value = item.value.type === 'found' ? '拾物详情' : '失物详情';
  pageSubtitle.value = item.value.type === 'found' ? '学生端 · 发起认领申请' : '学生端 · 查看失物线索';
}

async function submitClaim() {
  if (evidence.value.length < 8) {
    toast.value = '认领说明至少 8 个字。';
    return;
  }

  await api.post('/claims', { itemId: item.value.id, evidence: evidence.value });
  evidence.value = '';
  toast.value = '认领申请已提交，请等待管理员审核。';
}

onMounted(loadItem);
</script>
