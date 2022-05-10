package ee.viigipuu.dev.groovymailservice

import groovy.util.logging.Slf4j

import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.MimeMessage

@Slf4j
class SendingConf {

    final static String SMTP_CONF_PATH = 'src/main/resources/smtp.properties'
    final static String SMTP_CONF_SAMPLE_PATH = 'src/main/resources/smtp_sample.properties'

    private File smtpConfFile
    private File smtpConfSampleFile
    private Properties appProps

    SendingConf() {
        smtpConfFile = new File(SMTP_CONF_PATH)
        smtpConfSampleFile = new File(SMTP_CONF_SAMPLE_PATH)
        if (smtpConfFile.exists()) {
            appProps = readPropertiesFromFile(smtpConfFile)
            if (!appProps?.host || appProps?.host == 'default') {
                log.info("Smtp not configured, review settings at ${SMTP_CONF_PATH}. System exit ...")
                System.exit(1)
            }
        } else if (smtpConfSampleFile.exists()) {
            smtpConfFile << smtpConfSampleFile.text
            log.info("Smtp not configured, copying sample to ${SMTP_CONF_PATH}, enter correct values. System exit ...")
            System.exit(1)
        } else {
            log.error("Conf file and conf sample was not found. What is going on here! System exit ...")
            System.exit(1)
        }
    }

    String propValue(String key) {
        return appProps."$key" ?: ""
    }

    MimeMessage newMessage() {
        Properties mailProps = new Properties()
        mailProps.put('mail.smtp.user', appProps.user)
        mailProps.put('mail.smtp.host', appProps.host)
        mailProps.put('mail.smtp.port', appProps.port)
        mailProps.put('mail.smtp.starttls.enable', 'true')
        mailProps.put('mail.smtp.socketFactory.class', "javax.net.ssl.SSLSocketFactory")
        mailProps.put('mail.smtp.ssl.trust', "*")
        mailProps.put('mail.smtp.auth', 'true')

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(appProps.user, appProps.pass);
            }
        }
        return new MimeMessage(Session.getInstance(mailProps, auth))
    }

    private Properties readPropertiesFromFile(File propertiesFile) {
        Properties properties = new Properties()
        propertiesFile.withInputStream {
            properties.load(it)
        }
        return properties
    }
}
