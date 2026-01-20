import { Metadata } from 'next';
import Link from 'next/link';
import Image from 'next/image';
import { getTranslations } from 'next-intl/server';
import { fetchPosts } from '@/lib/api';
import { PAGINATION } from '@/constants/pagination';
import { Card, CardContent } from '@/components/ui/card';
import { BlogPagination } from '@/components/blog/BlogPagination';
import { EmptyState } from '@/components/ui/empty-state';
import {
  BookOpen,
  Sparkles,
  FileText,
  TrendingUp,
  Clock,
  ArrowRight,
  Star,
  User,
  Calendar
} from 'lucide-react';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'blog' });

  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function BlogPage({
  params,
  searchParams
}: {
  params: Promise<{ locale: string }>;
  searchParams: Promise<{ page?: string }>
}) {
  const { locale } = await params;
  const queryParams = await searchParams;
  const currentPage = parseInt(queryParams.page || String(PAGINATION.DEFAULT_PAGE), 10);
  const pageSize = PAGINATION.PAGE_SIZE.BLOG;

  const { list: posts, total } = await fetchPosts({
    pageNo: currentPage,
    pageSize
  });

  const totalPosts = total;
  const featuredCount = posts.filter(p => p.featured).length;

  const getTypeConfig = (type: string) => {
    const configs: Record<string, { label: string; color: string; bg: string }> = {
      deal: { label: 'Deal', color: 'text-amber-600', bg: 'bg-amber-50 border-amber-100' },
      review: { label: 'Review', color: 'text-violet-600', bg: 'bg-violet-50 border-violet-100' },
      tutorial: { label: 'Tutorial', color: 'text-emerald-600', bg: 'bg-emerald-50 border-emerald-100' },
      news: { label: 'News', color: 'text-blue-600', bg: 'bg-blue-50 border-blue-100' }
    };
    return configs[type] || { label: type, color: 'text-muted-foreground', bg: 'bg-muted border-border' };
  };

  return (
    <main className="min-h-screen bg-background">
      {/* Hero Header */}
      <section className="page-header py-12 md:py-16">
        {/* Background decoration */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-20 -right-20 w-96 h-96 bg-gradient-to-br from-blue-200/30 to-indigo-200/30 rounded-full blur-3xl" />
          <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-gradient-to-br from-violet-200/20 to-purple-200/20 rounded-full blur-3xl" />
        </div>

        <div className="container mx-auto px-4 relative">
          <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-8">
            {/* Title Section */}
            <div className="max-w-2xl">
              <div className="flex items-center gap-3 mb-5">
                <div className="p-3 rounded-2xl bg-gradient-to-br from-blue-100 to-indigo-100">
                  <BookOpen className="w-8 h-8 text-blue-600" />
                </div>
                <div className="badge-featured">
                  <Sparkles className="w-3.5 h-3.5" />
                  Fresh Content
                </div>
              </div>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-display font-bold text-foreground tracking-tight mb-4">
                Blog & Guides
              </h1>
              <p className="text-lg md:text-xl text-muted-foreground leading-relaxed">
                Tips, tutorials, reviews, and the latest deals news to help you save more on every purchase.
              </p>
            </div>

            {/* Stats Cards */}
            <div className="flex gap-4 md:gap-6 flex-wrap lg:flex-nowrap">
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <FileText className="w-4 h-4" />
                  <span>Articles</span>
                </div>
                <span className="stat-value">{totalPosts.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Star className="w-4 h-4 text-amber-500" />
                  <span>Featured</span>
                </div>
                <span className="stat-value text-gradient-primary">{featuredCount.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <TrendingUp className="w-4 h-4 text-emerald-500" />
                  <span>This Week</span>
                </div>
                <span className="stat-value">New</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Posts Grid */}
      <section className="container mx-auto px-4 py-10">
        {posts.length > 0 ? (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {posts.map(post => {
                const typeConfig = getTypeConfig(post.type);
                return (
                  <Link key={post.id} href={`/${locale}/blog/${post.slug}`} className="group block h-full">
                    <Card className="h-full overflow-hidden bg-card border-border/50 transition-all duration-300 hover:shadow-xl hover:shadow-primary/[0.08] hover:-translate-y-1 hover:border-primary/20 rounded-2xl">
                      {/* Cover Image */}
                      <div className="relative h-48 bg-gradient-to-br from-slate-100 via-slate-50 to-slate-100 overflow-hidden">
                        {post.coverImage ? (
                          <Image
                            src={post.coverImage}
                            alt={post.title}
                            fill
                            className="object-cover transition-transform duration-500 group-hover:scale-105"
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center">
                            <BookOpen className="w-12 h-12 text-muted-foreground/20" />
                          </div>
                        )}

                        {/* Overlay gradient */}
                        <div className="absolute inset-0 bg-gradient-to-t from-black/40 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />

                        {/* Badges */}
                        <div className="absolute top-3 left-3 z-10 flex items-center gap-2">
                          <span className={`px-2.5 py-1 rounded-full text-xs font-semibold border ${typeConfig.bg} ${typeConfig.color}`}>
                            {typeConfig.label}
                          </span>
                          {post.featured && (
                            <div className="badge-featured">
                              <Star className="w-3 h-3" />
                              Featured
                            </div>
                          )}
                        </div>

                        {/* Hover arrow indicator */}
                        <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-all duration-300 transform translate-x-2 group-hover:translate-x-0 z-10">
                          <div className="bg-primary/10 p-2 rounded-full backdrop-blur-sm">
                            <ArrowRight className="w-4 h-4 text-primary" />
                          </div>
                        </div>
                      </div>

                      <CardContent className="p-5">
                        {/* Title */}
                        <h2 className="font-display font-bold text-lg mb-2 text-foreground group-hover:text-primary transition-colors line-clamp-2 min-h-[3.5rem]">
                          {post.title}
                        </h2>

                        {/* Excerpt */}
                        <p className="text-sm text-muted-foreground mb-4 line-clamp-2">
                          {post.excerpt}
                        </p>

                        {/* Author & Date */}
                        <div className="flex items-center justify-between text-xs text-muted-foreground pt-4 border-t border-border/50">
                          <div className="flex items-center gap-1.5">
                            <User className="w-3.5 h-3.5" />
                            <span className="font-medium">{post.authorName}</span>
                          </div>
                          <div className="flex items-center gap-1.5">
                            <Calendar className="w-3.5 h-3.5" />
                            <span>{new Date(post.publishedAt).toLocaleDateString()}</span>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  </Link>
                );
              })}
            </div>
            <div className="mt-12">
              <BlogPagination total={total} pageSize={pageSize} currentPage={currentPage} />
            </div>
          </>
        ) : (
          <EmptyState
            icon="book"
            title="No posts found"
            description="We don't have any blog posts yet. Check back later for tips, guides, and deals news."
          />
        )}
      </section>
    </main>
  );
}
