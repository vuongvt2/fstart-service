package com.fstart.service.service;

import com.fstart.service.entity.News;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.home.HomeNewsData;
import com.fstart.service.model.news.NewsData;
import com.fstart.service.model.news.NewsFileForm;
import com.fstart.service.model.news.NewsFormCreate;
import com.fstart.service.model.news.NewsFormUpdate;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * NewsService
 *
 * @author: VuongVT2
 * @since: 2022/03/03
 */
public interface NewsService {

    DataWrapper createNews(String userId, NewsFormCreate newsFormCreate);

    PagedResponse<HomeNewsData> getAllNewsBy(String title, Pageable pageable);

    DataWrapper deleteNewsById(String id, String userId);

    PagedResponse<HomeNewsData> getAllNewsByTag(String tagNameList, Pageable pageable);

    NewsData getNewsDetail(String id);

    DataWrapper updateNews(String role, NewsFormUpdate newsFormUpdate);

    List<String> uploadFile(String userId, NewsFileForm newsFileForm);
}
