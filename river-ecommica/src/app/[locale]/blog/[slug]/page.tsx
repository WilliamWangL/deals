import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import Image from 'next/image';
import { fetchPosts, fetchPostBySlug } from '@/lib/api';
import { Badge } from '@/components/ui/badge';
import { JsonLd, generateBlogPostJsonLd } from '@/components/seo/JsonLd';

type Props = {
  params: Promise<{ locale: string; slug: string }>;
};

export async function generateStaticParams() {
  const { list: posts } = await fetchPosts();
  const locales = ['en', 'zh'];

  return locales.flatMap(locale =>
    posts
      .filter(post => post.slug && typeof post.slug === 'string')
      .map(post => ({
        locale,
        slug: post.slug,
      }))
  );
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { slug } = await params;
  const post = await fetchPostBySlug(slug);
  
  if (!post) {
    return { title: 'Post Not Found' };
  }
  
  return {
    title: post.metaTitle || post.title,
    description: post.metaDescription || post.excerpt,
  };
}

export default async function BlogPostPage({ params }: Props) {
  const { slug } = await params;
  const post = await fetchPostBySlug(slug);

  if (!post) {
    notFound();
  }

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
    <>
      <JsonLd data={generateBlogPostJsonLd(post)} />
      <main className="container mx-auto px-4 py-8 max-w-4xl">
      <article>
        {post.coverImage && (
          <div className="h-64 md:h-96 bg-gray-100 rounded-lg overflow-hidden mb-8 relative">
            <Image src={post.coverImage} alt={post.title} fill className="object-cover" />
          </div>
        )}

        <div className="flex items-center gap-2 mb-4">
          <Badge variant="secondary">{getTypeLabel(post.type)}</Badge>
          {post.featured && <Badge>Featured</Badge>}
        </div>

        <h1 className="text-4xl font-bold mb-4">{post.title}</h1>

        <div className="flex items-center gap-4 text-gray-600 mb-8 pb-8 border-b">
          <div className="flex items-center gap-2">
            {post.authorAvatar && (
              <Image src={post.authorAvatar} alt={post.authorName} width={32} height={32} className="rounded-full" />
            )}
            <span>{post.authorName}</span>
          </div>
          <span>•</span>
          <span>{new Date(post.publishedAt).toLocaleDateString()}</span>
          {post.viewCount && (
            <>
              <span>•</span>
              <span>{post.viewCount} views</span>
            </>
          )}
        </div>

        <div className="prose prose-lg max-w-none">
          {post.content ? (
            <div dangerouslySetInnerHTML={{ __html: post.content.replace(/\n/g, '<br/>') }} />
          ) : (
            <p>{post.excerpt}</p>
          )}
        </div>
      </article>
    </main>
    </>
  );
}
