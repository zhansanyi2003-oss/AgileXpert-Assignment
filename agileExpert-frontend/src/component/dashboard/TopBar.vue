<template>
  <header class="top-bar">
    <div>
      <p class="eyebrow">AgileExpert</p>
      <h1>Smart Device Dashboard</h1>
    </div>
    <div class="top-bar__meta">
      <div class="top-bar__chips">
        <div class="top-bar__chip">
          <span class="top-bar__label">Current user</span>
          <strong>{{ currentUser?.name ?? 'None' }}</strong>
        </div>
        <div class="top-bar__chip">
          <span class="top-bar__label">Theme</span>
          <strong>{{ currentTheme?.name ?? 'Unassigned' }}</strong>
        </div>
        <div class="top-bar__chip">
          <span class="top-bar__label">Wallpaper</span>
          <strong>{{ currentWallpaper?.name ?? 'Unassigned' }}</strong>
        </div>
      </div>
      <div class="top-bar__actions">
        <el-button round @click="$emit('open-settings')">Settings</el-button>
        <el-button type="primary" round @click="$emit('simulate')">Simulation</el-button>
      </div>
    </div>
  </header>
</template>

<script setup>
defineProps({
  currentUser: {
    type: Object,
    default: null,
  },
  currentTheme: {
    type: Object,
    default: null,
  },
  currentWallpaper: {
    type: Object,
    default: null,
  },
})

defineEmits(['simulate', 'open-settings'])
</script>
