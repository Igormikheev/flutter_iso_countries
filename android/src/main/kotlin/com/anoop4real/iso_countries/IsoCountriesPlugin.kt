package com.anoop4real.iso_countries

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class IsoCountriesPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {

    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, "com.anoop4real.iso_countries")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }

            "getISOCountries" -> {
                result.success(CountryDataStore.getIsoCountries())
            }

            "getISOCountriesForLocale" -> {
                val args = call.arguments as? Map<String, String>
                val identifier = args?.get("locale_identifier") ?: "en_US"
                result.success(CountryDataStore.getIsoCountries(identifier))
            }

            "getCountryForCountryCodeWithLocaleIdentifier" -> {
                val args = call.arguments as? Map<String, String>
                val identifier = args?.get("locale_identifier") ?: ""
                val code = args?.get("countryCode") ?: ""
                result.success(CountryDataStore.getCountryForCountryCode(code, identifier))
            }

            else -> {
                result.notImplemented()
            }
        }
    }
}


class CountryDataStore private constructor() {

    companion object {

        fun getIsoCountries(localeIdentifier: String = "en-US"): ArrayList<HashMap<String, String>> {
            var countriesList = arrayListOf<HashMap<String, String>>()
            for (countryCode in Locale.getISOCountries()) {
                // If no locale is passed, then use "en_US"
                val locale = Locale(localeIdentifier, countryCode)
                var countryName: String? = locale.getDisplayCountry(Locale.forLanguageTag(localeIdentifier))
                if (countryName == null) {
                    countryName = "UnIdentified"
                }
                val simpleCountry = hashMapOf("name" to countryName, "countryCode" to countryCode)
                countriesList.add(simpleCountry)
            }
            countriesList = ArrayList(countriesList.sortedWith(compareBy { it["name"] }))
            return countriesList
        }

        // Get a country name from code
        fun getCountryForCountryCode(code: String, localeIdentifier: String = ""): HashMap<String, String> {
            if (code.isEmpty() || code.length > 2) {
                return hashMapOf<String, String>()
            }
            val locale = Locale(localeIdentifier, code)
            val countryName: String? = locale.getDisplayCountry(Locale.forLanguageTag(localeIdentifier))
            if (countryName == null) {
                return hashMapOf<String, String>()
            }
            val simpleCountry = hashMapOf("name" to countryName, "countryCode" to code)
            return simpleCountry
        }
    }
}

