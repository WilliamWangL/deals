const LOGO_COLORS = ['0D9488', '3B82F6', 'F59E0B', 'EF4444', '8B5CF6', '10B981', 'EC4899', '6366F1']

export const getMerchantLogo = (name: string): string => {
  const initials = name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase()
  const colorIndex = name.length % LOGO_COLORS.length
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(initials)}&background=${LOGO_COLORS[colorIndex]}&color=fff&size=200&bold=true&format=svg`
}
