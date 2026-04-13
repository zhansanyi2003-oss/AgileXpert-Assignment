<template>
  <section class="panel device-panel">
    <div class="panel__header">
      <div>
        <p class="eyebrow">Device screen</p>
        <h2 v-if="screenTitle">{{ screenTitle }}</h2>
      </div>
      <div class="device-panel__actions">
        <el-button
          plain
          round
          :type="editMode ? 'danger' : 'default'"
          @click="$emit('toggle-edit-mode')"
        >
          {{ editMode ? "Done editing" : "Edit mode" }}
        </el-button>
        <el-button
          plain
          round
          :disabled="pathStack.length === 0"
          @click="$emit('go-back')"
        >
          Back
        </el-button>
      </div>
    </div>

    <div class="device-shell" :style="deviceShellStyle">
      <div class="device-shell__status">
        <span>{{ breadcrumb }}</span>
      </div>

      <div class="device-grid">
        <div
          v-for="item in items"
          :key="item.id"
          class="device-tile"
          :class="
            item.type === 'SUBMENU' ? 'device-tile--folder' : 'device-tile--app'
          "
        >
          <div v-if="editMode" class="device-tile__edit-actions">
            <button
              class="device-tile__edit-button"
              @click.stop="$emit('rename-item', item.id)"
            >
              <EditPen />
            </button>
            <button
              class="device-tile__edit-button device-tile__edit-button--danger"
              @click.stop="$emit('delete-item', item.id)"
            >
              X
            </button>
          </div>
          <button
            class="device-tile__body"
            :disabled="editMode"
            @click="
              $emit(
                item.type === 'SUBMENU' ? 'open-folder' : 'launch-app',
                item,
              )
            "
          >
            <div
              v-if="item.type === 'SUBMENU'"
              class="device-tile__folder-shell"
            >
              <div
                class="device-tile__folder-preview"
                :class="folderPreviewClass(item)"
              >
                <span
                  v-for="preview in folderPreview(item)"
                  :key="preview.id"
                  class="device-tile__folder-chip"
                >
                  {{ appGlyph(preview.name) }}
                </span>
                <span
                  v-if="folderOverflow(item) > 0"
                  class="device-tile__folder-chip device-tile__folder-chip--more"
                >
                  +{{ folderOverflow(item) }}
                </span>
              </div>
            </div>
            <div v-else class="device-tile__icon">
              <span>{{ appGlyph(item.name) }}</span>
            </div>
            <span class="device-tile__name">{{ item.name }}</span>
          </button>
        </div>
      </div>

      <div v-if="!items.length" class="device-shell__empty">
        No apps or folders on this screen yet.
      </div>
    </div>
  </section>
</template>

<script setup>
import { EditPen } from "@element-plus/icons-vue";
import { computed } from "vue";

const props = defineProps({
  items: {
    type: Array,
    required: true,
  },
  pathStack: {
    type: Array,
    required: true,
  },
  currentUserName: {
    type: String,
    default: "",
  },
  currentThemeName: {
    type: String,
    default: "Light",
  },
  currentWallpaperPath: {
    type: String,
    default: "",
  },
  editMode: {
    type: Boolean,
    default: false,
  },
});

defineEmits([
  "open-folder",
  "go-back",
  "launch-app",
  "toggle-edit-mode",
  "delete-item",
  "rename-item",
]);

const screenTitle = computed(() =>
  props.currentUserName ? `${props.currentUserName}'s device` : "",
);
const breadcrumb = computed(() => {
  const names = props.pathStack.map((item) => item.name);
  return names.length ? ["Home", ...names].join(" / ") : "Home";
});
const deviceShellStyle = computed(() => {
  const wallpaper = props.currentWallpaperPath?.trim();
  const hasRealWallpaper = wallpaper && wallpaper.startsWith("/");
  if (!hasRealWallpaper) {
    return {};
  }
  const overlay =
    props.currentThemeName === "Dark"
      ? "linear-gradient(180deg, #050a14b8, #0a1220d1)"
      : "linear-gradient(180deg, #f4f8ff6b, #dae7f5ad)";
  return {
    backgroundImage: `${overlay}, url(${wallpaper})`,
    backgroundSize: "cover",
    backgroundPosition: "center",
  };
});

function appGlyph(name) {
  return name.slice(0, 2).toUpperCase();
}

function folderPreview(item) {
  const children = item.children ?? [];
  if (children.length <= 4) {
    return children;
  }
  return children.slice(0, 3);
}

function folderOverflow(item) {
  const children = item.children ?? [];
  return children.length > 4 ? children.length - 3 : 0;
}

function folderPreviewClass(item) {
  const visibleCount =
    folderPreview(item).length + (folderOverflow(item) > 0 ? 1 : 0);
  return `device-tile__folder-preview--${Math.min(visibleCount, 4)}`;
}
</script>
