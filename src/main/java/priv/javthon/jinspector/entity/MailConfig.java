package priv.javthon.jinspector.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MailConfig {
    private MailSender mailSender;
    private String title;
    private List<String> receivers;
}
