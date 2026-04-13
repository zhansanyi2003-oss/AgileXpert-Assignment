<template>
  <main class="dashboard" :class="themeClass">
    <TopBar
      :current-user="currentUser"
      :current-theme="currentTheme"
      :current-wallpaper="currentWallpaper"
      @open-settings="settingsDrawerVisible = true"
      @simulate="handleSimulation"
    />

    <div class="dashboard__body">
      <UserSidebar
        :users="users"
        :current-user-id="currentUserId"
        @select-user="selectUser"
        @create-user="openCreateUserDialog"
        @rename-user="openRenameUserDialog"
        @delete-user="removeUser"
      />

      <section class="dashboard__main">
        <DeviceScreen
          :items="visibleMenuItems"
          :path-stack="pathStack"
          :current-user-name="currentUser?.name"
          :current-theme-name="currentTheme?.name"
          :current-wallpaper-path="currentWallpaper?.path"
          :edit-mode="editMode"
          @open-folder="openFolder"
          @go-back="goBack"
          @launch-app="launchAppOnScreen"
          @toggle-edit-mode="toggleEditMode"
          @delete-item="deleteMenuItemById"
          @rename-item="renameMenuItemById"
        />
      </section>
    </div>

    <el-drawer
      v-model="settingsDrawerVisible"
      title="Manage current device"
      size="420px"
      class="dashboard__settings-drawer"
    >
      <ControlPanel
        :apps="apps"
        :submenus="allSubmenus"
        :themes="themes"
        :wallpapers="wallpapers"
        :selected-theme-id="currentUser?.themeId"
        :selected-wallpaper-id="currentUser?.wallpaperId"
        :selected-root-app-id="selectedRootAppId"
        :selected-submenu-id="selectedSubmenuId"
        :selected-submenu-app-id="selectedSubmenuAppId"
        :new-submenu-name="newSubmenuName"
        @create-submenu="createFolder"
        @add-root-app="addAppToHomeScreen"
        @add-submenu-app="addAppIntoSubmenu"
        @select-theme="selectTheme"
        @select-wallpaper="selectWallpaper"
        @pick-wallpaper-file="pickWallpaperFile"
        @open-create-app="openCreateAppDialog"
        @edit-app="openEditAppDialog"
        @delete-app="removeApp"
        @update:selected-root-app-id="selectedRootAppId = $event"
        @update:selected-submenu-id="selectedSubmenuId = $event"
        @update:selected-submenu-app-id="selectedSubmenuAppId = $event"
        @update:new-submenu-name="newSubmenuName = $event"
      />
    </el-drawer>

    <el-dialog
      v-model="createAppDialogVisible"
      :title="appDialogMode === 'create' ? 'Create app' : 'Edit app'"
      width="420px"
      @closed="resetCreateAppForm"
    >
      <el-form @submit.prevent="submitAppDialog">
        <el-form-item label="App name">
          <el-input
            v-model="createAppForm.name"
            placeholder="Enter an app name"
            @keyup.enter="submitAppDialog"
          />
        </el-form-item>
        <el-form-item label="Icon name">
          <el-input
            v-model="createAppForm.iconName"
            placeholder="Enter an icon name"
            @keyup.enter="submitAppDialog"
          />
        </el-form-item>
        <el-form-item label="Launch message">
          <el-input
            v-model="createAppForm.launchMessage"
            placeholder="Enter a launch message"
            @keyup.enter="submitAppDialog"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createAppDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitAppDialog">
          {{ appDialogMode === 'create' ? 'Create' : 'Save' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="createUserDialogVisible"
      title="Create user"
      width="420px"
      @closed="createUserName = ''"
    >
      <el-form @submit.prevent="submitCreateUser">
        <el-form-item label="User name">
          <el-input
            v-model="createUserName"
            placeholder="Enter a user name"
            @keyup.enter="submitCreateUser"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createUserDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitCreateUser">Create</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="renameUserDialogVisible"
      title="Rename user"
      width="420px"
      @closed="renameUserName = ''"
    >
      <el-form @submit.prevent="submitRenameUser">
        <el-form-item label="User name">
          <el-input
            v-model="renameUserName"
            placeholder="Enter a user name"
            @keyup.enter="submitRenameUser"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameUserDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitRenameUser">Rename</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="renameItemDialogVisible"
      title="Rename item"
      width="420px"
      @closed="resetRenameItemDialog"
    >
      <el-form @submit.prevent="submitRenameMenuItem">
        <el-form-item label="Display name">
          <el-input
            v-model="renameItemName"
            placeholder="Enter a name"
            @keyup.enter="submitRenameMenuItem"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameItemDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitRenameMenuItem">Save</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="uploadWallpaperDialogVisible"
      title="Name wallpaper"
      width="420px"
      @closed="resetWallpaperUploadDialog"
    >
      <el-form @submit.prevent="submitWallpaperUpload">
        <el-form-item label="Wallpaper name">
          <el-input
            v-model="wallpaperUploadName"
            placeholder="Enter a wallpaper name"
            @keyup.enter="submitWallpaperUpload"
          />
        </el-form-item>
        <p v-if="pendingWallpaperFileName" class="dialog-helper">
          Selected file: {{ pendingWallpaperFileName }}
        </p>
      </el-form>
      <template #footer>
        <el-button @click="uploadWallpaperDialogVisible = false"
          >Cancel</el-button
        >
        <el-button type="primary" @click="submitWallpaperUpload"
          >Upload</el-button
        >
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import TopBar from "@/component/dashboard/TopBar.vue";
import UserSidebar from "@/component/dashboard/UserSidebar.vue";
import DeviceScreen from "@/component/dashboard/DeviceScreen.vue";
import ControlPanel from "@/component/dashboard/ControlPanel.vue";
import {
  createApp as createAppApi,
  deleteApp as deleteAppApi,
  updateApp as updateAppApi,
} from "@/api/app";
import { getDashboardData, runSimulation } from "@/api/dashboard";
import {
  getUser as getUserApi,
  selectTheme as selectThemeApi,
  selectWallpaper as selectWallpaperApi,
} from "@/api/user";
import { useMenuActions } from "@/composables/useMenuActions";
import { useUserActions } from "@/composables/useUserActions";
import { useWallpaperUpload } from "@/composables/useWallpaperUpload";

const users = ref([]);
const apps = ref([]);
const themes = ref([]);
const wallpapers = ref([]);
const currentUserId = ref(null);
const currentUser = ref(null);
const settingsDrawerVisible = ref(false);
const createAppDialogVisible = ref(false);
const appDialogMode = ref("create");
const editingAppId = ref(null);
const createAppForm = ref({
  name: "",
  iconName: "",
  launchMessage: "",
});

const currentTheme = computed(
  () =>
    themes.value.find((theme) => theme.id === currentUser.value?.themeId) ??
    null,
);
const currentWallpaper = computed(
  () =>
    wallpapers.value.find(
      (wallpaper) => wallpaper.id === currentUser.value?.wallpaperId,
    ) ?? null,
);
const themeClass = computed(() =>
  currentTheme.value?.name === "Dark" ? "dashboard--dark" : "dashboard--light",
);

onMounted(async () => {
  await loadDashboard();
});

watch(
  themeClass,
  (nextTheme, previousTheme) => {
    if (previousTheme) {
      document.body.classList.remove(previousTheme);
    }
    document.body.classList.add(nextTheme);
  },
  { immediate: true },
);

onBeforeUnmount(() => {
  document.body.classList.remove("dashboard--light", "dashboard--dark");
});

function applyDashboardData(data, preferredUserId = null) {
  users.value = data.users;
  apps.value = data.apps;
  themes.value = data.themes;
  wallpapers.value = data.wallpapers;
  settingsDrawerVisible.value = false;
  // The dashboard response only carries shared data and user summaries.
  resetMenuState();

  const nextUserId = data.users.some((user) => user.id === preferredUserId)
    ? preferredUserId
    : (data.users[0]?.id ?? null);

  currentUserId.value = nextUserId;
  return nextUserId;
}

async function loadDashboard(preferredUserId = currentUserId.value) {
  const data = await getDashboardData();
  const nextUserId = applyDashboardData(data, preferredUserId);
  if (!nextUserId) {
    currentUser.value = null;
    return;
  }
  // Load the selected device separately so user switching stays cheap.
  currentUser.value = await getUserApi(nextUserId);
}

async function loadCurrentUser(userId) {
  if (!userId) {
    currentUserId.value = null;
    currentUser.value = null;
    return;
  }

  const user = await getUserApi(userId);
  currentUserId.value = userId;
  currentUser.value = user;
}

function syncUserSummary(updatedUser) {
  const summary = users.value.find((user) => user.id === updatedUser.id);
  if (!summary) {
    return;
  }

  summary.name = updatedUser.name;
  summary.themeId = updatedUser.themeId;
  summary.wallpaperId = updatedUser.wallpaperId;
}

async function selectUser(userId) {
  try {
    settingsDrawerVisible.value = false;
    resetMenuState();
    await loadCurrentUser(userId);
  } catch (error) {
    showApiError(error);
  }
}

function showApiError(error) {
  ElMessage.error(error?.message || "Operation failed.");
}

function openCreateAppDialog() {
  appDialogMode.value = "create";
  editingAppId.value = null;
  resetCreateAppForm();
  createAppDialogVisible.value = true;
}

function openEditAppDialog(app) {
  appDialogMode.value = "edit";
  editingAppId.value = app.id;
  createAppForm.value = {
    name: app.name,
    iconName: app.iconName,
    launchMessage: app.launchMessage,
  };
  createAppDialogVisible.value = true;
}

function resetCreateAppForm() {
  editingAppId.value = null;
  appDialogMode.value = "create";
  createAppForm.value = {
    name: "",
    iconName: "",
    launchMessage: "",
  };
}

async function handleSimulation() {
  try {
    const data = await runSimulation();
    const nextUserId = applyDashboardData(data, data.users[0]?.id ?? null);
    currentUser.value = nextUserId ? await getUserApi(nextUserId) : null;
    ElMessage.success("Simulation data loaded.");
  } catch (error) {
    showApiError(error);
  }
}

async function selectTheme(themeId) {
  if (!currentUser.value) {
    return;
  }
  try {
    const updated = await selectThemeApi(currentUser.value.id, themeId);
    currentUser.value = updated;
    syncUserSummary(updated);
    ElMessage.success("Theme updated.");
  } catch (error) {
    showApiError(error);
  }
}

async function selectWallpaper(wallpaperId) {
  if (!currentUser.value) {
    return;
  }
  try {
    const updated = await selectWallpaperApi(currentUser.value.id, wallpaperId);
    currentUser.value = updated;
    syncUserSummary(updated);
    ElMessage.success("Wallpaper updated.");
  } catch (error) {
    showApiError(error);
  }
}

async function submitAppDialog() {
  const appName = createAppForm.value.name.trim();
  const iconName = createAppForm.value.iconName.trim();
  const launchMessage = createAppForm.value.launchMessage.trim();

  if (!appName || !iconName || !launchMessage) {
    ElMessage.warning("Fill in all app fields first.");
    return;
  }

  try {
    const saved =
      appDialogMode.value === "create"
        ? await createAppApi({
            name: appName,
            iconName,
            launchMessage,
          })
        : await updateAppApi(editingAppId.value, {
            name: appName,
            iconName,
            launchMessage,
          });
    createAppDialogVisible.value = false;
    await loadDashboard(currentUserId.value);
    ElMessage.success(
      appDialogMode.value === "create"
        ? `Created app ${saved.name}.`
        : `Updated app ${saved.name}.`,
    );
  } catch (error) {
    showApiError(error);
  }
}

async function removeApp(app) {
  try {
    await ElMessageBox.confirm(
      `Delete ${app.name}? You will only be able to remove it if it is not used in any menu.`,
      "Delete app",
      {
        confirmButtonText: "Delete",
        cancelButtonText: "Cancel",
        type: "warning",
      },
    );
  } catch {
    return;
  }

  try {
    await deleteAppApi(app.id);
    await loadDashboard(currentUserId.value);
    ElMessage.success(`Deleted app ${app.name}.`);
  } catch (error) {
    showApiError(error);
  }
}

const {
  createUserDialogVisible,
  renameUserDialogVisible,
  createUserName,
  renameUserName,
  openCreateUserDialog,
  submitCreateUser,
  openRenameUserDialog,
  submitRenameUser,
  removeUser,
} = useUserActions({
  currentUser,
  users,
  loadDashboard,
  showApiError,
});

const {
  pathStack,
  editMode,
  renameItemDialogVisible,
  selectedRootAppId,
  selectedSubmenuId,
  selectedSubmenuAppId,
  newSubmenuName,
  renameItemName,
  allSubmenus,
  visibleMenuItems,
  openFolder,
  goBack,
  launchAppOnScreen,
  createFolder,
  addAppToHomeScreen,
  addAppIntoSubmenu,
  deleteMenuItemById,
  renameMenuItemById,
  resetRenameItemDialog,
  submitRenameMenuItem,
  toggleEditMode,
  resetMenuState,
} = useMenuActions({
  currentUser,
  apps,
  loadDashboard,
  showApiError,
});

const {
  uploadWallpaperDialogVisible,
  pendingWallpaperFileName,
  wallpaperUploadName,
  pickWallpaperFile,
  resetWallpaperUploadDialog,
  submitWallpaperUpload,
} = useWallpaperUpload({
  currentUser,
  loadDashboard,
  selectWallpaper,
  showApiError,
});
</script>
