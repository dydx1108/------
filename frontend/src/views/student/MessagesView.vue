<template>
  <AppShell title="消息中心" subtitle="学生端 · 系统提醒" role="student">
    <div class="message-list">
      <article v-for="message in messages" :key="message.title" class="message-card glass-card reveal-card">
        <span class="card-layer card-layer--slow">{{ message.time }}</span>
        <h2 class="card-layer card-layer--front">{{ message.title }}</h2>
        <p class="card-layer card-layer--mid">{{ message.text }}</p>
      </article>
    </div>
  </AppShell>
</template>

<script setup>
const messages = [
  { time: '刚刚', title: '审核提醒', text: '你发布的信息提交后，将由管理员审核，审核通过后进入招领广场。' },
  { time: '今天', title: '认领建议', text: '认领时请填写具体特征，例如颜色、贴纸、卡号后四位等，提高审核通过率。' },
  { time: '本周', title: '安全提示', text: '涉及贵重物品请优先联系保卫处或辅导员，避免私下转账或泄露隐私。' }
];
</script>
