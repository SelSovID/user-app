package com.example.selsovid

import com.example.selsovid.data.SsiCert.SSICert
import com.google.protobuf.kotlin.toByteString
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.crypto.params.RSAKeyParameters
import org.bouncycastle.crypto.util.PublicKeyFactory
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringWriter
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.Cipher


class SSICertUtilities(
  private val publicKey: RSAPublicKey,
  val credentialText: String,
  private val ownerSignature: ByteArray,
  private val parentSignature: ByteArray? = null,
  private val parent: SSICertUtilities? = null,
) {
  private val isSelfSigned: Boolean
    get() = parent == null

  private fun exportToProto(): SSICert {
    val builder = SSICert.newBuilder()
    builder.credentialText = credentialText

    val publicKeyEncoded = Base64.getEncoder().encodeToString(publicKey.encoded)
    val publicKeyStrWriter = StringWriter()
    publicKeyStrWriter.write("-----BEGIN PUBLIC KEY-----\n")
    publicKeyStrWriter.write(publicKeyEncoded)
    publicKeyStrWriter.write("\n")
    publicKeyStrWriter.write("-----END PUBLIC KEY-----\n")
    publicKeyStrWriter.close()


    builder.publicKey = publicKeyStrWriter.toString()
    builder.ownerSignature = ownerSignature.toByteString()
    if (parentSignature != null) {
      builder.parentSignature = parentSignature.toByteString()
    }
    if (parent != null) {
      builder.parent = parent.exportToProto()
    }
    return builder.build()
  }

  fun export(): String {
    val protoMsg = exportToProto()
    return Base64.getEncoder().encodeToString(protoMsg.toByteArray())
  }



  override fun equals(other: Any?): Boolean {
    return if (other is SSICertUtilities) {
      (
              this.isSelfSigned == other.isSelfSigned
                      && this.publicKey == other.publicKey
                      && this.credentialText == other.credentialText
                      && this.ownerSignature.contentEquals(other.ownerSignature)
                      && this.parentSignature.contentEquals(other.parentSignature)
                      && this.parent == other.parent
              )
    } else {
      false
    }
  }

  companion object {
    fun create(
      publicKey: RSAPublicKey,
      ownerPrivateKey: RSAPrivateKey,
      credentialText: String,
    ): SSICertUtilities {
      return SSICertUtilities(publicKey, credentialText, createOwnerSignature(credentialText, ownerPrivateKey), null, null)
    }

    private fun createOwnerSignature(credentialText: String, key: RSAPrivateKey): ByteArray {
      val toSign = credentialText.toByteArray()
      return encrypt(toSign, key)
    }

    private fun createParentSignature(credentialText: String, ownerSignature: ByteArray, parentPrivateKey: RSAPrivateKey): ByteArray {
      val ownerSignatureStr = Base64.getEncoder().encodeToString(ownerSignature)
      val toSign = "$credentialText$ownerSignatureStr".toByteArray()
      return encrypt(toSign, parentPrivateKey)
    }

    fun create(
      publicKey: RSAPublicKey,
      ownerPrivateKey: RSAPrivateKey,
      credentialText: String,
      parent: SSICertUtilities,
      parentPrivateKey: RSAPrivateKey
    ): SSICertUtilities {
      val ownerSignature = createOwnerSignature(credentialText, ownerPrivateKey)
      val parentSignature = createParentSignature(credentialText, ownerSignature, parentPrivateKey)
      return SSICertUtilities(publicKey, credentialText, ownerSignature, parentSignature, parent)
    }

    fun import(data: ByteArray): SSICertUtilities {
      val decoded = SSICert.parseFrom(data)
      return import(decoded)
    }
    fun import(decoded: SSICert): SSICertUtilities {
      return SSICertUtilities(
        readPublicKey(decoded.publicKey.toByteArray()) as RSAPublicKey,
        decoded.credentialText,
        decoded.ownerSignature.toByteArray(),
        decoded.parentSignature?.toByteArray(),
        if (decoded.parent != null) import(decoded.parent) else null
      )
    }
  }
}

@Throws(Exception::class)
fun encrypt(data: ByteArray, key: Key): ByteArray {
  val encryptCipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
  val digester = MessageDigest.getInstance("SHA-256")
  val digest = digester.digest(data)
  encryptCipher.init(Cipher.ENCRYPT_MODE, key)
  return encryptCipher.doFinal(digest)
}

@Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class, IOException::class)
fun readPublicKey(keyData: ByteArray): PublicKey? {
  val pemParser = PEMParser(InputStreamReader(ByteArrayInputStream(keyData)))
  val `object`: Any = pemParser.readObject()
  val subjectPublicKeyInfo: SubjectPublicKeyInfo = `object` as SubjectPublicKeyInfo
  val rsa: RSAKeyParameters = PublicKeyFactory.createKey(subjectPublicKeyInfo) as RSAKeyParameters
  val rsaSpec = RSAPublicKeySpec(rsa.modulus, rsa.exponent)
  val kf: KeyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider())
  return kf.generatePublic(rsaSpec)
}