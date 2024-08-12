package com.softuni.easypeasyrecipes_app.service.impl;

import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.entity.Comment;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec headersUriSpec;

    @Mock
    private RestClient.RequestBodyUriSpec bodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllComments() {
        CommentDto[] mockComments = {new CommentDto(), new CommentDto()};
        when(restClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri("/api/comments")).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CommentDto[].class)).thenReturn(mockComments);

        List<CommentDto> comments = commentService.getAllComments();
        assertEquals(2, comments.size());
        verify(restClient, times(1)).get();
        verify(headersUriSpec, times(1)).uri("/api/comments");
        verify(headersUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(CommentDto[].class);
    }

    @Test
    public void testAddComment() {
        CommentDto mockCommentDto = new CommentDto();
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri("/api/comments/recipe/{recipeId}", 1L)).thenReturn(bodyUriSpec);
        when(bodyUriSpec.body(mockCommentDto)).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CommentDto.class)).thenReturn(mockCommentDto);

        CommentDto addedComment = commentService.addComment(1L, mockCommentDto);
        assertEquals(mockCommentDto, addedComment);
        verify(restClient, times(1)).post();
        verify(bodyUriSpec, times(1)).uri("/api/comments/recipe/{recipeId}", 1L);
        verify(bodyUriSpec, times(1)).body(mockCommentDto);
        verify(bodyUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(CommentDto.class);
    }

    @Test
    public void testDeleteComment() {
        when(restClient.delete()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri("/api/comments/{id}", 1L)).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        commentService.deleteComment(1L);
        verify(restClient, times(1)).delete();
        verify(headersUriSpec, times(1)).uri("/api/comments/{id}", 1L);
        verify(headersUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(Void.class);
    }

    @Test
    public void testUpdateComment() {
        CommentDto mockCommentDto = new CommentDto();
        when(restClient.put()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri("/api/comments/{id}", 1L)).thenReturn(bodyUriSpec);
        when(bodyUriSpec.body(mockCommentDto)).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CommentDto.class)).thenReturn(mockCommentDto);

        CommentDto updatedComment = commentService.updateComment(1L, mockCommentDto);
        assertEquals(mockCommentDto, updatedComment);
        verify(restClient, times(1)).put();
        verify(bodyUriSpec, times(1)).uri("/api/comments/{id}", 1L);
        verify(bodyUriSpec, times(1)).body(mockCommentDto);
        verify(bodyUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(CommentDto.class);
    }

    @Test
    public void testGetCommentById() {
        CommentDto mockCommentDto = new CommentDto();
        when(restClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri("/api/comments/{id}", 1L)).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CommentDto.class)).thenReturn(mockCommentDto);

        CommentDto fetchedComment = commentService.getCommentById(1L);
        assertEquals(mockCommentDto, fetchedComment);
        verify(restClient, times(1)).get();
        verify(headersUriSpec, times(1)).uri("/api/comments/{id}", 1L);
        verify(headersUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(CommentDto.class);
    }

    @Test
    public void testGetRecipeIdByCommentId() {
        Recipe mockRecipe = new Recipe();
        mockRecipe.setId(1L);
        Comment mockComment = new Comment();
        mockComment.setRecipe(mockRecipe);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));

        Long recipeId = commentService.getRecipeIdByCommentId(1L);
        assertEquals(1L, recipeId);
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetRecipeIdByCommentIdNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            commentService.getRecipeIdByCommentId(1L);
        });
        verify(commentRepository, times(1)).findById(1L);
    }
    @Test
    public void testGetCommentByIdThrowsHttpClientErrorException() {
        when(restClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri("/api/comments/{id}", 1L)).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CommentDto.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class, () -> {
            commentService.getCommentById(1L);
        });
    }
    @Test
    public void testCountComments() {
        when(commentRepository.count()).thenReturn(5L);

        long count = commentService.countComments();

        assertEquals(5L, count);
        verify(commentRepository, times(1)).count();
    }
}
