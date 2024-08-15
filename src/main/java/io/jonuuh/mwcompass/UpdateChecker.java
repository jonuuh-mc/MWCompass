package io.jonuuh.mwcompass;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UpdateChecker
{
    private static UpdateChecker instance;
    private final boolean isLatestVersion;

    public static UpdateChecker createInstance()
    {
        if (instance != null)
        {
            throw new IllegalStateException("UpdateChecker instance has already been created");
        }

        instance = new UpdateChecker();
        return instance;
    }

    public static UpdateChecker getInstance()
    {
        if (instance == null)
        {
            throw new NullPointerException("UpdateChecker instance has not been created");
        }

        return instance;
    }

    public UpdateChecker()
    {
        this.isLatestVersion = isLatestVersion();
    }

    public boolean getIsLatestVersion()
    {
        return isLatestVersion;
    }

    private boolean isLatestVersion()
    {
        List<String> urlContent = getUrlContent("https://raw.githubusercontent.com/jonuuh/MWCompass/master/src/main/resources/version.txt");
        String latestVersion = urlContent.size() == 1 ? urlContent.get(0) : MWCompass.version;

        if (latestVersion.matches("[0-9]+\\.[0-9]+\\.[0-9]+") && !latestVersion.equals(MWCompass.version))
        {
            System.out.println("[MWCompass] Update available; (latest:" + latestVersion + ") !=  (current:" + MWCompass.version + ")");
            return false;
        }

        System.out.println("[MWCompass] Up to date; (latest:" + latestVersion + ") == (current:" + MWCompass.version + ")");
        return true;
    }

    private List<String> getUrlContent(String url)
    {
        List<String> urlContent = new ArrayList<>();

        try
        {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
            httpsURLConnection.setSSLSocketFactory(createSSLContext(getGithubIOCertificateString()).getSocketFactory());

            Scanner scanner = new Scanner(httpsURLConnection.getInputStream());

            while (scanner.hasNextLine())
            {
                urlContent.add(scanner.nextLine());
            }
            scanner.close();
        }
        catch (GeneralSecurityException | IOException e)
        {
            System.out.println("[BNT] Failed to access or read version file");
            e.printStackTrace();
        }

        return urlContent;
    }

    private SSLContext createSSLContext(String derCertificateString) throws GeneralSecurityException, IOException
    {
        ByteArrayInputStream derInputStream = new ByteArrayInputStream(derCertificateString.getBytes());

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(derInputStream);
        String alias = certificate.getSubjectX500Principal().getName();

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        trustStore.setCertificateEntry(alias, certificate);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(trustStore, null);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }

    private String getGithubIOCertificateString()
    {
        InputStream inputStream = UpdateChecker.class.getClassLoader().getResourceAsStream("_.github.io.crt");

        if (inputStream != null)
        {
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String certificateStr = scanner.next();
            scanner.close();
            return certificateStr;
        }
        return "";
    }
}
