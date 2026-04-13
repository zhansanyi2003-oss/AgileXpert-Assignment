import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createUser, deleteUser, renameUser } from '@/api/user'

export function useUserActions({ currentUser, users, loadDashboard, showApiError }) {
  const createUserDialogVisible = ref(false)
  const renameUserDialogVisible = ref(false)
  const createUserName = ref('')
  const renameUserName = ref('')
  const USER_LIMIT_MESSAGE = 'User limit reached. Please recharge to add more users.'

  function openCreateUserDialog() {
    if (users.value.length >= 5) {
      ElMessage.warning(USER_LIMIT_MESSAGE)
      return
    }

    createUserName.value = ''
    createUserDialogVisible.value = true
  }

  async function submitCreateUser() {
    const name = createUserName.value.trim()
    if (!name) {
      ElMessage.warning('User name cannot be blank.')
      return
    }

    try {
      const created = await createUser(name)
      createUserDialogVisible.value = false
      await loadDashboard(created.id)
      ElMessage.success(`Created user ${created.name}.`)
    } catch (error) {
      showApiError(error)
    }
  }

  function openRenameUserDialog() {
    if (!currentUser.value) {
      return
    }

    renameUserName.value = currentUser.value.name
    renameUserDialogVisible.value = true
  }

  async function submitRenameUser() {
    if (!currentUser.value) {
      return
    }

    const nextName = renameUserName.value.trim()
    if (!nextName) {
      ElMessage.warning('User name cannot be blank.')
      return
    }

    try {
      const updated = await renameUser(currentUser.value.id, nextName)
      renameUserDialogVisible.value = false
      await loadDashboard(currentUser.value.id)
      ElMessage.success(`Renamed user to ${updated.name}.`)
    } catch (error) {
      showApiError(error)
    }
  }

  async function removeUser() {
    if (!currentUser.value || users.value.length <= 1) {
      ElMessage.warning('At least one user must remain.')
      return
    }

    try {
      await ElMessageBox.confirm(`Delete ${currentUser.value.name}?`, 'Delete user', {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning',
      })
    } catch {
      return
    }

    try {
      await deleteUser(currentUser.value.id)
      await loadDashboard()
      ElMessage.success('User deleted.')
    } catch (error) {
      showApiError(error)
    }
  }

  return {
    createUserDialogVisible,
    renameUserDialogVisible,
    createUserName,
    renameUserName,
    openCreateUserDialog,
    submitCreateUser,
    openRenameUserDialog,
    submitRenameUser,
    removeUser,
  }
}
