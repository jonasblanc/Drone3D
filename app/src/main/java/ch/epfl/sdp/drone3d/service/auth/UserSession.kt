/*
 *
 *  * Copyright (C) 2021  Drone3D-Team
 *  * The license can be found in LICENSE at root of the repository
 *  
 */

package ch.epfl.sdp.drone3d.service.auth

import com.google.firebase.auth.FirebaseUser

data class UserSession(val user: FirebaseUser)