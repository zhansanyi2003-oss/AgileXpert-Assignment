import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadWallpaper } from '@/api/wallpaper'

export function useWallpaperUpload({ currentUser, loadDashboard, selectWallpaper, showApiError }) {
  const uploadWallpaperDialogVisible = ref(false)
  const pendingWallpaperFile = ref(null)
  const pendingWallpaperFileName = ref('')
  const wallpaperUploadName = ref('')

  function pickWallpaperFile(file) {
    if (!currentUser.value) {
      ElMessage.warning('Select a user first.')
      return
    }

    pendingWallpaperFile.value = file
    pendingWallpaperFileName.value = file.name
    wallpaperUploadName.value = file.name.replace(/\.[^.]+$/, '')
    uploadWallpaperDialogVisible.value = true
  }

  function resetWallpaperUploadDialog() {
    pendingWallpaperFile.value = null
    pendingWallpaperFileName.value = ''
    wallpaperUploadName.value = ''
  }

  async function submitWallpaperUpload() {
    if (!currentUser.value || !pendingWallpaperFile.value) {
      return
    }

    const wallpaperName = wallpaperUploadName.value.trim()
    if (!wallpaperName) {
      ElMessage.warning('Wallpaper name cannot be blank.')
      return
    }

    try {
      const uploaded = await uploadWallpaper(pendingWallpaperFile.value, wallpaperName)
      uploadWallpaperDialogVisible.value = false
      await loadDashboard(currentUser.value.id)
      await selectWallpaper(uploaded.id)
      ElMessage.success(`Uploaded ${uploaded.name}.`)
    } catch (error) {
      showApiError(error)
    }
  }

  return {
    uploadWallpaperDialogVisible,
    pendingWallpaperFileName,
    wallpaperUploadName,
    pickWallpaperFile,
    resetWallpaperUploadDialog,
    submitWallpaperUpload,
  }
}
