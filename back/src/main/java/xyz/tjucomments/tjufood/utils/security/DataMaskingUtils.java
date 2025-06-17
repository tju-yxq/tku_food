package xyz.tjucomments.tjufood.utils.security;

import cn.hutool.core.util.StrUtil;

/**
 * 数据脱敏工具类
 * 符合阿里巴巴Java开发手册安全规约第2条：用户敏感数据禁止直接展示
 */
public class DataMaskingUtils {

    /**
     * 手机号脱敏
     * 158****9119
     */
    public static String maskPhone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     * u***@example.com
     */
    public static String maskEmail(String email) {
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 1) {
            return email;
        }
        
        String maskedUsername = username.charAt(0) + "***";
        if (username.length() > 4) {
            maskedUsername = username.substring(0, 2) + "***";
        }
        
        return maskedUsername + "@" + domain;
    }

    /**
     * 身份证号脱敏
     * 110***********1234
     */
    public static String maskIdCard(String idCard) {
        if (StrUtil.isBlank(idCard) || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 姓名脱敏
     * 张*三 或 李**
     */
    public static String maskName(String name) {
        if (StrUtil.isBlank(name)) {
            return name;
        }
        if (name.length() == 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    /**
     * 银行卡号脱敏
     * 6222***********1234
     */
    public static String maskBankCard(String bankCard) {
        if (StrUtil.isBlank(bankCard) || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "***********" + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 地址脱敏
     * 保留省市，详细地址用*代替
     */
    public static String maskAddress(String address) {
        if (StrUtil.isBlank(address) || address.length() <= 6) {
            return address;
        }
        return address.substring(0, 6) + "***";
    }
}
