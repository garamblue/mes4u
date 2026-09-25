/**
 * Whether a user with `role` may see a route.
 * A route without `meta.roles` is open to every logged-in user.
 * @param {string} role the user's role, e.g. 'ROLE_ADMIN'
 * @param {Object} meta route meta (`meta.roles`: allowed roles)
 * @returns {Boolean}
 */
export function hasRoleAccess(role, meta) {
  if (!meta || !meta.roles || meta.roles.length === 0) {
    return true
  }
  return meta.roles.includes(role)
}
