import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';
import { fetchPosts } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'blog' });
  
  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function BlogPage({ params }: { params: Promise<{ locale: string }> }) {
  const { locale } = await params;
  const posts = await fetchPosts();

  const getTypeLabel = (type: string) => {
    const labels: Record<string, string> = {
      deal: 'Deal',
      review: 'Review',
      tutorial: 'Tutorial',
      news: 'News'
    };
    return labels[type] || type;
  };

  return (
    <main className="container mx-auto px-4 py-8">
      <section className="mb-8">
        <h1 className="text-3xl font-bold mb-2">Blog</h1>
        <p className="text-gray-600">Tips, guides, and the latest deals news</p>
      </section>

      <section>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {posts.length > 0 ? posts.map(post => (
            <Link key={post.id} href={`/${locale}/blog/${post.slug}`}>
              <Card className="h-full hover:shadow-lg transition-shadow overflow-hidden">
                {post.coverImage && (
                  <div className="h-48 bg-gray-100">
                    <img src={post.coverImage} alt={post.title} className="w-full h-full object-cover" />
                  </div>
                )}
                <CardContent className="p-4">
                  <div className="flex items-center gap-2 mb-2">
                    <Badge variant="secondary">{getTypeLabel(post.type)}</Badge>
                    {post.featured && <Badge>Featured</Badge>}
                  </div>
                  <h2 className="font-bold text-lg mb-2 line-clamp-2">{post.title}</h2>
                  <p className="text-gray-600 text-sm mb-3 line-clamp-2">{post.excerpt}</p>
                  <div className="flex items-center justify-between text-xs text-gray-500">
                    <span>{post.authorName}</span>
                    <span>{new Date(post.publishedAt).toLocaleDateString()}</span>
                  </div>
                </CardContent>
              </Card>
            </Link>
          )) : (
            <p className="col-span-3 text-center text-gray-500">No posts found</p>
          )}
        </div>
      </section>
    </main>
  );
}
