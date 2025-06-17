// src/main/java/xyz/tjucomments/tjufood/service/impl/CommentServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.dto.UserDTO;
import xyz.tjucomments.tjufood.entity.Blog;
import xyz.tjucomments.tjufood.entity.Comment;
import xyz.tjucomments.tjufood.entity.Notification;
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.mapper.CommentMapper;
import xyz.tjucomments.tjufood.service.IBlogService;
import xyz.tjucomments.tjufood.service.ICommentService;
import xyz.tjucomments.tjufood.service.INotificationService;
import xyz.tjucomments.tjufood.utils.constants.IdPrefixConstants;
import xyz.tjucomments.tjufood.utils.id.RedisIdWorker;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private IBlogService blogService;

    // 【新增注入】注入通知服务和线程池
    @Resource
    private INotificationService notificationService;
    @Resource
    private ExecutorService cacheRebuildExecutor;


    @Override
    @Transactional
    public Result addComment(Comment comment) {
        UserDTO currentUser = UserHolder.getUser();
        if (currentUser == null) {
            return Result.fail("请先登录！");
        }
        comment.setUserId(currentUser.getId());
        comment.setId(redisIdWorker.nextId(IdPrefixConstants.COMMENT_ID_PREFIX));

        boolean isSuccess = save(comment);
        if (!isSuccess) {
            return Result.fail("评论失败！");
        }

        // 【核心新增】异步创建评论通知
        createCommentNotification(comment);

        return Result.ok(comment.getId());
    }

    /**
     * 创建评论通知的辅助方法
     * @param comment 已保存的评论对象
     */
    private void createCommentNotification(Comment comment) {
        cacheRebuildExecutor.submit(() -> {
            // 目前只处理对博客的评论通知
            if (comment.getTargetType() == 1) { // 1=博客
                Blog blog = blogService.getById(comment.getTargetId());
                UserDTO commenter = UserHolder.getUser();
                // 如果是自己评论自己的博客，则不发送通知
                if (blog != null && !blog.getUserId().equals(commenter.getId())) {
                    Notification notification = new Notification();
                    notification.setId(redisIdWorker.nextId("notification"));
                    notification.setRecipientId(blog.getUserId()); // 接收者是博客作者
                    notification.setSenderId(commenter.getId());
                    notification.setType(1); // 1=评论
                    notification.setTargetId(blog.getId());
                    notification.setTargetContent("您的博客《" + blog.getTitle() + "》有了新评论");
                    notificationService.save(notification);
                }
            }
            // 此处可以扩展对其他类型（如回复评论）的通知逻辑
        });

        // 无论通知是否成功，主流程都视为成功，不影响用户体验
        // 评论数更新也应在这里，确保事务性
        if (comment.getTargetType() == 1) {
            blogService.update().setSql("comments = comments + 1").eq("id", comment.getTargetId()).update();
        }
    }

    @Override
    public Result queryCommentsByTarget(Long targetId, Integer type) {
        List<Comment> list = list(new QueryWrapper<Comment>()
                .eq("target_id", targetId)
                .eq("target_type", type)
                .orderByDesc("create_time"));
        return Result.ok(list);
    }
}