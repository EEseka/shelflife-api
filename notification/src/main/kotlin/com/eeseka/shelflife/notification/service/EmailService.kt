package com.eeseka.shelflife.notification.service

import com.eeseka.shelflife.common.domain.type.UserId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
    private val templateService: EmailTemplateService,
    @param:Value("\${shelflife.email.from}")
    private val emailFrom: String,
    @param:Value("\${shelflife.email.url}")
    private val baseUrl: String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendVerificationEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String
    ) {
        logger.info("Sending verification email for user $userId")

        // Web Link (For Browser)
        val verificationUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/verify")
            .queryParam("token", token)
            .build()
            .toUriString()

        // Deep Link (For App)
        // Same URL, but uses shelflife:// scheme which allows easier testing of deep links
        // without having to verify them with Apple/Google
        val devVerificationUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/verify")
            .scheme("shelflife")
            .queryParam("token", token)
            .build()
            .toUriString()

        val htmlContent = templateService.processTemplate(
            templateName = "emails/account-verification",
            variables = mapOf(
                "username" to username,
                "verificationUrl" to verificationUrl,
                "devVerificationUrl" to devVerificationUrl
            )
        )

        sendHtmlEmail(to = email, subject = "Verify your ShelfLife account", html = htmlContent)
    }

    fun sendPasswordResetEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String,
        expiresIn: Duration
    ) {
        logger.info("Sending password reset email for user $userId")

        val resetPasswordUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/reset-password")
            .queryParam("token", token)
            .build()
            .toUriString()

        // Same URL, but uses chirp:// scheme which allows easier testing of deep links
        // without having to verify them with Apple/Google
        val devUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/reset-password")
            .scheme("shelflife")
            .queryParam("token", token)
            .build()
            .toUriString()

        val htmlContent = templateService.processTemplate(
            templateName = "emails/reset-password",
            variables = mapOf(
                "username" to username,
                "resetPasswordUrl" to resetPasswordUrl,
                "devResetPasswordUrl" to devUrl,
                "expiresInMinutes" to expiresIn.toMinutes()
            )
        )

        sendHtmlEmail(to = email, subject = "Reset your ShelfLife password", html = htmlContent)
    }

    private fun sendHtmlEmail(
        to: String,
        subject: String,
        html: String
    ) {
        val message = javaMailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setFrom(emailFrom)
            setTo(to)
            setSubject(subject)
            setText(html, true)
        }

        try {
            javaMailSender.send(message)
        } catch (e: MailException) {
            logger.error("Could not send email", e)
        }
    }
}