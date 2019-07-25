package priv.javthon.jinspector.utils;


import priv.javthon.jinspector.entity.MailSender;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * 邮件发送工具类
 * @author xxf
 * @version 1.0
 */
public class EmailUtil {


    public static void sendMail(MailSender mailSender, List<String> emailAddress, String subject, String htmlResult) throws MessagingException, UnsupportedEncodingException {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", mailSender.getHost());// 主机名
        properties.put("mail.smtp.port", mailSender.getPort());// 端口号
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        properties.put("mail.debug", "false");// 设置是否显示debug信息 true 会在控制台显示相关信息
        properties.put("mail.smtp.socketFactory.port", mailSender.getPort());
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // 得到回话对象
        Session session = Session.getInstance(properties,new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailSender.getAddress(), mailSender.getPassword());
            }
        });
        // 获取邮件对象
        Message message = new MimeMessage(session);
        // 设置发件人邮箱地址
        message.setFrom(new InternetAddress(mailSender.getAddress(), MimeUtility.encodeText(mailSender.getNickname(),"gb2312","b")));
        // 设置收件人邮箱地址
        InternetAddress[] receivers = new InternetAddress[emailAddress.size()];
        for(int i=0; i<emailAddress.size();i++){
            receivers[i] = new InternetAddress(emailAddress.get(i));
        }
        message.setRecipients(Message.RecipientType.TO,receivers);
        message.setSubject(subject);
        Multipart multipart = new MimeMultipart();
        BodyPart html = new MimeBodyPart();
        html.setContent(htmlResult, "text/html; charset=utf-8");
        multipart.addBodyPart(html);
        // 将MiniMultipart对象设置为邮件内容
        message.setContent(multipart);
        Transport.send(message);
    }


}
