import createNextIntlPlugin from 'next-intl/plugin';
import type { NextConfig } from 'next';

const withNextIntl = createNextIntlPlugin();

const nextConfig: NextConfig = {
  output: 'standalone',
  turbopack: {
    root: __dirname,
  },
  images: {
    dangerouslyAllowSVG: true,
    contentDispositionType: 'attachment',
    contentSecurityPolicy: "default-src 'self'; script-src 'none'; sandbox;",
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
      },
      {
        protocol: 'https',
        hostname: 'ui-avatars.com',
      },
      {
        protocol: 'https',
        hostname: '*.unsplash.com',
      },
      {
        protocol: 'https',
        hostname: 'picsum.photos',
      },
      {
        protocol: 'https',
        hostname: 'api.dicebear.com',
      },
      {
        protocol: 'https',
        hostname: 'cdn.admitad.com',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'cdn.admitad-connect.com',
        pathname: '/**',
      },
      {
        protocol: 'http',
        hostname: 'cdn.admitad.com',
        pathname: '/**',
      },
      {
        protocol: 'http',
        hostname: 'cdn.admitad-connect.com',
        pathname: '/**',
      },
    ],
  },
};

export default withNextIntl(nextConfig);
