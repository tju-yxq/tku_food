// src/main/java/xyz/tjucomments/tjufood/service/impl/BlogServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.BlogDTO;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.entity.Blog;
import xyz.tjucomments.tjufood.entity.Notification;
import xyz.tjucomments.tjufood.entity.User;
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.mapper.BlogMapper;
import xyz.tjucomments.tjufood.service.IBlogService;
import xyz.tjucomments.tjufood.service.INotificationService;
import xyz.tjucomments.tjufood.service.IUserService;
import xyz.tjucomments.tjufood.utils.constants.IdPrefixConstants;
import xyz.tjucomments.tjufood.utils.constants.RedisConstants;
import xyz.tjucomments.tjufood.utils.constants.SystemConstants;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;
import xyz.tjucomments.tjufood.utils.security.XssUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IUserService userService;
    @Resource
    private INotificationService notificationService;
    @Resource
    private ExecutorService cacheRebuildExecutor;


    @Override
    @Transactional
    public Result createBlog(BlogDTO blogDTO) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录！");
        }

        // XSS防护：清理用户输入的内容
        if (XssUtils.containsXss(blogDTO.getTitle()) || XssUtils.containsXss(blogDTO.getContent())) {
            return Result.fail("内容包含非法字符，请检查后重新提交！");
        }

        Blog blog = BeanUtil.copyProperties(blogDTO, Blog.class);
        // 对内容进行HTML转义
        blog.setTitle(XssUtils.escapeHtml(blog.getTitle()));
        blog.setContent(XssUtils.escapeHtml(blog.getContent()));

        blog.setId(redisIdWorker.nextId(IdPrefixConstants.BLOG_ID_PREFIX));
        blog.setUserId(user.getId());

        boolean isSuccess = save(blog);
        if (!isSuccess) {
            return Result.fail("发布博客失败！");
        }

        return Result.ok(blog.getId());
    }

    /**
     * 【重要新增】补全缺失的 queryBlogById 方法
     * @param id 博客ID
     * @return 包含博客详情的Result
     */
    @Override
    public Result queryBlogById(Long id) {
        // 1. 使用我们之前在Mapper中定义的联表查询，获取博客和作者的基础信息
        BlogDTO blogDTO = baseMapper.queryBlogWithAuthor(id);
        if (blogDTO == null) {
            return Result.fail("博客不存在！");
        }
        // 2. 检查当前登录用户是否对该博客点过赞
        checkIsLiked(blogDTO);
        return Result.ok(blogDTO);
    }

    @Override
    public Result queryHotBlogs(Integer current) {
        Page<Blog> page = page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE), null);
        List<Blog> records = page.getRecords();

        List<BlogDTO> dtoList = records.stream().map(blog -> {
            BlogDTO blogDTO = new BlogDTO();
            BeanUtil.copyProperties(blog, blogDTO);

            User user = userService.getById(blog.getUserId());
            if (user != null) {
                blogDTO.setAuthorName(user.getNickname());
                blogDTO.setAuthorIcon(user.getIcon());
            }
            checkIsLiked(blogDTO);
            return blogDTO;
        }).collect(Collectors.toList());

        return Result.ok(dtoList, page.getTotal());
    }

    @Override
    @Transactional
    public Result likeBlog(Long id) {
        Long userId = UserHolder.getUser().getId();
        String key = RedisConstants.BLOG_LIKED_KEY + id;

        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());

        if (score != null) {
            boolean success = update().setSql("liked = liked - 1").eq("id", id).update();
            if (success) {
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        } else {
            boolean success = update().setSql("liked = liked + 1").eq("id", id).update();
            if (success) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
                createLikeNotification(id, userId);
            }
        }
        return Result.ok();
    }

    private void createLikeNotification(Long blogId, Long likerId) {
        cacheRebuildExecutor.submit(() -> {
            Blog blog = getById(blogId);
            if (blog != null && !blog.getUserId().equals(likerId)) {
                Notification notification = new Notification();
                notification.setId(redisIdWorker.nextId("notification"));
                notification.setRecipientId(blog.getUserId());
                notification.setSenderId(likerId);
                notification.setType(3);
                notification.setTargetId(blog.getId());
                notification.setTargetContent("您的博客《" + blog.getTitle() + "》被点赞了");
                notificationService.save(notification);
            }
        });
    }

    @Override
    public Result queryBlogLikes(Long id) {
        String key = RedisConstants.BLOG_LIKED_KEY + id;
        Set<String> top5 = stringRedisTemplate.opsForZSet().reverseRange(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());

        List<UserDTO> userDTOS = userService.listByIds(ids)
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());

        return Result.ok(userDTOS);
    }

    private void checkIsLiked(BlogDTO blogDTO) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            blogDTO.setIsLiked(false);
            return;
        }
        String key = RedisConstants.BLOG_LIKED_KEY + blogDTO.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, user.getId().toString());
        blogDTO.setIsLiked(score != null);
    }
}