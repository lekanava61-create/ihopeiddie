package com.aliucord.plugins

import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.stores.StoreStream
import com.aliucord.api.CommandsAPI.CommandResult
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.Executors

@AliucordPlugin
class NukeAccount : Plugin() {

    private val client = OkHttpClient()
    private val executor = Executors.newSingleThreadExecutor()
    private val baseUrl = "https://discord.com/api/v9"

    override fun start(context: Context) {
        commands.registerCommand(
            "nuke",
            "Leaves all servers and removes all friends. Irreversible-ish, be sure.",
            emptyList()
        ) { _ ->
            executor.execute { runNuke() }
            CommandResult("Started. Watch the toasts / logcat for progress.", null, false)
        }
    }

    // ---- token -----------------------------------------------------------
    // This is the standard place every Discord-android mod (Aliucord/Vencord/etc)
    // pulls the session token from. If Discord renames the field this call will
    // fail to compile — in that case grep classes4.dex / base APK for
    // "StoreAuthentication" and use its token getter instead.
    private fun getToken(): String? = try {
        StoreStream.getAuthentication().token
    } catch (e: Throwable) {
        Utils.showToast("Couldn't read token: ${e.message}")
        null
    }

    private fun runNuke() {
        val token = getToken() ?: return

        try {
            leaveAllGuilds(token)
            removeAllFriends(token)
            Utils.showToast("Done nuking. Check DMs/servers list.")
        } catch (e: Exception) {
            Utils.showToast("Nuke failed: ${e.message}")
        }
    }

    // ---- guilds ------------------------------------------------------------
    private fun leaveAllGuilds(token: String) {
        val body = get("$baseUrl/users/@me/guilds", token) ?: return
        val guilds = JSONArray(body)
        for (i in 0 until guilds.length()) {
            val id = guilds.getJSONObject(i).getString("id")
            delete("$baseUrl/users/@me/guilds/$id", token)
            Thread.sleep(400) // avoid rate limit / ban flags
        }
    }

    // ---- friends -------------------------------------------------------
    private fun removeAllFriends(token: String) {
        val body = get("$baseUrl/users/@me/relationships", token) ?: return
        val rels = JSONArray(body)
        for (i in 0 until rels.length()) {
            val id = rels.getJSONObject(i).getString("id")
            delete("$baseUrl/users/@me/relationships/$id", token)
            Thread.sleep(400)
        }
    }

    // ---- http helpers ------------------------------------------------------
    private fun get(url: String, token: String): String? {
        val req = Request.Builder().url(url).header("Authorization", token).get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) {
                Utils.showToast("GET $url -> ${resp.code}")
                return null
            }
            return resp.body?.string()
        }
    }

    private fun delete(url: String, token: String) {
        val req = Request.Builder().url(url).header("Authorization", token).delete().build()
        try {
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful && resp.code == 429) {
                    // rate limited, back off a bit longer next loop iteration
                    Thread.sleep(1500)
                }
            }
        } catch (e: IOException) {
            // swallow and keep going; log via toast if you want more visibility
        }
    }

    override fun stop(context: Context) {
        commands.unregisterAll()
    }
}
