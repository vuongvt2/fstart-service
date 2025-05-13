package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.discussion.CommentData;
import com.fstart.service.model.discussion.CommentForm;
import com.fstart.service.model.discussion.DiscussionDetailData;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.CommentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * CommentController
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
@RestController
@RequestMapping(value = "fs/api/v1/comment")
public class CommentController {

    private final CommentService commentService;
    private final AccessTokenService accessTokenService;

    public CommentController(final CommentService commentService,
                             final AccessTokenService accessTokenService) {
        this.commentService = commentService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommentData createComment(@Validated(CommentForm.GroupCreate.class) @RequestBody CommentForm commentForm,
                                     HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return commentService.createComment(commentForm, userId);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper updateComment(@Validated(CommentForm.GroupUpdate.class) @RequestBody CommentForm commentForm) {
        return commentService.updateComment(commentForm);
    }

    @GetMapping(value = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public DiscussionDetailData getAllCommentsByDiscussionId(@RequestParam(value = "discussionId") Long discussionId,
                                                             @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                             @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                             HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        Pageable pageable = PageRequest.of(page - 1, size);
        return commentService.getAllCommentsByDiscussionId(discussionId, userId, pageable);
    }

    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper deleteComment(@RequestParam(value = "id") Long id,
                                     HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return commentService.deleteComment(id, userId);
    }
}
