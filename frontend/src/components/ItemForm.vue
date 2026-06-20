<template>
  <form class="form-grid glass-card tilt-card" @submit.prevent="submitForm">
    <label>
      标题
      <input v-model.trim="form.title" :placeholder="titlePlaceholder" required />
    </label>

    <label>
      类型
      <select v-model="form.type" required>
        <option value="lost">失物</option>
        <option value="found">拾物</option>
      </select>
    </label>

    <label>
      分类
      <select v-model="form.categoryId" required>
        <option value="">请选择分类</option>
        <option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.name }}
        </option>
      </select>
    </label>

    <label>
      地点
      <input v-model.trim="form.location" :placeholder="locationPlaceholder" required />
    </label>

    <label>
      日期
      <input v-model="form.eventDate" type="date" required />
    </label>

    <label>
      联系人
      <input v-model.trim="form.contactName" placeholder="联系人姓名" required />
    </label>

    <label>
      联系电话
      <input v-model.trim="form.contactPhone" placeholder="11 位手机号或办公室电话" required />
    </label>

    <label v-if="showImageUpload" class="full-row image-upload-field">
      物品图片
      <div class="image-upload-panel">
        <div class="image-upload-actions">
          <input
            ref="fileInput"
            class="image-file-input"
            type="file"
            accept="image/*"
            @change="handleFileChange"
          />
          <button class="small-button" type="button" :disabled="uploading" @click="triggerFileSelect">
            {{ uploading ? '上传中...' : '从电脑选择图片' }}
          </button>
          <span class="image-upload-tip">拾到物品请上传实拍图，方便失主核对。</span>
        </div>
        <div v-if="previewUrl" class="image-preview-card">
          <img :src="previewUrl" alt="物品预览图" class="image-preview" />
          <button class="ghost-link" type="button" @click="clearImage">移除图片</button>
        </div>
      </div>
    </label>

    <label class="full-row">
      描述
      <textarea
        v-model.trim="form.description"
        rows="5"
        :placeholder="descriptionPlaceholder"
        required
      ></textarea>
    </label>

    <p v-if="error" class="form-error">{{ error }}</p>
    <button class="submit-button full-row" type="submit" :disabled="uploading">
      {{ uploading ? '图片上传中...' : submitText }}
    </button>
  </form>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue';
import { api } from '../services/api';

const props = defineProps({
  categories: {
    type: Array,
    default: () => []
  },
  initialValue: {
    type: Object,
    default: () => ({})
  },
  submitText: {
    type: String,
    default: '提交'
  }
});

const emit = defineEmits(['submit']);

const error = ref('');
const uploading = ref(false);
const previewUrl = ref('');
const fileInput = ref(null);

const form = reactive({
  title: '',
  type: 'lost',
  categoryId: '',
  location: '',
  eventDate: new Date().toISOString().slice(0, 10),
  description: '',
  imageUrl: '',
  contactName: '',
  contactPhone: ''
});

const showImageUpload = computed(() => form.type === 'found');
const titlePlaceholder = computed(() => (form.type === 'found' ? '例如：拾到黑色头戴式耳机' : '例如：丢失蓝色保温杯'));
const locationPlaceholder = computed(() => (form.type === 'found' ? '例如：图书馆二楼靠窗座位' : '例如：操场看台附近'));
const descriptionPlaceholder = computed(() =>
  form.type === 'found'
    ? '请描述你拾到物品的外观、所在位置、是否暂时保管等信息'
    : '请描述丢失物品的颜色、品牌、挂件、内容物、划痕等明显特征'
);

watch(
  () => props.initialValue,
  (value) => {
    Object.assign(form, value);
    previewUrl.value = value?.imageUrl || '';
  },
  { immediate: true }
);

watch(
  () => form.type,
  (nextType) => {
    if (nextType === 'lost') {
      form.imageUrl = '';
      previewUrl.value = '';
      resetFileInput();
    }
  }
);

function triggerFileSelect() {
  fileInput.value?.click();
}

async function handleFileChange(event) {
  const [file] = event.target.files || [];
  if (!file) {
    return;
  }

  error.value = '';

  if (!file.type.startsWith('image/')) {
    error.value = '只能选择图片文件。';
    resetFileInput();
    return;
  }

  const formData = new FormData();
  formData.append('file', file);

  try {
    uploading.value = true;
    const response = await api.post('/items/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    form.imageUrl = response.data.url;
    previewUrl.value = response.data.url;
  } catch (uploadError) {
    error.value = uploadError.message || '图片上传失败，请稍后再试。';
  } finally {
    uploading.value = false;
    resetFileInput();
  }
}

function clearImage() {
  form.imageUrl = '';
  previewUrl.value = '';
  resetFileInput();
}

function resetFileInput() {
  if (fileInput.value) {
    fileInput.value.value = '';
  }
}

function submitForm() {
  error.value = '';

  if (uploading.value) {
    error.value = '图片还在上传中，请稍后再提交。';
    return;
  }

  if (form.description.length < 8) {
    error.value = '描述至少 8 个字，方便同学准确识别。';
    return;
  }

  if (!/^[0-9\\-]{7,20}$/.test(form.contactPhone)) {
    error.value = '联系电话格式不正确。';
    return;
  }

  if (form.type === 'found' && !form.imageUrl) {
    error.value = '拾到物品请上传一张实拍图片。';
    return;
  }

  if (form.type === 'lost') {
    form.imageUrl = '';
  }

  emit('submit', { ...form, categoryId: Number(form.categoryId) });
}
</script>

<style scoped>
.image-upload-field,
.notice-field {
  gap: 12px;
}

.image-upload-panel {
  display: grid;
  gap: 14px;
}

.image-upload-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.image-file-input {
  display: none;
}

.image-upload-tip {
  color: rgba(255, 255, 255, 0.68);
  font-size: 13px;
}

.image-preview-card,
.lost-form-note {
  display: grid;
  gap: 10px;
  justify-items: start;
  padding: 14px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
}

.lost-form-note strong {
  color: rgba(255, 255, 255, 0.92);
}

.lost-form-note span {
  color: rgba(255, 255, 255, 0.72);
  line-height: 1.7;
}

.image-preview {
  width: min(100%, 320px);
  height: 190px;
  object-fit: cover;
  border-radius: 14px;
  display: block;
}
</style>
