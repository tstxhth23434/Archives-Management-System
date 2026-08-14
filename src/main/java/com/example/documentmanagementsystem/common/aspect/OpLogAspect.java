package com.example.documentmanagementsystem.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.example.documentmanagementsystem.common.annotation.OpLog;
import com.example.documentmanagementsystem.modules.system.entity.SysLog;
import com.example.documentmanagementsystem.modules.system.mapper.SysLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 拦截标注了 @OpLog 的方法，自动记录操作日志到 sys_log 表
 *
 * 设计要点：
 * 1. 参数脱敏：password 等敏感字段替换为 ***，防止明文密码入库
 * 2. 日志失败不影响业务：try-catch 包裹，日志落库异常只记日志不抛出
 * 3. 登录接口本身无 token，userId/username 可能为 null，属正常
 */
@Slf4j
@Aspect
@Component
public class OpLogAspect {

    @Resource
    private SysLogMapper sysLogMapper;

    /**
     * 环绕通知：方法执行前后记录日志
     */
    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint joinPoint, OpLog opLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Throwable error = null;
        try {
            // 执行业务方法
            return joinPoint.proceed();
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            try {
                // 无论成功失败都记录（finally 保证执行）
                saveLog(joinPoint, opLog, System.currentTimeMillis() - startTime, error == null);
            } catch (Exception e) {
                // 日志落库失败不影响业务
                log.warn("操作日志记录失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 组装并保存日志
     */
    private void saveLog(ProceedingJoinPoint joinPoint, OpLog opLog, long spendTime, boolean success) {
        SysLog sysLog = new SysLog();

        // 1. 当前登录用户（登录接口无 token 时 userId/username 为 null）
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                sysLog.setUserId(Long.valueOf(loginId.toString()));
                // username 从 Sa-Token session 里取（登录时存的）
                Object username = StpUtil.getSession().get("username");
                sysLog.setUsername(username == null ? null : username.toString());
            }
        } catch (Exception ignored) {
            // 非登录态（如登录接口本身）
        }

        // 2. 操作描述
        sysLog.setOperation(opLog.value() + (success ? "" : "（失败）"));

        // 3. 请求方法（类名.方法名）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        sysLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());

        // 4. 请求参数（脱敏）
        sysLog.setParams(buildParams(joinPoint.getArgs()));

        // 5. IP
        sysLog.setIp(getClientIp());

        // 6. 耗时 + 时间
        sysLog.setSpendTime((int) spendTime);
        sysLog.setCreateTime(LocalDateTime.now());

        sysLogMapper.insert(sysLog);
    }

    /**
     * 构建请求参数 JSON（过滤 Servlet 对象、MultipartFile，脱敏 password 字段）
     */
    private String buildParams(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            // 跳过 Servlet 对象、上传文件等无法序列化的参数
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                    || arg instanceof MultipartFile) {
                continue;
            }
            if (i > 0) {
                sb.append(", ");
            }
            String json = toJsonWithMask(arg);
            sb.append(json);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 对象转 JSON，password 字段脱敏
     */
    private String toJsonWithMask(Object obj) {
        if (obj == null) {
            return "null";
        }
        String json = JSONUtil.toJsonStr(obj);
        // 密码字段脱敏：如 "password":"abc123" → "password":"***"
        return json.replaceAll("(\"password\"\\s*:\\s*\")[^\"]*(\")", "$1***$2");
    }

    /**
     * 获取客户端真实 IP（兼容反向代理）
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        } catch (Exception e) {
            return null;
        }
    }
}
