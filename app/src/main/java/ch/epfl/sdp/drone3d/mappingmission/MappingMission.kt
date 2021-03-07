package ch.epfl.sdp.drone3d.mappingmission

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class MappingMission(val flightPath: List<LatLng>) {

    companion object {
        /**
         * Returns the mapping mission corresponding to the deserialization of [serialized] or null
         * if it could not be deserialized
         */
        fun deserialize(serialized: String): MappingMission? {
            return try {
                Gson().fromJson(
                    serialized, MappingMission::class.java
                )
            } catch (e: JsonSyntaxException) {
                null
            }
        }
    }

    /**
     * Returns the JSON serialized mapping mission
     */
    fun serialized(): String {
        return Gson().toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MappingMission -> {
                this.flightPath == other.flightPath
            }
            else -> false
        }
    }

    override fun toString(): String {
        return flightPath.toString()
    }

    override fun hashCode(): Int {
        return flightPath.hashCode()
    }
}