import com.company.Domain.Message;
import com.company.Domain.User;
import com.company.Utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class TestsMessage {
    @Test
    public void TestCreateMessage(){
        User fromUser = new User("Popica", "Nechifor");
        fromUser.setId(1L);
        User toUser1 = new User("Monke", "Mouse");
        toUser1.setId(2L);
        User toUser2 = new User("A", "B");
        toUser2.setId(3L);
        Message message = new Message(fromUser, "Hello World!", LocalDateTime.now());
        Message newMessage = new Message(fromUser, "HEEEYY!!", LocalDateTime.now());
        message.setToUsers(toUser1);
        message.setToUsers(toUser2);
        newMessage.setToUsers(toUser1);
        Assertions.assertTrue(message.getToUsers().size() == 2);
        Assertions.assertTrue(newMessage.getToUsers().size() == 1);
        //System.out.println(message.getDate().format(Constants.DATE_TIME_FORMATTER));
    }
}
