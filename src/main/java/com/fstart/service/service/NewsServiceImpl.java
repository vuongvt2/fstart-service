package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ExistenceException;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.logging.AppLogger;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.IDGenerator;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.component.S3Component;
import com.fstart.service.entity.News;
import com.fstart.service.entity.User;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.home.HomeNewsData;
import com.fstart.service.model.news.NewsData;
import com.fstart.service.model.news.NewsFileForm;
import com.fstart.service.model.news.NewsFormCreate;
import com.fstart.service.model.news.NewsFormUpdate;
import com.fstart.service.model.user.BaseUserData;
import com.fstart.service.repository.NewsRepository;
import com.fstart.service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * NewsServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/03/03
 */
@Service
public class NewsServiceImpl implements NewsService {

    private final Message message;

    private final S3Component s3Component;

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public NewsServiceImpl(final Message message,
                           final S3Component s3Component,
                           final NewsRepository newsRepository,
                           final UserRepository userRepository) {
        this.message = message;
        this.s3Component = s3Component;
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper createNews(final String userId, final NewsFormCreate newsFormCreate) {
        User user = userRepository.getById(userId);
        if (!ERole.ADMIN.equals(user.getRole().getId())) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        String id = "";
        do {
            id = IDGenerator.generateID(newsRepository, 10);
        } while (newsRepository.existsById(id));
        News news = News.builder()
                .id(id)
                .title(newsFormCreate.getTitle())
                .shortDescription(newsFormCreate.getShortDescription())
                .description(newsFormCreate.getDescription())
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .tags(newsFormCreate.getTags())
                .user(user)
                .build();
        try {
            String thumbnail = s3Component.upload("news/" + news.getId() + "/thumbnail", newsFormCreate.getThumbnail());
            news.setThumbnail(thumbnail);
            newsRepository.save(news);
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }

        return DataWrapper.builder()
                .data(news.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<HomeNewsData> getAllNewsBy(final String title, final Pageable pageable) {
        Page<News> newsList;
        if (StringUtils.hasLength(title)) {
            newsList = newsRepository.getAllNewsByTitle(title, pageable);
        } else {
            newsList = newsRepository.getAllNews(pageable);
        }
        List<HomeNewsData> newsDataList = newsList.stream()
                .map(news -> {
                    HomeNewsData homeNewsData = DataBuilder.to(news, HomeNewsData.class);
                    if (StringUtils.hasLength(news.getTags())) {
                        homeNewsData.setTags(Arrays.asList(news.getTags().split(",")));
                    }
                    return homeNewsData;
                }).collect(Collectors.toList());
        return new PagedResponse<>(newsDataList, newsList.getNumber(), newsList.getSize(), newsList.getTotalElements(), newsList.getTotalPages());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper deleteNewsById(final String id, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!ERole.ADMIN.equals(user.getRole().getId())) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        newsRepository.delete(news);
        return DataWrapper.builder()
                .data(id)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<HomeNewsData> getAllNewsByTag(final String tagNameList, final Pageable pageable) {
        Page<News> newsList = newsRepository.getAllNewsByTag(tagNameList, pageable);
        List<HomeNewsData> newsDataList = newsList.stream()
                .map(news -> {
                    HomeNewsData homeNewsData = DataBuilder.to(news, HomeNewsData.class);
                    if (StringUtils.hasLength(news.getTags())) {
                        homeNewsData.setTags(Arrays.asList(news.getTags().split(",")));
                    }
                    return homeNewsData;
                }).collect(Collectors.toList());
        return new PagedResponse<>(newsDataList, newsList.getNumber(), newsList.getSize(), newsList.getTotalElements(), newsList.getTotalPages());
    }

    @Override
    public NewsData getNewsDetail(final String id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        NewsData newsData = DataBuilder.to(news, NewsData.class);
        BaseUserData baseUserData = DataBuilder.to(news.getUser(), BaseUserData.class);
        if (StringUtils.hasLength(news.getTags())) {
            newsData.setTags(Arrays.asList(news.getTags().split(",")));
        }
        newsData.setUser(baseUserData);
        return newsData;
    }

    @Override
    public DataWrapper updateNews(final String role, final NewsFormUpdate newsFormUpdate) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        News news = newsRepository.findById(newsFormUpdate.getId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        news.setTitle(newsFormUpdate.getTitle());
        news.setShortDescription(newsFormUpdate.getShortDescription());
        news.setDescription(newsFormUpdate.getDescription());
        news.setTags(newsFormUpdate.getTags());

        if (Objects.nonNull(newsFormUpdate.getThumbnail())) {
            try {
                String fileName = news.getThumbnail().substring(news.getThumbnail().lastIndexOf("/") + 1);
                s3Component.delete("news/" + news.getId() + "/thumbnail", fileName);
                String thumbnail = s3Component.upload("news/" + news.getId() + "/thumbnail", newsFormUpdate.getThumbnail());
                news.setThumbnail(thumbnail);
            } catch (IOException | URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
        }
        newsRepository.save(news);

        return DataWrapper.builder()
                .data(newsFormUpdate.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public List<String> uploadFile(final String userId, final NewsFileForm newsFileForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        News news = newsRepository.findById(newsFileForm.getNewsId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!user.equals(news.getUser())) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        MultipartFile[] files = newsFileForm.getFiles();
        List<String> listFiles = new ArrayList<>();
        if (Objects.nonNull(files) && files.length != 0) {
            try {
                for (var i = 0; i < files.length; i++) {
                    String file = s3Component.upload("news/" + news.getId() + "/files", files[i]);
                    listFiles.add(file);
                }
            } catch (IOException | URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
        }

        return listFiles;
    }
}