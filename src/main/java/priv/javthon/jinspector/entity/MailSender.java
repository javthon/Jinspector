package priv.javthon.jinspector.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailSender {
    private String host;
    private Integer port;
    private String address;
    private String password;
    private String nickname;
}
