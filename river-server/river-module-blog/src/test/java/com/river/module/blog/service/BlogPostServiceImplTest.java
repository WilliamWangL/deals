package com.river.module.blog.service;

import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.blog.dal.dataobject.BlogPostDO;
import com.river.module.blog.dal.mysql.BlogPostMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link BlogPostServiceImpl} 的单元测试
 */
class BlogPostServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private BlogPostServiceImpl blogPostService;

    @Mock
    private BlogPostMapper blogPostMapper;

    @Mock
    private AuthorService authorService;

    @Test
    void testCreatePost_Success() {
        // Given
        BlogPostDO post = new BlogPostDO();
        post.setTitle("Test Post");
        post.setSlug("test-post");
        post.setAuthorId(1L);
        doNothing().when(authorService).validateAuthorExists(eq(1L));
        when(blogPostMapper.selectBySlug(eq("test-post"))).thenReturn(null);
        when(blogPostMapper.insert(any(BlogPostDO.class))).thenReturn(1);

        // When
        Long result = blogPostService.createPost(post);

        // Then
        verify(authorService).validateAuthorExists(eq(1L));
        verify(blogPostMapper).insert(any(BlogPostDO.class));
    }

    @Test
    void testGetPost_Success() {
        // Given
        Long postId = 1L;
        BlogPostDO expectedPost = new BlogPostDO();
        expectedPost.setId(postId);
        expectedPost.setTitle("Test Post");
        when(blogPostMapper.selectById(eq(postId))).thenReturn(expectedPost);

        // When
       BlogPostDO result = blogPostService.getPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Test Post", result.getTitle());
        verify(blogPostMapper).selectById(eq(postId));
    }

    @Test
    void testGetPost_NotFound() {
        // Given
        Long postId = 999L;
        when(blogPostMapper.selectById(eq(postId))).thenReturn(null);

        // When
       BlogPostDO result = blogPostService.getPost(postId);

        // Then
        assertNull(result);
        verify(blogPostMapper).selectById(eq(postId));
    }

    @Test
    void testDeletePost_Success() {
        // Given
        Long postId = 1L;
       BlogPostDO existingPost = new BlogPostDO();
        existingPost.setId(postId);
        when(blogPostMapper.selectById(eq(postId))).thenReturn(existingPost);
        when(blogPostMapper.deleteById(eq(postId))).thenReturn(1);

        // When
        blogPostService.deletePost(postId);

        // Then
        verify(blogPostMapper).selectById(eq(postId));
        verify(blogPostMapper).deleteById(eq(postId));
    }

    @Test
    void testGetPostBySlug_Success() {
        // Given
        String slug = "test-post";
       BlogPostDO expectedPost = new BlogPostDO();
        expectedPost.setSlug(slug);
        expectedPost.setTitle("Test Post");
        when(blogPostMapper.selectBySlug(eq(slug))).thenReturn(expectedPost);

        // When
       BlogPostDO result = blogPostService.getPostBySlug(slug);

        // Then
        assertNotNull(result);
        assertEquals(slug, result.getSlug());
        verify(blogPostMapper).selectBySlug(eq(slug));
    }

}
