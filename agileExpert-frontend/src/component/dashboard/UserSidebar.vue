<template>
  <aside class="panel sidebar">
    <div class="panel__header">
      <div>
        <p class="eyebrow">Users</p>
      </div>
      <el-button
        class="sidebar__action-button"
        plain
        round
        @click="$emit('create-user')"
      >
        Create user
      </el-button>
    </div>

    <div class="sidebar__list">
      <button
        v-for="user in users"
        :key="user.id"
        class="sidebar__user"
        :class="{ 'sidebar__user--active': user.id === currentUserId }"
        @click="$emit('select-user', user.id)"
      >
        <span>
          <span class="sidebar__name">{{ user.name }}</span>
        </span>
      </button>
    </div>

    <div class="sidebar__actions">
      <el-button
        class="sidebar__action-button"
        round
        :disabled="!currentUserId"
        @click="$emit('rename-user')"
      >
        Rename user
      </el-button>
      <el-button
        class="sidebar__action-button sidebar__action-button--danger"
        type="danger"
        plain
        round
        :disabled="!currentUserId"
        @click="$emit('delete-user')"
      >
        Delete user
      </el-button>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  users: {
    type: Array,
    required: true,
  },
  currentUserId: {
    type: String,
    default: null,
  },
});

defineEmits(["select-user", "create-user", "rename-user", "delete-user"]);
</script>
