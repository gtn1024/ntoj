package zip.ntoj.server.config

import cn.dev33.satoken.jwt.SaJwtTemplate
import cn.dev33.satoken.util.SaFoxUtil
import cn.hutool.jwt.JWT
import zip.ntoj.shared.util.randomString
import java.util.Date

class CustomSaJwtTemplate : SaJwtTemplate() {
    override fun createToken(
        loginType: String?,
        loginId: Any?,
        device: String?,
        timeout: Long,
        extraData: MutableMap<String, Any>?,
        keyt: String?,
    ): String {
        val currentTimestamp = System.currentTimeMillis()
        var expTime = timeout
        if (timeout != NEVER_EXPIRE) {
            expTime = timeout * 1000 + currentTimestamp
        }

        val jwt =
            JWT.create()
                .setIssuer("NTOJ")
                .setIssuedAt(Date(currentTimestamp))
                .setNotBefore(Date(currentTimestamp))
                .setExpiresAt(Date(expTime))
                .setJWTId(randomString())
                .setSubject(loginId.toString())
                .setPayload(LOGIN_TYPE, loginType)
                .setPayload(LOGIN_ID, loginId)
                .setPayload(DEVICE, device)
                .setPayload(EFF, expTime)
                .setPayload(RN_STR, SaFoxUtil.getRandomString(32))
                .addPayloads(extraData)
        return generateToken(jwt, keyt)
    }
}
