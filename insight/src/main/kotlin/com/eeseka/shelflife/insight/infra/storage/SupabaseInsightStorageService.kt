package com.eeseka.shelflife.insight.infra.storage

import com.eeseka.shelflife.insight.domain.exception.StorageException
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class SupabaseInsightStorageService(
    private val supabaseRestClient: RestClient
) {
    companion object {
        private const val BUCKET_NAME = "pantry_images"
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
}