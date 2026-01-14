import {NextIntlClientProvider} from 'next-intl';
import {getMessages} from 'next-intl/server';
import { notFound } from 'next/navigation';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import "@/app/globals.css";
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: {
    template: '%s | Ecommica',
    default: 'Ecommica - Best Deals & Coupons'
  },
  description: 'Find the best deals and coupons for your favorite stores.',
  openGraph: {
    type: 'website',
    locale: 'en_US',
    url: 'https://deals.ecommica.com',
    siteName: 'Ecommica',
  }
};

export default async function LocaleLayout({
  children,
  params
}: {
  children: React.ReactNode;
  params: Promise<{locale: string}>;
}) {
  const { locale } = await params;
  
  if (!['en', 'zh'].includes(locale)) {
    notFound();
  }
 
  const messages = await getMessages();
 
  return (
    <html lang={locale}>
      <body>
        <NextIntlClientProvider messages={messages}>
          <div className="min-h-screen flex flex-col">
            <Header />
            <main className="flex-grow">
               {children}
            </main>
            <Footer locale={locale} />
          </div>
        </NextIntlClientProvider>
      </body>
    </html>
  );
}
