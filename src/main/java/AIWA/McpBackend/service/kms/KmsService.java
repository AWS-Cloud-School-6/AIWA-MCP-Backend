//package AIWA.McpBackend.service.kms;
//
//import com.amazonaws.services.kms.AWSKMS;
//import com.amazonaws.services.kms.AWSKMSClientBuilder;
//import com.amazonaws.services.kms.model.DecryptRequest;
//import com.amazonaws.services.kms.model.EncryptRequest;
//import com.amazonaws.services.kms.model.EncryptResult;
//import com.amazonaws.services.kms.model.DecryptResult;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//@Service
//public class KmsService {
//
//    private final AWSKMS kmsClient;
//    private final String kmsKeyId;
//
//    public KmsService(@Value("${aws.kms.keyId}") String kmsKeyId,
//                      @Value("${aws.kms.region}") String kmsRegion) {
//        this.kmsKeyId = kmsKeyId;
//        this.kmsClient = AWSKMSClientBuilder.standard()
//                .withRegion(kmsRegion)
//                .build();
//    }
//
//    public String encrypt(String plaintext) {
//        ByteBuffer plaintextBuffer = ByteBuffer.wrap(plaintext.getBytes(StandardCharsets.UTF_8));
//        EncryptRequest encryptRequest = new EncryptRequest()
//                .withKeyId(kmsKeyId)
//                .withPlaintext(plaintextBuffer);
//
//        EncryptResult encryptResult = kmsClient.encrypt(encryptRequest);
//        return Base64.getEncoder().encodeToString(encryptResult.getCiphertextBlob().array());
//    }
//
//    public String decrypt(String encryptedValue) {
//        ByteBuffer encryptedBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedValue));
//        DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(encryptedBuffer);
//        DecryptResult decryptResult = kmsClient.decrypt(decryptRequest);
//        return new String(decryptResult.getPlaintext().array(), StandardCharsets.UTF_8);
//    }
//}