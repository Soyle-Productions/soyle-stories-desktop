package com.soyle.stories.location.usecases.getLocationDetails

import com.soyle.stories.location.LocationException
import java.util.*

interface GetLocationDetails {

	suspend operator fun invoke(locationId: UUID, output: OutputPort)

	class ResponseModel(val locationId: UUID, val locationName: String, val locationDescription: String)

	interface OutputPort {
		fun receiveGetLocationDetailsFailure(failure: LocationException)
		fun receiveGetLocationDetailsResponse(response: ResponseModel)
	}

}