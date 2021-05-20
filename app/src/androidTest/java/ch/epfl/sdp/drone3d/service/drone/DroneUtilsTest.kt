/*
 * Copyright (C) 2021  Drone3D-Team
 * The license can be found in LICENSE at root of the repository
 */
/*
 * This class was taken from the project Fly2Find and adapted for our project
 */

package ch.epfl.sdp.drone3d.service.drone

import ch.epfl.sdp.drone3d.service.impl.drone.DroneUtils
import com.mapbox.mapboxsdk.geometry.LatLng
import io.mavsdk.mission.Mission
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.math.PI
import kotlin.random.Random.Default.nextBoolean
import kotlin.random.Random.Default.nextDouble

class DroneUtilsTest {

    @Before
    fun before() {
        DroneInstanceMock.setupDefaultMocks()
    }

    @Test
    fun generateMissionItemTest() {
        val n = 100
        repeat(n) {
            val randomLatitude = nextDouble(-90.0, 90.0)
            val randomLongitude = nextDouble(0.0, 180.0)
            val randomPitch =  nextDouble(0.0, 60.0/(2*PI)).toFloat()
            val cameraAction = Mission.MissionItem.CameraAction.TAKE_PHOTO
            val mission = Mission.MissionItem(
                randomLatitude,
                randomLongitude,
                10f,
                10f,
                false, randomPitch, Float.NaN,
                cameraAction, Float.NaN,
                1.0)
            val expectedMission = DroneUtils.generateMissionItem(randomLatitude, randomLongitude, 10f,randomPitch)
            Assert.assertTrue(missionEquality(expectedMission, mission))
        }
    }

    @Test
    fun makeDroneMissionTest() {
        val positions = arrayListOf(
            LatLng(47.398039859999997, 8.5455725400000002),
            LatLng(47.398036222362471, 8.5450146439425509),
            LatLng(47.397825620791885, 8.5450092830163271),
            LatLng(47.397832880000003, 8.5455939999999995)
        )

        val altitude = 10f

        val pitch = 30/(2*PI).toFloat()

        val expectedMissionItems = positions.map { pos ->
            DroneUtils.generateMissionItem(pos.latitude, pos.longitude, altitude,pitch)
        }

        val missionPlan = DroneUtils.makeDroneMission(positions, altitude,pitch)

        expectedMissionItems.zip(missionPlan.missionItems).forEach { (expected, observed) ->
            Assert.assertTrue(missionEquality(expected, observed))
        }
    }

    private fun missionEquality(m1: Mission.MissionItem, m2: Mission.MissionItem): Boolean {
        val lat = m1.latitudeDeg.equals(m2.latitudeDeg)
        val lon = m1.longitudeDeg.equals(m2.longitudeDeg)
        val alt = m1.relativeAltitudeM.equals(m2.relativeAltitudeM)
        val spe = m1.speedMS.equals(m2.speedMS)
        val fly = m1.isFlyThrough == m2.isFlyThrough
        val gim = m1.gimbalPitchDeg.equals(m2.gimbalPitchDeg)
        val yaw = m1.gimbalPitchDeg.equals(m2.gimbalPitchDeg)
        val cam = m1.cameraAction == m2.cameraAction
        val loi = m1.loiterTimeS.equals(m2.loiterTimeS)
        val gcpi = m1.cameraPhotoIntervalS.equals(m2.cameraPhotoIntervalS)
        return lat and lon and alt and spe and fly and gim and yaw and cam and loi and gcpi
    }
}