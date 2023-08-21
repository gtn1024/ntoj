export function hasAdminPermissions(role?: UserRole): boolean {
  return role === 'ADMIN' || role === 'SUPER_ADMIN'
}
