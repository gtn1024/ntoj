import '@/config/envConfig.ts'

export const siteConfig = {
  name: "NTOJ",
  url: process.env.NEXT_PUBLIC_APP_URL || "http://localhost:2024",
  description: "NTOJ",
}

export type SiteConfig = typeof siteConfig
