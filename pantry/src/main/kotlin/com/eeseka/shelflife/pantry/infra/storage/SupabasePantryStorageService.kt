package com.eeseka.shelflife.pantry.infra.storage

import com.eeseka.shelflife.common.domain.type.PantryItemId
import com.eeseka.shelflife.common.domain.type.UserId
import com.eeseka.shelflife.pantry.domain.exception.InvalidPantryImageException
import com.eeseka.shelflife.pantry.domain.exception.StorageException
import com.eeseka.shelflife.pantry.domain.models.PantryImageUploadCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.Instant
import java.util.*

@Service
class SupabasePantryStorageService(
    @param:Value("\${supabase.url}") private val supabaseUrl: String,
    private val supabaseRestClient: RestClient
) {
    companion object {
        private const val BUCKET_NAME = "pantry_images"

        private val allowedMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/jpg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp",
        )
    }

    fun generateSignedUploadUrl(
        userId: UserId,
        itemId: PantryItemId,
        mimeType: String
    ): PantryImageUploadCredentials {
        val extension = allowedMimeTypes[mimeType]
            ?: throw InvalidPantryImageException("Invalid mime type $mimeType")

        val fileName = "${itemId}_${UUID.randomUUID()}.$extension"
        val path = "$BUCKET_NAME/${userId}/$fileName"

        val publicUrl = "$supabaseUrl/storage/v1/object/public/$path"

        return PantryImageUploadCredentials(
            uploadUrl = createSignedUrl(
                path = path,
                expiresInSeconds = 300
            ),
            publicUrl = publicUrl,
            headers = mapOf(
                "Content-Type" to mimeType
            ),
            expiresAt = Instant.now().plusSeconds(300)
        )
    }

    fun deleteFile(url: String) {
        val path = if (url.contains("/object/public/")) {
            url.substringAfter("/object/public/")
        } else throw StorageException("Invalid file URL format")

        val deleteUrl = "/storage/v1/object/$path"

        val response = supabaseRestClient
            .delete()
            .uri(deleteUrl)
            .retrieve()
            .toBodilessEntity()

        if (response.statusCode.isError) {
            throw StorageException("Unable to delete file: ${response.statusCode.value()}")
        }
    }

    fun deleteManyFiles(urls: List<String>) {
        if (urls.isEmpty()) return

        val paths = urls.mapNotNull { url ->
            if (url.contains("/object/public/$BUCKET_NAME/")) {
                url.substringAfter("/object/public/$BUCKET_NAME/")
            } else {
                null
            }
        }

        if (paths.isEmpty()) return

        val payload = mapOf("prefixes" to paths)

        val response = supabaseRestClient
            .method(HttpMethod.DELETE)
            .uri("/storage/v1/object/$BUCKET_NAME")
            .header("Content-Type", "application/json")
            .body(payload)
            .retrieve()
            .toBodilessEntity()

        if (response.statusCode.isError) {
            throw StorageException("Batch delete failed with status: ${response.statusCode}")
        }
    }

    private fun createSignedUrl(path: String, expiresInSeconds: Int): String {
        val json = """
            { "expiresIn": $expiresInSeconds }
        """.trimIndent()

        val response = supabaseRestClient
            .post()
            .uri("/storage/v1/object/upload/sign/$path")
            .header("Content-Type", "application/json")
            .body(json)
            .retrieve()
            .body<SignedUploadResponse>()
            ?: throw StorageException("Failed to create signed URL")

        return "$supabaseUrl/storage/v1${response.url}"
    }

    private data class SignedUploadResponse(val url: String)
}