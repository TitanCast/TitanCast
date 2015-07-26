package com.hydrabolt.titancast.secure;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Created by Amish on 26/07/2015.
 */
public class SSLGen {

    private static final long MIN = 1000 * 60L;

    private static final long HALFHOUR = MIN * 30L;

    private static final long ONEHOUR = HALFHOUR * 2;

    private static final long ONEDAY = ONEHOUR * 24L;

    private static final long ONEYEAR = ONEDAY * 365L;

    private static final String BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

    public static boolean generate(String hostname, File keystore, String password){

        try{

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
            kpGen.initialize(1024, new SecureRandom());
            KeyPair pair = kpGen.generateKeyPair();

            // Generate self-signed certificate
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.OU, "TitanCast");
            builder.addRDN(BCStyle.O, "TitanCast");
            builder.addRDN(BCStyle.CN, hostname);

            Date notBefore = new Date(System.currentTimeMillis() - ONEDAY);
            Date notAfter = new Date(System.currentTimeMillis() + 10 * ONEYEAR);
            BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(builder.build(),
                    serial, notBefore, notAfter, builder.build(), pair.getPublic());
            ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .setProvider(BC).build(pair.getPrivate());
            X509Certificate cert = new JcaX509CertificateConverter().setProvider(BC)
                    .getCertificate(certGen.build(sigGen));
            cert.checkValidity(new Date());
            cert.verify(cert.getPublicKey());

            // Save to keystore
            KeyStore store = KeyStore.getInstance("BKS");
            if (keystore.exists()) {
                FileInputStream fis = new FileInputStream(keystore);
                store.load(fis, password.toCharArray());
                fis.close();
            } else {
                store.load(null);
            }
            store.setKeyEntry(hostname, pair.getPrivate(), password.toCharArray(),
                    new java.security.cert.Certificate[] { cert });
            FileOutputStream fos = new FileOutputStream(keystore);
            store.store(fos, password.toCharArray());
            fos.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
