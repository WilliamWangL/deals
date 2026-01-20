'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { cn } from '@/lib/utils';
import { FileText, Shield, Cookie } from 'lucide-react';

interface Section {
  id: string;
  title: string;
  content: string;
}

interface RelatedLink {
  href: string;
  label: string;
  icon: React.ElementType;
  active?: boolean;
}

interface RelatedLinkItemProps {
  link: RelatedLink;
  variant: 'sidebar' | 'mobile';
}

function RelatedLinkItem({ link, variant }: RelatedLinkItemProps): React.ReactElement {
  const Icon = link.icon;
  const isSidebar = variant === 'sidebar';

  return (
    <Link
      href={link.href}
      className={cn(
        'flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors',
        isSidebar
          ? link.active
            ? 'bg-muted text-foreground font-medium'
            : 'text-muted-foreground hover:text-foreground hover:bg-muted/50'
          : link.active
            ? 'bg-muted text-foreground font-medium gap-2 px-4'
            : 'bg-muted/50 text-muted-foreground hover:bg-muted hover:text-foreground gap-2 px-4'
      )}
    >
      <Icon className="w-4 h-4" />
      <span>{link.label}</span>
    </Link>
  );
}

interface LegalPageLayoutProps {
  title: string;
  lastUpdated: string;
  tocLabel: string;
  relatedLinksLabel: string;
  sections: Section[];
  currentPage: 'privacy' | 'terms' | 'cookies';
  relatedPages: {
    privacy: string;
    terms: string;
    cookies: string;
  };
}

export function LegalPageLayout({
  title,
  lastUpdated,
  tocLabel,
  relatedLinksLabel,
  sections,
  currentPage,
  relatedPages,
}: LegalPageLayoutProps) {
  const params = useParams();
  const locale = params.locale as string;
  const [activeSection, setActiveSection] = useState(sections[0]?.id || '');

  // Track scroll position to highlight active section
  useEffect(() => {
    const handleScroll = () => {
      const sectionElements = sections.map((section) => ({
        id: section.id,
        element: document.getElementById(section.id),
      }));

      for (let i = sectionElements.length - 1; i >= 0; i--) {
        const { id, element } = sectionElements[i];
        if (element) {
          const rect = element.getBoundingClientRect();
          if (rect.top <= 150) {
            setActiveSection(id);
            break;
          }
        }
      }
    };

    window.addEventListener('scroll', handleScroll);
    handleScroll(); // Initial check
    return () => window.removeEventListener('scroll', handleScroll);
  }, [sections]);

  const relatedLinks: RelatedLink[] = [
    {
      href: `/${locale}/privacy-policy`,
      label: relatedPages.privacy,
      icon: Shield,
      active: currentPage === 'privacy',
    },
    {
      href: `/${locale}/terms-of-service`,
      label: relatedPages.terms,
      icon: FileText,
      active: currentPage === 'terms',
    },
    {
      href: `/${locale}/cookie-policy`,
      label: relatedPages.cookies,
      icon: Cookie,
      active: currentPage === 'cookies',
    },
  ];

  return (
    <div className="py-12 lg:py-16">
      <div className="container mx-auto px-4">
        <div className="max-w-6xl mx-auto">
          {/* Mobile TOC - Horizontal Scroll */}
          <div className="lg:hidden mb-8 -mx-4 px-4">
            <div className="text-sm font-medium text-muted-foreground mb-3">{tocLabel}</div>
            <div className="flex gap-2 overflow-x-auto pb-2 no-scrollbar">
              {sections.map((section) => (
                <a
                  key={section.id}
                  href={`#${section.id}`}
                  className={cn(
                    'flex-shrink-0 px-4 py-2 rounded-lg text-sm font-medium transition-colors whitespace-nowrap',
                    activeSection === section.id
                      ? 'bg-primary text-primary-foreground'
                      : 'bg-muted/50 text-muted-foreground hover:bg-muted hover:text-foreground'
                  )}
                >
                  {section.title}
                </a>
              ))}
            </div>
          </div>

          <div className="flex gap-12 lg:gap-16">
            {/* Sidebar - Desktop Only */}
            <aside className="hidden lg:block w-56 flex-shrink-0">
              <div className="sticky top-24">
                {/* Table of Contents */}
                <div className="mb-8">
                  <div className="text-sm font-semibold text-foreground mb-4">{tocLabel}</div>
                  <nav className="space-y-1">
                    {sections.map((section, index) => (
                      <a
                        key={section.id}
                        href={`#${section.id}`}
                        className={cn(
                          'flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-all',
                          activeSection === section.id
                            ? 'bg-primary/10 text-primary font-medium border-l-2 border-primary -ml-[2px]'
                            : 'text-muted-foreground hover:text-foreground hover:bg-muted/50'
                        )}
                      >
                        <span
                          className={cn(
                            'w-5 h-5 rounded-full flex items-center justify-center text-xs font-semibold',
                            activeSection === section.id
                              ? 'bg-primary text-primary-foreground'
                              : 'bg-muted text-muted-foreground'
                          )}
                        >
                          {index + 1}
                        </span>
                        <span className="truncate">{section.title}</span>
                      </a>
                    ))}
                  </nav>
                </div>

                {/* Related Links */}
                <div>
                  <div className="text-sm font-semibold text-foreground mb-4">{relatedLinksLabel}</div>
                  <nav className="space-y-1">
                    {relatedLinks.map((link) => (
                      <RelatedLinkItem key={link.href} link={link} variant="sidebar" />
                    ))}
                  </nav>
                </div>
              </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 min-w-0">
              {/* Header */}
              <div className="mb-10">
                <h1 className="text-3xl lg:text-4xl font-display font-bold text-foreground mb-3">
                  {title}
                </h1>
                <p className="text-muted-foreground">{lastUpdated}</p>
              </div>

              {/* Sections */}
              <div className="space-y-12">
                {sections.map((section, index) => (
                  <section key={section.id} id={section.id} className="scroll-mt-24">
                    <div className="flex items-center gap-3 mb-4">
                      <span className="w-8 h-8 rounded-lg bg-gradient-to-br from-amber-500 to-orange-600 flex items-center justify-center text-white font-bold text-sm shadow-lg shadow-amber-500/20">
                        {index + 1}
                      </span>
                      <h2 className="text-xl lg:text-2xl font-display font-bold text-foreground">
                        {section.title}
                      </h2>
                    </div>
                    <div className="text-muted-foreground leading-relaxed pl-11">
                      {section.content}
                    </div>
                  </section>
                ))}
              </div>

              {/* Mobile Related Links */}
              <div className="lg:hidden mt-12 pt-8 border-t border-border">
                <div className="text-sm font-semibold text-foreground mb-4">{relatedLinksLabel}</div>
                <div className="flex flex-wrap gap-2">
                  {relatedLinks.map((link) => (
                    <RelatedLinkItem key={link.href} link={link} variant="mobile" />
                  ))}
                </div>
              </div>
            </main>
          </div>
        </div>
      </div>
    </div>
  );
}
