package com.river.module.blog.service;

import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.blog.dal.dataobject.PostDO;
import com.river.module.blog.dal.mysql.PostMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link PostServiceImpl} 的单元测试
 */
class PostServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private AuthorService authorService;

    @Test
    void testCreatePost_Success() {
        // Given
        PostDO post = new PostDO();
        post.setTitle("Test Post");
        post.setSlug("test-post");
        post.setAuthorId(1L);
        doNothing().when(authorService).validateAuthorExists(eq(1L));
        when(postMapper.selectBySlug(eq("test-post"))).thenReturn(null);
        when(postMapper.insert(any(PostDO.class))).thenReturn(1);

        // When
        Long result = postService.createPost(post);

        // Then
        verify(authorService).validateAuthorExists(eq(1L));
        verify(postMapper).insert(any(PostDO.class));
    }

    @Test
    void testGetPost_Success() {
        // Given
        Long postId = 1L;
        PostDO expectedPost = new PostDO();
        expectedPost.setId(postId);
        expectedPost.setTitle("Test Post");
        when(postMapper.selectById(eq(postId))).thenReturn(expectedPost);

        // When
        PostDO result = postService.getPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Test Post", result.getTitle());
        verify(postMapper).selectById(eq(postId));
    }

    @Test
    void testGetPost_NotFound() {
        // Given
        Long postId = 999L;
        when(postMapper.selectById(eq(postId))).thenReturn(null);

        // When
        PostDO result = postService.getPost(postId);

        // Then
        assertNull(result);
        verify(postMapper).selectById(eq(postId));
    }

    @Test
    void testDeletePost_Success() {
        // Given
        Long postId = 1L;
        PostDO existingPost = new PostDO();
        existingPost.setId(postId);
        when(postMapper.selectById(eq(postId))).thenReturn(existingPost);
        when(postMapper.deleteById(eq(postId))).thenReturn(1);

        // When
        postService.deletePost(postId);

        // Then
        verify(postMapper).selectById(eq(postId));
        verify(postMapper).deleteById(eq(postId));
    }

    @Test
    void testGetPostBySlug_Success() {
        // Given
        String slug = "test-post";
        PostDO expectedPost = new PostDO();
        expectedPost.setSlug(slug);
        expectedPost.setTitle("Test Post");
        when(postMapper.selectBySlug(eq(slug))).thenReturn(expectedPost);

        // When
        PostDO result = postService.getPostBySlug(slug);

        // Then
        assertNotNull(result);
        assertEquals(slug, result.getSlug());
        verify(postMapper).selectBySlug(eq(slug));
    }

}
