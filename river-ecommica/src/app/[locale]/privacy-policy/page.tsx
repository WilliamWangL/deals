import { getTranslations } from 'next-intl/server';
import { PageHero } from '@/components/layout/PageHero';
import { LegalPageLayout } from '@/components/layout/LegalPageLayout';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });
  return {
    title: t('privacy.meta.title'),
    description: t('privacy.meta.description'),
  };
}

export default async function PrivacyPolicyPage({ params }: { params: Promise<{ locale: string }> }) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });

  const sections = [
    {
      id: 'intro',
      title: t('privacy.sections.intro.title'),
      content: t('privacy.sections.intro.content'),
    },
    {
      id: 'collection',
      title: t('privacy.sections.collection.title'),
      content: t('privacy.sections.collection.content'),
    },
    {
      id: 'usage',
      title: t('privacy.sections.usage.title'),
      content: t('privacy.sections.usage.content'),
    },
    {
      id: 'affiliates',
      title: t('privacy.sections.affiliates.title'),
      content: t('privacy.sections.affiliates.content'),
    },
    {
      id: 'rights',
      title: t('privacy.sections.rights.title'),
      content: t('privacy.sections.rights.content'),
    },
    {
      id: 'contact',
      title: t('privacy.sections.contact.title'),
      content: t('privacy.sections.contact.content'),
    },
  ];

  return (
    <div className="min-h-screen bg-background">
      <PageHero
        title={t('privacy.title')}
        variant="light"
        size="compact"
        date={t('lastUpdated', { date: 'January 2026' })}
      />
      <LegalPageLayout
        title={t('privacy.title')}
        lastUpdated={t('lastUpdated', { date: 'January 2026' })}
        tocLabel={t('tableOfContents')}
        relatedLinksLabel={t('relatedLinks')}
        sections={sections}
        currentPage="privacy"
        relatedPages={{
          privacy: t('privacy.title'),
          terms: t('terms.title'),
          cookies: t('cookies.title'),
        }}
      />
    </div>
  );
}
