export function collectSubmenus(items) {
  return items.flatMap((item) => {
    if (item.type !== 'SUBMENU') {
      return []
    }

    return [item, ...collectSubmenus(item.children ?? [])]
  })
}
