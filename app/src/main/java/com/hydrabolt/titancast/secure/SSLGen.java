package com.hydrabolt.titancast.secure;

import android.util.Log;

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
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

            long baseTime = 1420070400;

            Date notBefore = new Date(baseTime - ONEDAY);
            Date notAfter = new Date(baseTime + 100 * ONEYEAR);
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

            Log.d("the md5 is...", checksum(keystore));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

    public static String checksum(File file) {
        try {
            InputStream fin = new FileInputStream(file);
            java.security.MessageDigest md5er =
                    MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = fin.read(buffer);
                if (read > 0)
                    md5er.update(buffer, 0, read);
            } while (read != -1);
            fin.close();
            byte[] digest = md5er.digest();
            if (digest == null)
                return null;
            String strDigest = "0x";
            for (int i = 0; i < digest.length; i++) {
                strDigest += Integer.toString((digest[i] & 0xff)
                        + 0x100, 16).substring(1).toUpperCase();
            }
            return strDigest;
        } catch (Exception e) {
            return null;
        }
    }

}
