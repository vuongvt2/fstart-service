package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.entity.News;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.home.HomeNewsData;
import com.fstart.service.model.news.NewsData;
import com.fstart.service.model.news.NewsFileForm;
import com.fstart.service.model.news.NewsFormCreate;
import com.fstart.service.model.news.NewsFormUpdate;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.NewsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * NewsController
 *
 * @author: VuongVT2
 * @since: 2022/03/03
 */
@RestController
@RequestMapping(value = "fs/api/v1/news")
public class NewsController {

    private final AccessTokenService accessTokenService;
    private final NewsService newsService;

    public NewsController(final AccessTokenService accessTokenService,
                          final NewsService newsService) {
        this.accessTokenService = accessTokenService;
        this.newsService = newsService;
    }

    @PostMapping(value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataWrapper createNews(@Valid NewsFormCreate newsFormCreate,
                                  HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return newsService.createNews(userId, newsFormCreate);
    }

    @PostMapping(value = "/update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataWrapper updateNews(@Valid NewsFormUpdate newsFormUpdate,
                                  HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return newsService.updateNews(role, newsFormUpdate);
    }

    @GetMapping(value = "/public/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<HomeNewsData> filterNewsBy(@RequestParam(name = "title", defaultValue = AppConstant.DEFAULT_STR_VALUE) String title,
                                                    @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                    @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return newsService.getAllNewsBy(title, pageable);
    }

    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper deleteNewsById(@RequestParam(name = "id") @NotBlank String id,
                                      HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return newsService.deleteNewsById(id, userId);
    }

    @GetMapping(value = "/public/news-by-tag", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<HomeNewsData> getAllNewsByTag(@RequestParam(name = "tag") @NotBlank String tagNameList,
                                          @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                          @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return newsService.getAllNewsByTag(tagNameList, pageable);
    }

    @GetMapping(value = "/public/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public NewsData getNewsDetail(@RequestParam(name = "id") @NotBlank String id) {
        return newsService.getNewsDetail(id);
    }

    @PostMapping(value = "/upload-file", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> uploadFile(@Valid NewsFileForm newsFileForm,
                                   HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return newsService.uploadFile(userId, newsFileForm);
    }
}
