<template>
  <section class="control-panel">
    <div class="control-panel__intro">
      <p class="eyebrow">Device settings</p>
      <p class="control-panel__copy">
        Adjust this device's appearance and organize what shows up on the home screen.
      </p>
    </div>

    <section class="settings-section">
      <div class="settings-section__header">
        <div>
          <h3>Appearance</h3>
          <p>Choose the current visual style for this device.</p>
        </div>
      </div>

      <div class="settings-section__body">
        <label class="field">
          <span class="field__label">Theme</span>
          <el-select
            :model-value="selectedThemeId"
            placeholder="Select theme"
            @change="$emit('select-theme', $event)"
          >
            <el-option v-for="theme in themes" :key="theme.id" :label="theme.name" :value="theme.id" />
          </el-select>
        </label>

        <label class="field">
          <span class="field__label">Wallpaper</span>
          <el-upload
            class="wallpaper-upload"
            :show-file-list="false"
            :auto-upload="false"
            accept="image/*"
            :on-change="handleWallpaperPicked"
          >
            <el-button round>Upload photo</el-button>
          </el-upload>
          <el-select
            :model-value="selectedWallpaperId"
            placeholder="Select wallpaper"
            @change="$emit('select-wallpaper', $event)"
          >
            <el-option
              v-for="wallpaper in wallpapers"
              :key="wallpaper.id"
              :label="wallpaper.name"
              :value="wallpaper.id"
            />
          </el-select>
        </label>
      </div>
    </section>

    <section class="settings-section">
      <div class="settings-section__header">
        <div>
          <h3>Menu management</h3>
          <p>Add folders and decide which apps live on the home screen or inside folders.</p>
        </div>
      </div>

      <div class="settings-section__body">
        <label class="field">
          <span class="field__label">New folder</span>
          <el-input
            :model-value="newSubmenuName"
            placeholder="Folder name"
            @update:model-value="$emit('update:new-submenu-name', $event)"
          />
          <el-button round @click="$emit('create-submenu')">Create submenu</el-button>
        </label>

        <label class="field">
          <span class="field__label">Add app to home screen</span>
          <el-select
            :model-value="selectedRootAppId"
            placeholder="Select app"
            @change="$emit('update:selected-root-app-id', $event)"
          >
            <el-option v-for="app in apps" :key="app.id" :label="app.name" :value="app.id" />
          </el-select>
          <el-button round @click="$emit('add-root-app')">Add to home screen</el-button>
        </label>

        <label class="field">
          <span class="field__label">Add app to folder</span>
          <el-select
            :model-value="selectedSubmenuId"
            placeholder="Select submenu"
            @change="$emit('update:selected-submenu-id', $event)"
          >
            <el-option v-for="submenu in submenus" :key="submenu.id" :label="submenu.name" :value="submenu.id" />
          </el-select>
          <el-select
            :model-value="selectedSubmenuAppId"
            placeholder="Select app"
            @change="$emit('update:selected-submenu-app-id', $event)"
          >
            <el-option v-for="app in apps" :key="app.id" :label="app.name" :value="app.id" />
          </el-select>
          <el-button round @click="$emit('add-submenu-app')">Add to folder</el-button>
        </label>
      </div>
    </section>

    <section class="settings-section">
      <div class="settings-section__header">
        <div>
          <h3>Apps</h3>
          <p>Create a new app, then place it on the home screen or inside folders.</p>
        </div>
      </div>

      <div class="settings-section__body">
        <div class="apps-toolbar">
          <el-button round type="primary" @click="$emit('open-create-app')">Create app</el-button>
        </div>

        <div class="installed-apps">
          <article v-for="app in apps" :key="app.id" class="installed-app">
            <div class="installed-app__icon">{{ app.name.slice(0, 2).toUpperCase() }}</div>
            <div class="installed-app__content">
              <strong>{{ app.name }}</strong>
              <p>{{ app.launchMessage }}</p>
            </div>
            <div class="installed-app__actions">
              <el-button text @click="$emit('edit-app', app)">Edit</el-button>
              <el-button text type="danger" @click="$emit('delete-app', app)">Delete</el-button>
            </div>
          </article>
        </div>
      </div>
    </section>
  </section>
</template>

<script setup>
function handleWallpaperPicked(uploadFile) {
  if (uploadFile?.raw) {
    emit('pick-wallpaper-file', uploadFile.raw)
  }
}

defineProps({
  apps: {
    type: Array,
    required: true,
  },
  submenus: {
    type: Array,
    required: true,
  },
  themes: {
    type: Array,
    required: true,
  },
  wallpapers: {
    type: Array,
    required: true,
  },
  selectedThemeId: {
    type: String,
    default: '',
  },
  selectedWallpaperId: {
    type: String,
    default: '',
  },
  selectedRootAppId: {
    type: String,
    default: '',
  },
  selectedSubmenuId: {
    type: String,
    default: '',
  },
  selectedSubmenuAppId: {
    type: String,
    default: '',
  },
  newSubmenuName: {
    type: String,
    default: '',
  },
})

const emit = defineEmits([
  'create-submenu',
  'add-root-app',
  'add-submenu-app',
  'select-theme',
  'select-wallpaper',
  'pick-wallpaper-file',
  'open-create-app',
  'edit-app',
  'delete-app',
  'update:selected-root-app-id',
  'update:selected-submenu-id',
  'update:selected-submenu-app-id',
  'update:new-submenu-name',
])
</script>
