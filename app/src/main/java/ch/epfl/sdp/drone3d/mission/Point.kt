package ch.epfl.sdp.drone3d.mission

import kotlin.math.abs
import kotlin.math.sqrt

data class Point (val x:Double,val y:Double) {

    companion object{
        /**
         * Computes the middle point between two points
         */
        fun middle(a:Point,b:Point) = Point((a.x+b.x)/2, (a.y+b.y)/2)

        /**
         * Computes the distance between two points
         */
        fun distance(a:Point,b:Point) = Vector(a,b).norm()
    }

    operator fun plus(vector: Vector) = Point(this.x+vector.x, this.y+vector.y)
    operator fun minus(vector: Vector) = Point(this.x-vector.x, this.y-vector.y)


    /**
     * Unit vector representing the direction from this to a
     */
    fun direction(other:Point) = Vector(this,other).normalized()
}