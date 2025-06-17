package xyz.tjucomments.tjufood.utils.security;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * XSS防护工具类
 * 符合阿里巴巴Java开发手册安全规约第5条：禁止向HTML页面输出未经安全过滤或未正确转义的用户数据
 */
public class XssUtils {

    // 危险脚本模式
    private static final Pattern[] SCRIPT_PATTERNS = {
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onload", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onerror", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onclick", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onmouseover", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onfocus", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onblur", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<object[^>]*>.*?</object>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*>.*?</embed>", Pattern.CASE_INSENSITIVE)
    };

    /**
     * HTML转义，防止XSS攻击
     */
    public static String escapeHtml(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * 清理危险的HTML标签和脚本
     */
    public static String cleanHtml(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        String cleaned = input;
        
        // 移除危险的脚本模式
        for (Pattern pattern : SCRIPT_PATTERNS) {
            cleaned = pattern.matcher(cleaned).replaceAll("");
        }
        
        // HTML转义剩余内容
        return HtmlUtils.htmlEscape(cleaned);
    }

    /**
     * 检查是否包含XSS攻击代码
     */
    public static boolean containsXss(String input) {
        if (StrUtil.isBlank(input)) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        
        // 检查常见的XSS攻击模式
        String[] xssPatterns = {
            "<script", "javascript:", "vbscript:", "onload=", "onerror=", 
            "onclick=", "onmouseover=", "onfocus=", "onblur=", "<iframe", 
            "<object", "<embed", "eval(", "expression("
        };
        
        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 过滤SQL注入关键字
     */
    public static String filterSqlKeywords(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        
        String[] sqlKeywords = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "exec", "execute", "union", "declare", "master", "truncate", "char",
            "and", "or", "where", "order", "group", "having", "count", "chr",
            "mid", "substring", "substr", "length", "len", "user", "database"
        };
        
        String result = input;
        for (String keyword : sqlKeywords) {
            result = result.replaceAll("(?i)" + keyword, "");
        }
        
        return result;
    }
}
