import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addRootApp,
  addSubmenuApp,
  createSubmenu,
  deleteMenuItem,
  launchMenuItem,
  renameMenuItem,
} from '@/api/menu'
import { collectSubmenus } from '@/utils/menu'

export function useMenuActions({ currentUser, apps, loadDashboard, showApiError }) {
  const pathStack = ref([])
  const editMode = ref(false)
  const renameItemDialogVisible = ref(false)
  const selectedRootAppId = ref('')
  const selectedSubmenuId = ref('')
  const selectedSubmenuAppId = ref('')
  const newSubmenuName = ref('')
  const renameItemName = ref('')
  const renameItemTargetId = ref(null)

  const allSubmenus = computed(() => {
    if (!currentUser.value) {
      return []
    }

    return collectSubmenus(currentUser.value.menu)
  })

  const visibleMenuItems = computed(() => {
    if (!currentUser.value) {
      return []
    }

    // The last opened folder decides which screen we are currently showing.
    if (!pathStack.value.length) {
      return currentUser.value.menu
    }

    return pathStack.value.at(-1).children ?? []
  })

  function openFolder(item) {
    pathStack.value = [...pathStack.value, item]
    editMode.value = false
  }

  function goBack() {
    pathStack.value = pathStack.value.slice(0, -1)
    editMode.value = false
  }

  async function launchAppOnScreen(item) {
    if (!currentUser.value) {
      return
    }

    try {
      const result = await launchMenuItem(currentUser.value.id, item.id)
      ElMessage.success(result)
    } catch (error) {
      showApiError(error)
    }
  }

  async function createFolder() {
    if (!currentUser.value) {
      return
    }

    const submenuName = newSubmenuName.value.trim()
    if (!submenuName) {
      ElMessage.warning('Submenu name cannot be blank.')
      return
    }

    try {
      await createSubmenu(currentUser.value.id, {
        name: submenuName,
        parentMenuItemId: pathStack.value.at(-1)?.id ?? null,
      })
      newSubmenuName.value = ''
      await loadDashboard(currentUser.value.id)
      ElMessage.success(`Created submenu ${submenuName}.`)
    } catch (error) {
      showApiError(error)
    }
  }

  async function addAppToHomeScreen() {
    if (!currentUser.value) {
      return
    }

    if (!selectedRootAppId.value) {
      ElMessage.warning('Select an app first.')
      return
    }

    const app = apps.value.find((entry) => entry.id === selectedRootAppId.value)

    try {
      await addRootApp(currentUser.value.id, selectedRootAppId.value)
      selectedRootAppId.value = ''
      await loadDashboard(currentUser.value.id)
      ElMessage.success(`Added ${app?.name ?? 'app'} to home screen.`)
    } catch (error) {
      showApiError(error)
    }
  }

  async function addAppIntoSubmenu() {
    if (!currentUser.value) {
      return
    }

    if (!allSubmenus.value.length) {
      ElMessage.warning('Create a submenu first.')
      return
    }

    if (!selectedSubmenuId.value || !selectedSubmenuAppId.value) {
      ElMessage.warning('Select both a submenu and an app.')
      return
    }

    const app = apps.value.find((entry) => entry.id === selectedSubmenuAppId.value)
    const target = allSubmenus.value.find((item) => item.id === selectedSubmenuId.value)

    try {
      await addSubmenuApp(currentUser.value.id, selectedSubmenuId.value, selectedSubmenuAppId.value)
      selectedSubmenuId.value = ''
      selectedSubmenuAppId.value = ''
      await loadDashboard(currentUser.value.id)
      ElMessage.success(`Added ${app?.name ?? 'app'} to ${target?.name ?? 'folder'}.`)
    } catch (error) {
      showApiError(error)
    }
  }

  async function deleteMenuItemById(itemId) {
    const target = visibleMenuItems.value.find((item) => item.id === itemId)
    if (!target || !currentUser.value) {
      ElMessage.error('Menu item not found on this screen.')
      return
    }

    try {
      await ElMessageBox.confirm(`Delete ${target.name} from this screen?`, 'Delete menu item', {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning',
      })
    } catch {
      return
    }

    try {
      await deleteMenuItem(currentUser.value.id, itemId)
      await loadDashboard(currentUser.value.id)
      ElMessage.success(`Deleted ${target.name}.`)
    } catch (error) {
      showApiError(error)
    }
  }

  function renameMenuItemById(itemId) {
    const target = visibleMenuItems.value.find((item) => item.id === itemId)
    if (!target) {
      ElMessage.error('Menu item not found on this screen.')
      return
    }

    renameItemTargetId.value = itemId
    renameItemName.value = target.name
    renameItemDialogVisible.value = true
  }

  function resetRenameItemDialog() {
    renameItemName.value = ''
    renameItemTargetId.value = null
  }

  async function submitRenameMenuItem() {
    const itemId = renameItemTargetId.value
    if (!itemId || !currentUser.value) {
      return
    }

    const nextName = renameItemName.value.trim()
    if (!nextName) {
      ElMessage.warning('Name cannot be blank.')
      return
    }

    try {
      const updated = await renameMenuItem(currentUser.value.id, itemId, nextName)
      renameItemDialogVisible.value = false
      await loadDashboard(currentUser.value.id)
      ElMessage.success(`Renamed item to ${updated.name}.`)
    } catch (error) {
      showApiError(error)
    }
  }

  function toggleEditMode() {
    editMode.value = !editMode.value
  }

  function resetMenuState() {
    pathStack.value = []
    editMode.value = false
    newSubmenuName.value = ''
    selectedRootAppId.value = ''
    selectedSubmenuId.value = ''
    selectedSubmenuAppId.value = ''
  }

  return {
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
  }
}
