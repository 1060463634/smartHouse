package com.qqcs.smartHouse.utils;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密工具类
 * @author saferycom
 * @date 2019/7/12
 */
public class AESUtils {

    // Base64加密
    public static final int MODE_BASE64 = 0;
    // 16进制加密
    public static final int MODE_HEX = 1;

    // 算法类型：用于指定生成AES的密钥
    private static final String ALGORITHM = "AES";
    // 加密器类型：加密算法为AES，加密模式为CBC，补码方式为PKCS5Padding
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    // 编码字符集
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * 生成加密秘钥
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
//        // 构造指定算法密钥生成器
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//        // 生成一个128位的随机源，AES要求密钥长度为128位
//        // 解决Linux上报异常：javax.crypto.BadPaddingException: Given final block not properly padded
//        //keyGenerator.init(128, new SecureRandom(password.getBytes()));
//        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
//        random.setSeed(password.getBytes());
//        keyGenerator.init(128, random);
//
//        // 生成原始对称密钥
//        SecretKey secretKey = keyGenerator.generateKey();
//        // 转换为AES专用密钥
//        return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);

        // 注意，为了能与 iOS 统一
        // 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
        return new SecretKeySpec(Arrays.copyOf(password.getBytes(), 16), ALGORITHM);
    }

    /**
     * AES加密
     * @param content 需要加密的字符串
     * @param password 加密的密钥
     * @return 返回转码后的加密数据
     */
    public static String encrypt(String content, String password) {
        return encrypt(content, password, MODE_HEX);
    }

    /**
     * AES加密
     * @param content 需要加密的字符串
     * @param password 加密的密钥
     * @param mode 转码时对应的加密模式
     * @return 返回转码后的加密数据
     */
    public static String encrypt(String content, String password, int mode) {
        try {
            // 根据指定算法生成密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            // 使用密钥初始化密码器，设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            // 加密内容的字节数组
            byte[] contentBytes = content.getBytes(DEFAULT_ENCODING);
            // 执行加密
            byte[] result = cipher.doFinal(contentBytes);
            // 将加密后的数据转换为字符串
            //return new BASE64Encoder().encode(result);
            switch (mode) {
                case 0:
                    return Base64.encodeToString(result,Base64.DEFAULT);
                default:
                    return bytes2Hex(result); // 默认使用16进制做转码加密
            }
        } catch (Exception e) {
            return content;
        }
    }

    /**
     * AES解密
     * @param encrypted 已加密的密文
     * @param password 加密的密钥
     * @return 返回解密后的数据
     */
    public static String decrypt(String encrypted, String password) throws Exception {
        return decrypt(encrypted, password, MODE_HEX);
    }

    /**
     * AES解密
     * @param encrypted 已加密的密文
     * @param password 加密的密钥
     * @param mode 解码时对应的解密模式
     * @return 返回解密后的数据
     */
    public static String decrypt(String encrypted, String password, int mode) throws Exception {
        // 根据指定算法生成密码器
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
        // 将加密并编码后的内容解码成字节数组
        //byte[] encryptedBytes = new BASE64Decoder().decodeBuffer(encrypted);
        byte[] encryptedBytes = null;
        switch (mode) {
            case 0:
                encryptedBytes = Base64.decode(encrypted,Base64.DEFAULT); // 先用Base64解密
                break;
            default:
                encryptedBytes = hex2Bytes(encrypted); // 默认使用16进制转换二进制字节数组
        }

        // 执行解密
        byte[] result = cipher.doFinal(encryptedBytes);
        return new String(result, DEFAULT_ENCODING);
    }

    /**
     * 将二进制转换成16进制
     * @param buf
     * @return
     */
    private static String bytes2Hex(byte[] buf) {
        if (buf == null || buf.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            buffer.append(hex);
        }
        return buffer.toString().toUpperCase();
    }

    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] hex2Bytes(String hexStr) {
        if (hexStr == null || "".equals(hexStr) || hexStr.length() % 2 != 0) {
            return null;
        }
        int len = hexStr.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }

    public static void main(String args[]) throws Exception {
        String content = "test1好";
        String password = "12345678";

        //加密
        System.out.println("加密前（Base64）：" + content);
        String encryptResult = encrypt(content, password, MODE_BASE64);
        System.out.println("加密后（Base64）：" + encryptResult);
        //解密
        String decryptResult = decrypt(encryptResult, password, MODE_BASE64);
        System.out.println("解密后（Base64）：" + decryptResult);

        //加密
        System.out.println("加密前（HEX）：" + content);
        encryptResult = encrypt(content, password, MODE_HEX);
        System.out.println("加密后（HEX）：" + encryptResult);
        //解密
        decryptResult = decrypt(encryptResult, password, MODE_HEX);
        System.out.println("解密后（HEX）：" + decryptResult);

        System.out.println(encrypt("123456", "Safery@~1a", MODE_HEX));
        System.out.println(decrypt("18975D561B880F1871DEE14266FDF9C1", "Safery@~1a", MODE_HEX));

    }

}
