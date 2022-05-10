package ee.viigipuu.dev.groovymailservice

import groovy.util.logging.Slf4j
import picocli.CommandLine

import javax.mail.Message
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import java.util.concurrent.Callable

@Slf4j
@CommandLine.Command(name = 'app', mixinStandardHelpOptions = true, version = 'app 1.0',
        description = 'Sending email with java api.')
class App implements Callable<Integer> {

    static final String CHARSET = 'utf-8'

    @CommandLine.Option(names = ['-t', '--to'], description = 'Recipient email address')
    String to = 'unknown'

    @CommandLine.Option(names = ['-s', '--subject'], description = 'Email subject')
    String subject = 'default subject'

    @CommandLine.Option(names = ['-c', '--content'], description = 'Email content')
    String content = 'default content'

    void sendEmail() {
        def sendingConf = new SendingConf()
        def message = sendingConf.newMessage()
        message.setFrom(new InternetAddress(sendingConf.propValue('user')))
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
        message.setSubject(subject, CHARSET)
        message.setText(content, CHARSET)
        try {
            Transport transport = message.getSession().getTransport('smtp')
            transport.connect(sendingConf.propValue('host'), sendingConf.propValue('user'), sendingConf.propValue('pass'));
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            log.error('Failed to send message.')
            log.error(e.toString())
        }
        log.info("Message sent")
    }

    @Override
    Integer call() throws Exception {
        sendEmail()
        return 0
    }

    static void main(String[] args) {
        System.exit(new CommandLine(new App()).execute(args))
    }
}