'use client';

import { cn } from '@/lib/utils';
import { Handshake, Globe, Zap, TrendingUp, Award } from 'lucide-react';

// Affiliate networks with brand colors
const affiliateNetworks = [
  { name: 'ShareASale', color: 'from-green-500 to-emerald-600', initial: 'S' },
  { name: 'CJ Affiliate', color: 'from-blue-600 to-indigo-700', initial: 'CJ' },
  { name: 'Rakuten', color: 'from-red-500 to-rose-600', initial: 'R' },
  { name: 'Impact', color: 'from-violet-500 to-purple-600', initial: 'I' },
  { name: 'Awin', color: 'from-cyan-500 to-blue-600', initial: 'A' },
  { name: 'FlexOffers', color: 'from-orange-500 to-amber-600', initial: 'F' },
  { name: 'Pepperjam', color: 'from-pink-500 to-rose-600', initial: 'P' },
  { name: 'AvantLink', color: 'from-teal-500 to-cyan-600', initial: 'AL' },
  { name: 'Refersion', color: 'from-indigo-500 to-violet-600', initial: 'R' },
  { name: 'PartnerStack', color: 'from-slate-500 to-slate-700', initial: 'PS' },
];

// Duplicate for seamless loop
const networks = [...affiliateNetworks, ...affiliateNetworks];

export function AffiliateNetworks() {
  return (
    <section className="relative py-12 lg:py-16 overflow-hidden">
      {/* Background - matches the overall dark theme */}
      <div className="absolute inset-0 bg-gradient-to-b from-slate-900 via-slate-900 to-slate-950" />

      {/* Subtle grid pattern */}
      <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.015)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.015)_1px,transparent_1px)] bg-[size:60px_60px]" />

      {/* Glow effects */}
      <div className="absolute top-1/2 left-1/4 -translate-y-1/2 w-[400px] h-[400px] bg-indigo-500/10 rounded-full blur-[100px]" />
      <div className="absolute top-1/2 right-1/4 -translate-y-1/2 w-[300px] h-[300px] bg-violet-500/10 rounded-full blur-[80px]" />

      <div className="container mx-auto px-4 relative">
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-14">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/5 border border-white/10 text-sm font-medium mb-6">
            <Handshake className="w-4 h-4 text-amber-400" />
            <span className="text-white/80">Trusted Partners</span>
          </div>
          <h2 className="text-3xl lg:text-4xl font-display font-bold text-white mb-4">
            Powered by Leading Networks
          </h2>
          <p className="text-slate-400 text-lg">
            We partner with the world's top affiliate networks to bring you verified deals
          </p>
        </div>

        {/* Marquee Container */}
        <div className="relative mb-16 overflow-hidden">
          {/* Gradient Masks */}
          <div className="absolute left-0 top-0 bottom-0 w-24 md:w-40 bg-gradient-to-r from-slate-900 to-transparent z-10 pointer-events-none" />
          <div className="absolute right-0 top-0 bottom-0 w-24 md:w-40 bg-gradient-to-l from-slate-900 to-transparent z-10 pointer-events-none" />

          {/* Scrolling Row */}
          <div className="inline-flex animate-marquee hover:[animation-play-state:paused]">
            {networks.map((network, index) => (
              <div
                key={`${network.name}-${index}`}
                className="flex-shrink-0 mx-3 group"
              >
                <div className={cn(
                  "flex items-center gap-4",
                  "w-52 md:w-60 h-20 px-5",
                  "bg-white/[0.03] rounded-xl",
                  "border border-white/[0.06]",
                  "transition-all duration-300",
                  "hover:bg-white/[0.06] hover:border-white/[0.12]"
                )}>
                  {/* Colored Icon */}
                  <div className={cn(
                    "w-11 h-11 rounded-lg flex items-center justify-center flex-shrink-0",
                    "bg-gradient-to-br shadow-lg",
                    network.color
                  )}>
                    <span className="text-white font-bold text-sm">{network.initial}</span>
                  </div>

                  {/* Name */}
                  <div className="min-w-0">
                    <span className="text-white font-medium text-base block truncate">
                      {network.name}
                    </span>
                    <span className="text-slate-500 text-xs">Partner Network</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-6 max-w-4xl mx-auto">
          {[
            { icon: Globe, value: '10+', label: 'Networks', color: 'text-blue-400', bg: 'bg-blue-500/10' },
            { icon: Zap, value: '50K+', label: 'Active Offers', color: 'text-amber-400', bg: 'bg-amber-500/10' },
            { icon: TrendingUp, value: '$2M+', label: 'User Savings', color: 'text-emerald-400', bg: 'bg-emerald-500/10' },
            { icon: Award, value: '99%', label: 'Verified', color: 'text-violet-400', bg: 'bg-violet-500/10' },
          ].map(({ icon: Icon, value, label, color, bg }) => (
            <div
              key={label}
              className="p-5 md:p-6 rounded-xl bg-white/[0.02] border border-white/[0.05] text-center"
            >
              <div className={cn("inline-flex p-2.5 rounded-lg mb-3", bg)}>
                <Icon className={cn("w-5 h-5", color)} />
              </div>
              <div className="text-2xl md:text-3xl font-bold text-white mb-1">{value}</div>
              <div className="text-sm text-slate-500">{label}</div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default AffiliateNetworks;
