package org.opencovidtrace.octrace.storage

import android.location.Location
import com.google.gson.Gson
import org.opencovidtrace.octrace.storage.KeyManager.fromJson
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object LocationBordersManager : PreferencesHolder("location-borders") {

    private const val BORDERS_KEY = "borders"

    fun getLocationBorders(): HashMap<Int, LocationBorder> {
        val storedHashMapString = KeyManager.getString(BORDERS_KEY)
        (Gson().fromJson(storedHashMapString) as? HashMap<Int, LocationBorder>)?.let {
            return it
        } ?: kotlin.run { return hashMapOf() }
    }

    fun setLocationBorders(newValue: HashMap<Int, LocationBorder>) {
        val hashMapString = Gson().toJson(newValue)
        KeyManager.setString(BORDERS_KEY, hashMapString)
    }


    data class LocationBorder(
        var minLat: Double,
        var minLng: Double,
        var maxLat: Double,
        var maxLng: Double
    ) {
        companion object {
            const val maxLatValue = 90.0
            const val maxLngValue = 180.0
            private const val minDiff = 0.1 // ~ 10km
        }


        constructor(location: Location) : this(
            location.latitude,
            location.longitude,
            location.latitude,
            location.longitude
        )

        fun update(location: Location) {
            minLat = min(minLat, location.latitude)
            minLng = min(minLng, location.longitude)
            maxLat = max(maxLat, location.latitude)
            maxLng = max(maxLng, location.longitude)
        }

        fun extend(other: LocationBorder): LocationBorder {
            return LocationBorder(
                minLat = min(minLat, other.minLat),
                minLng = min(minLng, other.minLng),
                maxLat = max(maxLat, other.maxLat),
                maxLng = max(maxLng, other.maxLng)
            )
        }

        fun secure() {
            if (minLat - maxLat < minDiff) {
                minLat -= random(minDiff / 2, minDiff)
                maxLat += random(minDiff / 2, minDiff)

                adjustLatLimits()

                println("Extended latitude to $minLat - $maxLat")
            }

            if (minLng - maxLng < minDiff) {
                minLng -= random(minDiff / 2, minDiff)
                maxLng += random(minDiff / 2, minDiff)

                adjustLngLimits()

                print("Extended longitude to $minLng - $maxLng")
            }
        }

        private fun adjustLatLimits() {
            if (minLat < -maxLatValue) {
                minLat += maxLatValue * 2
            }

            if (maxLat > maxLatValue) {
                maxLat -= maxLatValue * 2
            }
        }

        private fun adjustLngLimits() {
            if (minLng < -maxLngValue) {
                minLng += maxLngValue * 2
            }

            if (maxLng > maxLngValue) {
                maxLng -= maxLngValue * 2
            }
        }

        private fun random(leftLimit: Double, rightLimit: Double): Double {
            return leftLimit + Random.nextDouble() * (rightLimit - leftLimit)
        }
    }


}