package me.chanjar.weixin.cp.message;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxErrorExceptionHandler;
import me.chanjar.weixin.common.api.WxMessageDuplicateChecker;
import me.chanjar.weixin.common.api.WxMessageInMemoryDuplicateCheckerSingleton;
import me.chanjar.weixin.common.session.InternalSession;
import me.chanjar.weixin.common.session.InternalSessionManager;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.common.util.LogExceptionHandler;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author gu_renkai
 * @date 2022/11/15 16:31
 */
@Slf4j
public class MessageRouter extends WxCpMessageRouter {

    private static final int DEFAULT_THREAD_POOL_SIZE = 100;
    private final List<WxCpMessageRouterRule> rules = new ArrayList<>();

    private final WxCpService wxCpService;

    private ExecutorService executorService;

    private WxMessageDuplicateChecker messageDuplicateChecker;

    private WxSessionManager sessionManager;

    private WxErrorExceptionHandler exceptionHandler;

    /**
     * 构造方法.
     *
     * @param wxCpService the wx cp service
     */
    public MessageRouter(WxCpService wxCpService) {
        super(wxCpService);
        this.wxCpService = wxCpService;
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("WxCpMessageRouter-pool-%d").build();
        executorService = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), namedThreadFactory);
        messageDuplicateChecker = WxMessageInMemoryDuplicateCheckerSingleton.getInstance();
        sessionManager = wxCpService.getSessionManager();
        exceptionHandler = new LogExceptionHandler();
    }

    /**
     * 使用自定义的 {@link ExecutorService}.
     *
     * @param wxMpService     the wx mp service
     * @param executorService the executor service
     */
    public MessageRouter(WxCpService wxMpService, ExecutorService executorService) {
        super(wxMpService, executorService);
        wxCpService = wxMpService;
        this.executorService = executorService;
        messageDuplicateChecker = WxMessageInMemoryDuplicateCheckerSingleton.getInstance();
        sessionManager = wxCpService.getSessionManager();
        exceptionHandler = new LogExceptionHandler();
    }

    /**
     * 系统退出前，应该调用该方法
     */
    @Override
    public void shutDownExecutorService() {
        executorService.shutdown();
    }

    /**
     * 系统退出前，应该调用该方法，增加了超时时间检测
     *
     * @param second the second
     */
    @Override
    public void shutDownExecutorService(Integer second) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(second, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(second, TimeUnit.SECONDS))
                    log.error("线程池未关闭！");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * <pre>
     * 设置自定义的 {@link ExecutorService}
     * 如果不调用该方法，默认使用 Executors.newFixedThreadPool(100)
     * </pre>
     *
     * @param executorService the executor service
     */
    @Override
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * <pre>
     * 设置自定义的 {@link WxMessageDuplicateChecker}
     * 如果不调用该方法，默认使用 {@link me.chanjar.weixin.common.api.WxMessageInMemoryDuplicateChecker}
     * </pre>
     *
     * @param messageDuplicateChecker the message duplicate checker
     */
    @Override
    public void setMessageDuplicateChecker(WxMessageDuplicateChecker messageDuplicateChecker) {
        this.messageDuplicateChecker = messageDuplicateChecker;
    }

    /**
     * <pre>
     * 设置自定义的{@link WxSessionManager}
     * 如果不调用该方法，默认使用 {@link me.chanjar.weixin.common.session.StandardSessionManager}
     * </pre>
     *
     * @param sessionManager the session manager
     */
    @Override
    public void setSessionManager(WxSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * <pre>
     * 设置自定义的{@link WxErrorExceptionHandler}
     * 如果不调用该方法，默认使用 {@link LogExceptionHandler}
     * </pre>
     *
     * @param exceptionHandler the exception handler
     */
    @Override
    public void setExceptionHandler(WxErrorExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Gets rules.
     *
     * @return the rules
     */
    @Override
    List<WxCpMessageRouterRule> getRules() {
        return rules;
    }

    /**
     * 开始一个新的Route规则.
     *
     * @return the wx cp message router rule
     */
    @Override
    public WxCpMessageRouterRule rule() {
        return new WxCpMessageRouterRule(this);
    }

    /**
     * 处理微信消息.
     *
     * @param wxMessage the wx message
     * @param context   the context
     * @return the wx cp xml out message
     */
    @Override
    public WxCpXmlOutMessage route(final WxCpXmlMessage wxMessage, final Map<String, Object> context) {
        if (isMsgDuplicated(wxMessage)) {
            log.warn("过滤了重复消息");
            // 如果是重复消息，那么就不做处理
            return null;
        }

        final List<WxCpMessageRouterRule> matchRules = new ArrayList<>();
        // 收集匹配的规则
        for (final WxCpMessageRouterRule rule : rules) {
            if (rule.test(wxMessage)) {

                matchRules.add(rule);
                if (!rule.isReEnter()) {
                    break;
                }
            }
        }

        if (matchRules.size() == 0) {
            return null;
        }

        WxCpXmlOutMessage res = null;
        final List<Future> futures = new ArrayList<>();
        for (final WxCpMessageRouterRule rule : matchRules) {
            // 返回最后一个非异步的rule的执行结果
            if (rule.isAsync()) {
                futures.add(
                        executorService.submit(() -> {
                            rule.service(wxMessage, context, wxCpService,
                                    sessionManager, exceptionHandler);
                        })
                );
            } else {
                res = rule.service(wxMessage, context, wxCpService, sessionManager, exceptionHandler);
                // 在同步操作结束，session访问结束
                log.debug("End session access: async=false, sessionId={}", wxMessage.getFromUserName());
                sessionEndAccess(wxMessage);
            }
        }

        if (futures.size() > 0) {
            executorService.submit(() -> {
                for (Future future : futures) {
                    try {
                        future.get();
                        log.debug("End session access: async=true, sessionId={}", wxMessage.getFromUserName());
                        // 异步操作结束，session访问结束
                        sessionEndAccess(wxMessage);
                    } catch (InterruptedException e) {
                        log.error("Error happened when wait task finish", e);
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        log.error("Error happened when wait task finish", e);
                    }
                }
            });
        }
        return res;
    }

    /**
     * 处理微信消息.
     *
     * @param wxMessage the wx message
     * @return the wx cp xml out message
     */
    @Override
    public WxCpXmlOutMessage route(final WxCpXmlMessage wxMessage) {
        return route(wxMessage, new HashMap<>(2));
    }

    private boolean isMsgDuplicated(WxCpXmlMessage wxMessage) {
        StringBuilder messageId = new StringBuilder();
        if (wxMessage.getMsgId() == null) {
            messageId.append(wxMessage.getCreateTime())
                    .append("-").append(StringUtils.trimToEmpty(String.valueOf(wxMessage.getAgentId())))
                    .append("-").append(wxMessage.getFromUserName())
                    .append("-").append(StringUtils.trimToEmpty(wxMessage.getEventKey()))
                    .append("-").append(StringUtils.trimToEmpty(wxMessage.getEvent()));
        } else {
            messageId.append(wxMessage.getMsgId())
                    .append("-").append(wxMessage.getCreateTime())
                    .append("-").append(wxMessage.getFromUserName());
        }
        if (Objects.nonNull(wxMessage.getApprovalInfo())) {
            append(messageId, wxMessage.getApprovalInfo().getSpNo());
            append(messageId, String.valueOf(wxMessage.getApprovalInfo().getSpStatus()));
        }
        append(messageId, wxMessage.getUserId());
        append(messageId, wxMessage.getChangeType());
        append(messageId, wxMessage.getTagId());
        append(messageId, wxMessage.getId());
        append(messageId, wxMessage.getChatId());
        append(messageId, wxMessage.getExternalUserId());
//        log.info("messageId {}", messageId);
        return messageDuplicateChecker.isDuplicate(messageId.toString());
    }

    private void append(StringBuilder sb, String value) {
        if (StringUtils.isNotEmpty(value)) {
            sb.append("-").append(value);
        }
    }

    /**
     * 对session的访问结束.
     */
    private void sessionEndAccess(WxCpXmlMessage wxMessage) {
        InternalSession session = ((InternalSessionManager) sessionManager).findSession(wxMessage.getFromUserName());
        if (session != null) {
            session.endAccess();
        }

    }
}
