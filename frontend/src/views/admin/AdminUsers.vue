<template>
  <AppShell title="用户管理" subtitle="管理员端 · 学生与管理员账号" role="admin">
    <form class="inline-form glass-card" @submit.prevent="saveUser">
      <input v-model.trim="form.username" placeholder="用户名" :disabled="Boolean(form.id)" required />
      <input v-if="!form.id" v-model="form.password" type="password" placeholder="密码" required />
      <input v-model.trim="form.realName" placeholder="姓名" required />
      <select v-model="form.role">
        <option value="student">学生</option>
        <option value="admin">管理员</option>
      </select>
      <input v-model.trim="form.phone" placeholder="电话" />
      <input v-model.trim="form.college" placeholder="学院/部门" />
      <button class="small-button" type="submit">{{ form.id ? '保存修改' : '新增用户' }}</button>
      <button v-if="form.id" class="ghost-link" type="button" @click="resetForm">取消</button>
    </form>

    <DataTable :columns="columns" :rows="users">
      <template #role="{ row }">{{ row.role === 'admin' ? '管理员' : '学生' }}</template>
      <template #actions="{ row }">
        <button class="ghost-link" type="button" @click="editUser(row)">编辑</button>
        <button class="danger-link" type="button" @click="removeUser(row.id)">删除</button>
      </template>
    </DataTable>
  </AppShell>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import AppShell from '../../components/AppShell.vue';
import DataTable from '../../components/DataTable.vue';
import { api } from '../../services/api';

const users = ref([]);
const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  role: 'student',
  phone: '',
  college: '',
  status: 'active'
});
const columns = [
  { key: 'username', label: '用户名' },
  { key: 'realName', label: '姓名' },
  { key: 'role', label: '角色' },
  { key: 'phone', label: '电话' },
  { key: 'college', label: '学院/部门' },
  { key: 'status', label: '状态' }
];

function resetForm() {
  Object.assign(form, { id: null, username: '', password: '', realName: '', role: 'student', phone: '', college: '', status: 'active' });
}

function editUser(row) {
  Object.assign(form, row, { password: '' });
}

async function loadUsers() {
  const result = await api.get('/users');
  users.value = result.data;
}

async function saveUser() {
  if (form.id) {
    await api.put(`/users/${form.id}`, form);
  } else {
    await api.post('/users', form);
  }
  resetForm();
  await loadUsers();
}

async function removeUser(id) {
  if (!confirm('确认删除该用户吗？')) return;
  await api.delete(`/users/${id}`);
  await loadUsers();
}

onMounted(loadUsers);
</script>
